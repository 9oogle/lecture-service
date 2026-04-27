package com.goggles.lecture_service.domain.lecture;

import com.goggles.common.domain.BaseAudit;
import com.goggles.lecture_service.domain.lecture.exception.InvalidSortOrderException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "p_chapter",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_chapter_lecture_sort_order",
            columnNames = {"lecture_id", "sort_order"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter extends BaseAudit {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lecture_id", nullable = false)
  private Lecture lecture;

  @Embedded private ChapterContent content;

  @Column(nullable = false)
  private int sortOrder;

  @Embedded private ChapterDuration duration;

  // Lecture.addChapter()를 통해서만 생성 가능 (패키지 접근)
  static Chapter create(
      Lecture lecture, ChapterContent content, int sortOrder, ChapterDuration duration) {
    validateSortOrder(sortOrder);
    return new Chapter(lecture, content, sortOrder, duration);
  }

  private Chapter(
      Lecture lecture, ChapterContent content, int sortOrder, ChapterDuration duration) {
    this.lecture = Objects.requireNonNull(lecture, "lecture는 필수입니다.");
    this.content = Objects.requireNonNull(content, "content는 필수입니다.");
    this.sortOrder = sortOrder;
    this.duration = Objects.requireNonNull(duration, "duration은 필수입니다.");
  }

  public UUID getLectureId() {
    return lecture.getId();
  }

  void updateContent(ChapterContent newContent) {
    this.content = Objects.requireNonNull(newContent, "content는 필수입니다.");
  }

  void updateSortOrder(int newSortOrder) {
    validateSortOrder(newSortOrder);
    this.sortOrder = newSortOrder;
  }

  void updateDuration(ChapterDuration newDuration) {
    this.duration = Objects.requireNonNull(newDuration, "duration은 필수입니다.");
  }

  private static void validateSortOrder(int sortOrder) {
    if (sortOrder < 1) {
      throw new InvalidSortOrderException(sortOrder);
    }
  }
}
