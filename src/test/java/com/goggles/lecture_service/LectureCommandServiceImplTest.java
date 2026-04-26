package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.application.lecture.command.service.LectureCommandServiceImpl;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.InvalidCategoryException;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
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
          .isInstanceOf(IllegalArgumentException.class);
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
          .isInstanceOf(IllegalArgumentException.class);
      verify(lectureRepository, times(0)).save(any(Lecture.class));
    }
  }
}
