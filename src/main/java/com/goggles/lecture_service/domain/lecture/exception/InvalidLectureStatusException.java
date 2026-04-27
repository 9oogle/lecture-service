package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public class InvalidLectureStatusException extends BadRequestException {

  public InvalidLectureStatusException(UUID lectureId, LectureStatus status) {
    super("수정 가능한 강의 상태가 아닙니다. lectureId=" + lectureId + ", status=" + status);
  }
}
