package com.goggles.lecture_service.application.lecture.command.service;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.exception.LectureAccessDeniedException;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureCommandServiceImpl implements LectureCommandService {

  private static final String ROLE_MASTER = "MASTER";
  private final LectureRepository lectureRepository;

  @Override
  public LectureCreateResult createLecture(LectureCreateCommand command) {
    // Todo: APPROVED 상태 강사만 생성 가능 조건 추가
    Lecture lecture =
        Lecture.create(
            command.instructorId(),
            command.instructorName(),
            command.category(),
            command.title(),
            command.subtitle(),
            command.description(),
            command.durationPolicy(),
            command.price());

    Lecture saved = lectureRepository.save(lecture);
    return LectureCreateResult.from(saved);
  }

  @Override
  @Transactional
  public ChapterCreateResult createChapter(ChapterCreateCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    UUID chapterId =
        lecture.addChapter(
            command.title(), command.content(), command.sortOrder(), command.durationSeconds());

    return ChapterCreateResult.from(lecture.getId(), chapterId);
  }

  @Override
  public LectureUpdateResult updateLecture(LectureUpdateCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    validateLectureAccess(lecture, command.actorId(), command.actorRole());

    lecture.updateMetadata(
        command.title(),
        command.subtitle(),
        command.description(),
        command.category(),
        command.durationPolicy(),
        command.price());

    return LectureUpdateResult.from(lecture);
  }

  @Override
  public LectureDeleteResult deleteLecture(LectureDeleteCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    validateLectureAccess(lecture, command.actorId(), command.actorRole());

    lecture.delete(command.actorId());

    return LectureDeleteResult.from(command.lectureId());
  }

  @Override
  public ChapterUpdateResult updateChapter(ChapterUpdateCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    validateLectureAccess(lecture, command.actorId(), command.actorRole());

    lecture.updateChapter(
        command.chapterId(), command.title(), command.content(), command.durationSeconds());

    return ChapterUpdateResult.from(lecture.getId(), command.chapterId());
  }

  @Override
  public ChapterDeleteResult deleteChapter(ChapterDeleteCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    validateLectureAccess(lecture, command.actorId(), command.actorRole());

    lecture.removeChapter(command.chapterId());

    return ChapterDeleteResult.from(command.lectureId(), command.chapterId());
  }

  @Override
  public ChapterReorderResult reorderChapters(ChapterReorderCommand command) {
    Lecture lecture =
        lectureRepository
            .findById(command.lectureId())
            .orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

    validateLectureAccess(lecture, command.actorId(), command.actorRole());

    Map<UUID, Integer> chapterOrders =
        command.orders().stream()
            .collect(
                Collectors.toMap(
                    ChapterReorderCommand.ChapterOrderCommand::chapterId,
                    ChapterReorderCommand.ChapterOrderCommand::sortOrder));

    lecture.reorderChapters(chapterOrders);

    return ChapterReorderResult.from(command.lectureId());
  }

  // 강의 소유자(강사) 또는 관리자(MASTER)만 통과
  private void validateLectureAccess(Lecture lecture, UUID actorId, String actorRole) {
    if (lecture.isOwnedBy(actorId) || isAdmin(actorRole)) {
      return;
    }
    throw new LectureAccessDeniedException();
  }

  private boolean isAdmin(String actorRole) {
    // TODO(#로그인): user-service 로그인 API 연동 후 공통 UserRole enum 으로 교체
    //   위에 상수도 제거
    // return actorRole == UserRole.MASTER;
    return ROLE_MASTER.equals(actorRole);
  }
}
