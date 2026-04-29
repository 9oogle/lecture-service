package com.goggles.lecture_service.domain.lecture;

import com.goggles.common.domain.BaseAudit;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.ChapterNotFoundException;
import com.goggles.lecture_service.domain.lecture.exception.DuplicateSortOrderException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidCategoryException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureStatusException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidRejectionReasonException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_lecture")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseAudit {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id = UUID.randomUUID();

  @Embedded private Instructor instructor;

  @Column(nullable = false, length = 50)
  private String category;

  @Embedded private LectureContent content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DurationPolicy durationPolicy;

  @Embedded private Money price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LectureStatus status;

  @Column(columnDefinition = "TEXT")
  private String rejectionReason;

  @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Chapter> chapters = new ArrayList<>();

  // 정적 팩토리 메서드
  public static Lecture create(
      UUID instructorId,
      String instructorName,
      String category,
      String title,
      String subtitle,
      String description,
      DurationPolicy durationPolicy,
      Long priceAmount) {

    validateCategory(category);

    return new Lecture(
        new Instructor(instructorId, instructorName),
        category,
        new LectureContent(title, subtitle, description),
        durationPolicy,
        Money.of(priceAmount));
  }

  private Lecture(
      Instructor instructor,
      String category,
      LectureContent content,
      DurationPolicy durationPolicy,
      Money price) {
    this.instructor = instructor;
    this.category = category;
    this.content = content;
    this.durationPolicy = durationPolicy != null ? durationPolicy : DurationPolicy.DAYS_365;
    this.price = price;
    this.status = LectureStatus.DRAFT;
  }

  // 도메인 메서드 (챕터 관리)
  public UUID addChapter(String title, String content, int sortOrder, int durationSeconds) {
    validateDraftStatus();
    validateDuplicateSortOrder(sortOrder);

    ChapterContent chapterContent = new ChapterContent(title, content);
    ChapterDuration chapterDuration = new ChapterDuration(durationSeconds);

    Chapter chapter = Chapter.create(this, chapterContent, sortOrder, chapterDuration);
    chapters.add(chapter);

    return chapter.getId();
  }

  public void removeChapter(UUID chapterId) {
    validateDraftStatus();
    boolean removed = chapters.removeIf(chapter -> chapter.getId().equals(chapterId));
    if (!removed) {
      throw new ChapterNotFoundException(chapterId);
    }
  }

  // 챕터 수정 (DRAFT 상태에서만, 순서는 변경하지 않음)
  public void updateChapter(UUID chapterId, String title, String content, int durationSeconds) {
    validateDraftStatus();

    Chapter target = findChapterById(chapterId);

    target.updateContent(new ChapterContent(title, content));
    target.updateDuration(new ChapterDuration(durationSeconds));
  }

  // 챕터 일괄 순서 변경 (DRAFT 상태에서만) 변경 후 sortOrder가 강의 전체 챕터 안에서 중복되면 throw
  public void reorderChapters(Map<UUID, Integer> chapterOrders) {
    validateDraftStatus();

    Map<UUID, Chapter> chapterMap =
        chapters.stream().collect(Collectors.toMap(Chapter::getId, chapter -> chapter));

    // 1. 모든 chapterId 이 강의의 챕터인지 검증
    for (UUID chapterId : chapterOrders.keySet()) {
      if (!chapterMap.containsKey(chapterId)) {
        throw new ChapterNotFoundException(chapterId);
      }
    }

    // 2. 변경 후 최종 sortOrder 중복 확인
    Map<UUID, Integer> finalSortOrders =
        chapters.stream().collect(Collectors.toMap(Chapter::getId, Chapter::getSortOrder));
    chapterOrders.forEach(finalSortOrders::put);

    Integer duplicatedSortOrder =
        finalSortOrders.values().stream()
            .collect(Collectors.groupingBy(sortOrder -> sortOrder, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

    if (duplicatedSortOrder != null) {
      throw new DuplicateSortOrderException(duplicatedSortOrder);
    }

    // 3) 적용
    chapterOrders.forEach(
        (chapterId, sortOrder) -> chapterMap.get(chapterId).updateSortOrder(sortOrder));
  }

  public List<ChapterView> getChapterViews() {
    return chapters.stream()
        .map(
            chapter ->
                new ChapterView(
                    chapter.getId(),
                    chapter.getContent().getTitle(),
                    chapter.getContent().getContent(),
                    chapter.getSortOrder(),
                    chapter.getDuration().getSeconds()))
        .toList();
  }

  public int getChapterCount() {
    return chapters.size();
  }

  // 도메인 메서드 (정보 수정)
  public void updateMetadata(
      String title,
      String subtitle,
      String description,
      String category,
      DurationPolicy durationPolicy,
      Long priceAmount) {
    validateDraftStatus();
    validateCategory(category);

    this.content = new LectureContent(title, subtitle, description);
    this.category = category;
    this.durationPolicy = durationPolicy != null ? durationPolicy : DurationPolicy.DAYS_365;
    this.price = Money.of(priceAmount);
  }

  // 도메인 메서드 (상태 전이)
  public void submitForReview() {
    if (this.status != LectureStatus.DRAFT) {
      throw new InvalidLectureStatusException(id, status);
    }
    if (chapters.isEmpty()) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_CHAPTER_REQUIRED);
    }
    this.status = LectureStatus.PENDING_REVIEW;
    // 리뷰 재신청 시 이전 이유 삭제
    this.rejectionReason = null;
  }

  // 도메인 메서드 (삭제)
  public void delete(UUID deletedBy) {
    if (deletedBy == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    validateDraftStatus();
    softDelete(deletedBy);
  }

  public void approve() {
    if (this.status != LectureStatus.PENDING_REVIEW) {
      throw new InvalidLectureStatusException(id, status);
    }
    this.status = LectureStatus.PUBLISHED;
    this.rejectionReason = null;
  }

  public void reject(String reason) {
    if (this.status != LectureStatus.PENDING_REVIEW) {
      throw new InvalidLectureStatusException(id, status);
    }
    if (reason == null || reason.isBlank()) {
      throw new InvalidRejectionReasonException();
    }
    this.status = LectureStatus.DRAFT;
    this.rejectionReason = reason;
  }

  public void hide() {
    if (this.status != LectureStatus.PUBLISHED) {
      throw new InvalidLectureStatusException(id, status);
    }
    this.status = LectureStatus.HIDDEN;
  }

  // 도메인 메서드 (소유자 확인)
  public boolean isOwnedBy(UUID userId) {
    if (userId == null || this.instructor == null) {
      return false;
    }
    return userId.equals(this.instructor.getInstructorId());
  }

  // 주문 가능 여부
  public boolean isOrderable() {
    return this.status == LectureStatus.PUBLISHED;
  }

  // 내부 검증 및 편의 메서드
  private void validateDraftStatus() {
    if (this.status != LectureStatus.DRAFT) {
      throw new InvalidLectureStatusException(id, status);
    }
  }

  private void validateDuplicateSortOrder(int sortOrder) {
    boolean isDuplicate = chapters.stream().anyMatch(c -> c.getSortOrder() == sortOrder);
    if (isDuplicate) {
      throw new DuplicateSortOrderException(sortOrder);
    }
  }

  private Chapter findChapterById(UUID chapterId) {
    return chapters.stream()
        .filter(c -> c.getId().equals(chapterId))
        .findFirst()
        .orElseThrow(() -> new ChapterNotFoundException(chapterId));
  }

  private static void validateCategory(String category) {
    if (category == null || category.isBlank()) {
      throw new InvalidCategoryException();
    }
  }
}
