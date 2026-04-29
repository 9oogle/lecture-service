package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;
import com.goggles.lecture_service.application.lecture.command.service.LectureCommandServiceImpl;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.DuplicateSortOrderException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidCategoryException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureStatusException;
import com.goggles.lecture_service.domain.lecture.exception.LectureAccessDeniedException;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureCommandServiceImplTest {

  @InjectMocks private LectureCommandServiceImpl lectureCommandService;

  @Mock private LectureRepository lectureRepository;

  private UUID instructorId;
  private LectureCreateCommand command;

  @BeforeEach
  void setUp() {
    instructorId = UUID.randomUUID();
    command =
        new LectureCreateCommand(
            instructorId,
            "강사이름",
            "IT",
            "자바 강의",
            "자바 기초부터 심화까지",
            "강의 설명입니다.",
            DurationPolicy.DAYS_365,
            50000L);
  }

  @Nested
  @DisplayName("강의 생성")
  class CreateLecture {

    @Test
    @DisplayName("성공: DRAFT 상태로 강의가 생성된다")
    void createLecture_success() {
      // given
      when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> inv.getArgument(0));

      // when
      LectureCreateResult result = lectureCommandService.createLecture(command);

      // then
      assertThat(result).isNotNull();
      assertThat(result.id()).isNotNull();
      assertThat(result.status()).isEqualTo(LectureStatus.DRAFT);
      verify(lectureRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    @DisplayName("성공: durationPolicy 가 null 이면 도메인 기본값(DAYS_365)이 적용된다")
    void createLecture_nullDurationPolicy_defaultsToDays365() {
      // given
      LectureCreateCommand nullPolicyCommand =
          new LectureCreateCommand(instructorId, "강사이름", "IT", "자바 강의", "부제", "설명", null, 50000L);
      when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> inv.getArgument(0));

      // when
      lectureCommandService.createLecture(nullPolicyCommand);

      // then - 도메인의 기본값 정책이 적용되었는지는 도메인 테스트 영역,
      // 여기서는 호출이 성공했는지만 검증
      verify(lectureRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    @DisplayName("실패: 카테고리가 비어있으면 예외 발생")
    void createLecture_emptyCategory_throws() {
      // given
      LectureCreateCommand invalidCommand =
          new LectureCreateCommand(
              instructorId,
              "강사이름",
              "", // 빈 카테고리
              "자바 강의",
              "부제",
              "설명",
              DurationPolicy.DAYS_365,
              50000L);

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createLecture(invalidCommand))
          .isInstanceOf(InvalidCategoryException.class);
      verify(lectureRepository, times(0)).save(any(Lecture.class));
    }

    @Test
    @DisplayName("실패: 가격이 음수이면 예외 발생")
    void createLecture_negativePrice_throws() {
      // given
      LectureCreateCommand invalidCommand =
          new LectureCreateCommand(
              instructorId, "강사이름", "IT", "자바 강의", "부제", "설명", DurationPolicy.DAYS_365, -1000L);

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createLecture(invalidCommand))
          .isInstanceOf(InvalidLectureFieldException.class);
      verify(lectureRepository, times(0)).save(any(Lecture.class));
    }

    @Test
    @DisplayName("실패: 제목이 비어있으면 예외 발생")
    void createLecture_emptyTitle_throws() {
      // given
      LectureCreateCommand invalidCommand =
          new LectureCreateCommand(
              instructorId, "강사이름", "IT", "", "부제", "설명", DurationPolicy.DAYS_365, 50000L);

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createLecture(invalidCommand))
          .isInstanceOf(InvalidLectureFieldException.class);
      verify(lectureRepository, times(0)).save(any(Lecture.class));
    }
  } // 강의 생성 끝

  @Nested
  @DisplayName("챕터 생성")
  class CreateChapter {

    private UUID lectureId;
    private Lecture lecture;

    @BeforeEach
    void setUp() {
      lecture =
          Lecture.create(
              instructorId, "강사이름", "IT", "자바 강의", "부제", "설명", DurationPolicy.DAYS_365, 50000L);

      lectureId = lecture.getId();
    }

    @Test
    @DisplayName("성공: DRAFT 강의에 챕터를 추가한다")
    void createChapter_success() {
      // given
      ChapterCreateCommand chapterCommand =
          new ChapterCreateCommand(lectureId, "1강", "자바 소개", 1, 600);

      when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));

      // when
      ChapterCreateResult result = lectureCommandService.createChapter(chapterCommand);

      // then
      assertThat(result).isNotNull();
      assertThat(result.lectureId()).isEqualTo(lectureId);
      assertThat(result.chapterId()).isNotNull();

      verify(lectureRepository).findById(lectureId);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 강의이면 예외 발생")
    void createChapter_lectureNotFound_throws() {
      // given
      UUID notFoundId = UUID.randomUUID();
      ChapterCreateCommand chapterCommand =
          new ChapterCreateCommand(notFoundId, "1강", "자바 소개", 1, 600);

      when(lectureRepository.findById(notFoundId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createChapter(chapterCommand))
          .isInstanceOf(LectureNotFoundException.class);

      verify(lectureRepository).findById(notFoundId);
      verify(lectureRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: DRAFT 상태가 아닌 강의에 챕터 추가 시 예외 발생")
    void createChapter_notDraftStatus_throws() {
      // given - PENDING_REVIEW 상태로 만들기 위해 챕터 1개 추가 후 submitForReview
      lecture.addChapter("dummy", "dummy", 1, 600);
      lecture.submitForReview();

      ChapterCreateCommand chapterCommand = new ChapterCreateCommand(lectureId, "2강", "내용", 2, 600);
      when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createChapter(chapterCommand))
          .isInstanceOf(InvalidLectureStatusException.class);
    }

    @Test
    @DisplayName("실패: sortOrder 가 중복되면 예외 발생")
    void createChapter_duplicateSortOrder_throws() {
      // given - 이미 sortOrder=1인 챕터 추가
      lecture.addChapter("1강", "내용", 1, 600);

      ChapterCreateCommand chapterCommand =
          new ChapterCreateCommand(lectureId, "다른강", "내용", 1, 600);
      when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createChapter(chapterCommand))
          .isInstanceOf(DuplicateSortOrderException.class);
    }

    @Test
    @DisplayName("실패: durationSeconds = 0 이면 예외 발생 (정책 강화)")
    void createChapter_zeroDuration_throws() {
      // given
      ChapterCreateCommand chapterCommand = new ChapterCreateCommand(lectureId, "1강", "내용", 1, 0);
      when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));

      // when & then
      assertThatThrownBy(() -> lectureCommandService.createChapter(chapterCommand))
          .isInstanceOf(InvalidLectureFieldException.class);
    }
  } // 챕터 생성 끝

  @Nested
  @DisplayName("강의 수정")
  class UpdateLecture {

    @Test
    @DisplayName("성공: 강의 소유자(강사)가 DRAFT 상태 강의 수정")
    void updateLecture_byOwner_success() {
      UUID instructorId = UUID.randomUUID();
      Lecture lecture = draftLecture(instructorId);
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      LectureUpdateResult result =
          lectureCommandService.updateLecture(
              new LectureUpdateCommand(
                  lecture.getId(),
                  instructorId,
                  "INSTRUCTOR",
                  "BACKEND",
                  "수정된 제목",
                  "수정된 부제",
                  "수정된 설명",
                  DurationPolicy.DAYS_180,
                  20000L));

      assertThat(result.lectureId()).isEqualTo(lecture.getId());
      assertThat(lecture.getContent().getTitle()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("성공: 관리자(MASTER)가 DRAFT 상태 강의 수정")
    void updateLecture_byMaster_success() {
      Lecture lecture = draftLecture(UUID.randomUUID()); // 다른 강사 소유
      UUID master = UUID.randomUUID();
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      LectureUpdateResult result =
          lectureCommandService.updateLecture(
              new LectureUpdateCommand(
                  lecture.getId(),
                  master,
                  "MASTER",
                  "BACKEND",
                  "수정된 제목",
                  null,
                  null,
                  DurationPolicy.DAYS_365,
                  10000L));

      assertThat(result.lectureId()).isEqualTo(lecture.getId());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 강의 수정")
    void updateLecture_lectureNotFound_throws() {
      UUID lectureId = UUID.randomUUID();
      when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

      assertThatThrownBy(
              () ->
                  lectureCommandService.updateLecture(
                      new LectureUpdateCommand(
                          lectureId,
                          UUID.randomUUID(),
                          "INSTRUCTOR",
                          "BACKEND",
                          "제목",
                          null,
                          null,
                          DurationPolicy.DAYS_365,
                          10000L)))
          .isInstanceOf(LectureNotFoundException.class);
    }

    @Test
    @DisplayName("실패: DRAFT가 아닌 상태에서 수정")
    void updateLecture_notDraft_throws() {
      UUID instructorId = UUID.randomUUID();
      Lecture lecture = publishedLecture(instructorId); // PUBLISHED 상태
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      assertThatThrownBy(
              () ->
                  lectureCommandService.updateLecture(
                      new LectureUpdateCommand(
                          lecture.getId(),
                          instructorId,
                          "INSTRUCTOR",
                          "BACKEND",
                          "제목",
                          null,
                          null,
                          DurationPolicy.DAYS_365,
                          10000L)))
          .isInstanceOf(InvalidLectureStatusException.class);
    }

    @Test
    @DisplayName("실패: 강의 소유자가 아닌 강사가 수정")
    void updateLecture_notOwner_throws() {
      Lecture lecture = draftLecture(UUID.randomUUID()); // 다른 강사
      UUID otherInstructor = UUID.randomUUID();
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      assertThatThrownBy(
              () ->
                  lectureCommandService.updateLecture(
                      new LectureUpdateCommand(
                          lecture.getId(),
                          otherInstructor,
                          "INSTRUCTOR",
                          "BACKEND",
                          "제목",
                          null,
                          null,
                          DurationPolicy.DAYS_365,
                          10000L)))
          .isInstanceOf(LectureAccessDeniedException.class);
    }

    @Test
    @DisplayName("실패: lectureId null Command 예외")
    void updateLecture_nullLectureId_throws() {
      assertThatThrownBy(
              () ->
                  new LectureUpdateCommand(
                      null,
                      UUID.randomUUID(),
                      "INSTRUCTOR",
                      "BACKEND",
                      "제목",
                      null,
                      null,
                      DurationPolicy.DAYS_365,
                      10000L))
          .isInstanceOf(InvalidLectureFieldException.class);
    }

    @Test
    @DisplayName("실패: actorRole blank Command 예외")
    void updateLecture_blankActorRole_throws() {
      assertThatThrownBy(
              () ->
                  new LectureUpdateCommand(
                      UUID.randomUUID(),
                      UUID.randomUUID(),
                      "  ",
                      "BACKEND",
                      "제목",
                      null,
                      null,
                      DurationPolicy.DAYS_365,
                      10000L))
          .isInstanceOf(InvalidLectureFieldException.class);
    }

    @Test
    @DisplayName("실패: price null Command 예외")
    void updateLecture_nullPrice_throws() {
      assertThatThrownBy(
              () ->
                  new LectureUpdateCommand(
                      UUID.randomUUID(),
                      UUID.randomUUID(),
                      "INSTRUCTOR",
                      "BACKEND",
                      "제목",
                      null,
                      null,
                      DurationPolicy.DAYS_365,
                      null))
          .isInstanceOf(InvalidLectureFieldException.class);
    }
  }

  @Nested
  @DisplayName("강의 삭제")
  class DeleteLecture {

    @Test
    @DisplayName("성공: 강의 소유자가 DRAFT 상태 강의 삭제")
    void deleteLecture_byOwner_success() {
      UUID instructorId = UUID.randomUUID();
      Lecture lecture = draftLecture(instructorId);
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      LectureDeleteResult result =
          lectureCommandService.deleteLecture(
              new LectureDeleteCommand(lecture.getId(), instructorId, "INSTRUCTOR"));

      assertThat(result.lectureId()).isEqualTo(lecture.getId());
      assertThat(lecture.getDeletedAt()).isNotNull();
      assertThat(lecture.getDeletedBy()).isEqualTo(instructorId);
    }

    @Test
    @DisplayName("성공: 관리자(MASTER)가 다른 강사의 DRAFT 강의 삭제")
    void deleteLecture_byMaster_success() {
      Lecture lecture = draftLecture(UUID.randomUUID());
      UUID master = UUID.randomUUID();
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      lectureCommandService.deleteLecture(
          new LectureDeleteCommand(lecture.getId(), master, "MASTER"));

      assertThat(lecture.getDeletedBy()).isEqualTo(master);
    }

    @Test
    @DisplayName("실패: DRAFT가 아닌 강의 삭제")
    void deleteLecture_notDraft_throws() {
      UUID instructorId = UUID.randomUUID();
      Lecture lecture = publishedLecture(instructorId);
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      assertThatThrownBy(
              () ->
                  lectureCommandService.deleteLecture(
                      new LectureDeleteCommand(lecture.getId(), instructorId, "INSTRUCTOR")))
          .isInstanceOf(InvalidLectureStatusException.class);
    }

    @Test
    @DisplayName("실패: 소유자도 관리자도 아닌 사용자가 삭제")
    void deleteLecture_notOwnerNotAdmin_throws() {
      Lecture lecture = draftLecture(UUID.randomUUID());
      UUID stranger = UUID.randomUUID();
      when(lectureRepository.findById(lecture.getId())).thenReturn(Optional.of(lecture));

      assertThatThrownBy(
              () ->
                  lectureCommandService.deleteLecture(
                      new LectureDeleteCommand(lecture.getId(), stranger, "STUDENT")))
          .isInstanceOf(LectureAccessDeniedException.class);
    }
  }

  // 헬퍼
  private Lecture draftLecture(UUID instructorId) {
    return Lecture.create(
        instructorId, "강사명", "BACKEND", "스프링 강의", "부제", "설명", DurationPolicy.DAYS_365, 10000L);
  }

  private Lecture publishedLecture(UUID instructorId) {
    Lecture l = draftLecture(instructorId);
    l.addChapter("챕터1", "내용", 1, 600);
    l.submitForReview();
    l.approve();
    return l;
  }
}
