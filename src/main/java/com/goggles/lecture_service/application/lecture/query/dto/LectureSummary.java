package com.goggles.lecture_service.application.lecture.query.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureSummary(
    UUID id,
    String instructorName,
    String category,
    String title,
    String subtitle,
    Long price,
    DurationPolicy durationPolicy,
    LectureStatus status) {

  public static LectureSummary from(Lecture lecture) {
    return new LectureSummary(
        lecture.getId(),
        lecture.getInstructor().getInstructorName(),
        lecture.getCategory(),
        lecture.getContent().getTitle(),
        lecture.getContent().getSubtitle(),
        lecture.getPrice().getAmount(),
        lecture.getDurationPolicy(),
        lecture.getStatus());
  }
}
