package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public class InvalidLectureStatusException extends BadRequestException {

  public InvalidLectureStatusException(UUID lectureId, LectureStatus status) {
    super("요청한 작업을 수행할 수 없는 강의 상태입니다. lectureId=" + lectureId + ", status=" + status);
  }
}
