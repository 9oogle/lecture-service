package com.goggles.lecture_service.application.lecture.query.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.List;
import java.util.UUID;

public record LectureDetail(
    UUID id,
    UUID instructorId,
    String instructorName,
    String category,
    String title,
    String subtitle,
    String description,
    Long price,
    DurationPolicy durationPolicy,
    LectureStatus status,
    String rejectionReason,
    List<ChapterDetail> chapters) {

  public static LectureDetail from(Lecture lecture) {
    return new LectureDetail(
        lecture.getId(),
        lecture.getInstructor().getInstructorId(),
        lecture.getInstructor().getInstructorName(),
        lecture.getCategory(),
        lecture.getContent().getTitle(),
        lecture.getContent().getSubtitle(),
        lecture.getContent().getDescription(),
        lecture.getPrice().getAmount(),
        lecture.getDurationPolicy(),
        lecture.getStatus(),
        lecture.getRejectionReason(),
        lecture.getChapterViews().stream().map(ChapterDetail::from).toList());
  }
}
