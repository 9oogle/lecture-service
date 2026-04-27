package com.goggles.lecture_service.domain.lecture;

import com.goggles.common.domain.BaseAudit;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.ChapterNotFoundException;
import com.goggles.lecture_service.domain.lecture.exception.DuplicateSortOrderException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidCategoryException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureStatusException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidRejectionReasonException;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

  public Chapter addChapter(String title, String content, int sortOrder, int durationSeconds) {
    validateDraftStatus();
    validateDuplicateSortOrder(sortOrder);

    ChapterContent chapterContent = new ChapterContent(title, content);
    ChapterDuration chapterDuration = new ChapterDuration(durationSeconds);

    Chapter chapter = Chapter.create(this, chapterContent, sortOrder, chapterDuration);
    chapters.add(chapter);

    return chapter;
  }

  public void removeChapter(UUID chapterId) {
    validateDraftStatus();
    boolean removed = chapters.removeIf(chapter -> chapter.getId().equals(chapterId));
    if (!removed) {
      throw new ChapterNotFoundException(chapterId);
    }
  }

  public void reorderChapter(UUID chapterId, int newSortOrder) {
    validateDraftStatus();
    Chapter target = findChapterById(chapterId);
    if (target.getSortOrder() == newSortOrder) return;
    validateDuplicateSortOrder(newSortOrder);
    target.updateSortOrder(newSortOrder);
  }

  public List<Chapter> getChapters() {
    return Collections.unmodifiableList(chapters);
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
      throw new InvalidLectureStatusException(id, status);
    }
    this.status = LectureStatus.PENDING_REVIEW;
    // 리뷰 재신청 시 이전 이유 삭제
    this.rejectionReason = null;
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
