package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record ChapterReorderCommand(
    UUID lectureId, UUID actorId, String actorRole, List<ChapterOrderCommand> orders) {

  public ChapterReorderCommand {
    if (lectureId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_ID_REQUIRED);
    }
    if (actorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null || actorRole.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
    if (orders == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_REORDER_ITEMS_REQUIRED);
    }
    if (orders.isEmpty()) {
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_REORDER_ITEMS_EMPTY);
    }
    if (orders.stream().anyMatch(order -> order == null)) {
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_REORDER_ITEMS_REQUIRED);
    }

    boolean hasDuplicatedChapterId =
        new HashSet<>(orders.stream().map(ChapterOrderCommand::chapterId).toList()).size()
            != orders.size();
    if (hasDuplicatedChapterId) {
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_ID_DUPLICATED);
    }

    orders = List.copyOf(orders);
  }

  public record ChapterOrderCommand(UUID chapterId, Integer sortOrder) {

    public ChapterOrderCommand {
      if (chapterId == null) {
        throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_ID_REQUIRED);
      }
      if (sortOrder == null) {
        throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_SORT_ORDER_REQUIRED);
      }
      if (sortOrder < 1) {
        throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_INVALID_SORT_ORDER);
      }
    }
  }
}
