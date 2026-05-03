package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.application.lecture.query.service.LectureQueryServiceImpl;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureQueryRepository;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LectureQueryServiceImplTest {

  @InjectMocks private LectureQueryServiceImpl lectureQueryService;

  @Mock private LectureRepository lectureRepository;
  @Mock private LectureQueryRepository lectureQueryRepository;

  private UUID lectureId;
  private Lecture lecture;

  @BeforeEach
  void setUp() {
    lectureId = UUID.randomUUID();

    lecture =
        Lecture.create(
            UUID.randomUUID(),
            "강사이름",
            "IT",
            "자바 강의",
            "자바 기초부터 심화까지",
            "강의 설명입니다.",
            DurationPolicy.DAYS_365,
            50000L);

    ReflectionTestUtils.setField(lecture, "id", lectureId);
  }

  @Nested
  @DisplayName("강의 상세 조회")
  class GetLectureDetail {

    @Test
    @DisplayName("성공: PUBLISHED 강의 상세 조회")
    void getLectureDetail_success() {
      // given
      ReflectionTestUtils.setField(lecture, "status", LectureStatus.PUBLISHED);
      when(lectureRepository.findByIdAndStatus(lectureId, LectureStatus.PUBLISHED))
          .thenReturn(Optional.of(lecture));

      // when
      LectureDetail result = lectureQueryService.getLectureDetail(lectureId);

      // then
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(lectureId);
      assertThat(result.title()).isEqualTo("자바 강의");
      assertThat(result.instructorName()).isEqualTo("강사이름");
      assertThat(result.category()).isEqualTo("IT");
      assertThat(result.price()).isEqualTo(50000L);
      assertThat(result.status()).isEqualTo(LectureStatus.PUBLISHED);
      verify(lectureRepository, times(1)).findByIdAndStatus(lectureId, LectureStatus.PUBLISHED);
    }

    @Test
    @DisplayName("실패: 강의를 찾을 수 없음")
    void getLectureDetail_notFound() {
      // given
      when(lectureRepository.findByIdAndStatus(lectureId, LectureStatus.PUBLISHED))
          .thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> lectureQueryService.getLectureDetail(lectureId))
          .isInstanceOf(LectureNotFoundException.class);
      verify(lectureRepository, times(1)).findByIdAndStatus(lectureId, LectureStatus.PUBLISHED);
    }

    @Test
    @DisplayName("실패: DRAFT 상태 강의 조회 불가")
    void getLectureDetail_notPublished() {
      // given
      when(lectureRepository.findByIdAndStatus(lectureId, LectureStatus.PUBLISHED))
          .thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> lectureQueryService.getLectureDetail(lectureId))
          .isInstanceOf(LectureNotFoundException.class);
      verify(lectureRepository, times(1)).findByIdAndStatus(lectureId, LectureStatus.PUBLISHED);
    }

    @Test
    @DisplayName("성공: 챕터 포함 강의 상세 조회")
    void getLectureDetail_withChapters() {
      // given
      lecture.addChapter("챕터1", "챕터1 내용", 1, 3600);
      lecture.addChapter("챕터2", "챕터2 내용", 2, 7200);
      ReflectionTestUtils.setField(lecture, "status", LectureStatus.PUBLISHED);
      when(lectureRepository.findByIdAndStatus(lectureId, LectureStatus.PUBLISHED))
          .thenReturn(Optional.of(lecture));

      // when
      LectureDetail result = lectureQueryService.getLectureDetail(lectureId);

      // then
      assertThat(result.chapters()).hasSize(2);
      assertThat(result.chapters().get(0).title()).isEqualTo("챕터1");
      assertThat(result.chapters().get(1).sortOrder()).isEqualTo(2);
      verify(lectureRepository, times(1)).findByIdAndStatus(lectureId, LectureStatus.PUBLISHED);
    }
  }

  @Nested
  @DisplayName("강사 본인 강의 목록 조회")
  class GetTeachingLectures {

    @Test
    @DisplayName("성공: 강사 본인 강의 페이지 위임 호출")
    @SuppressWarnings("unchecked")
    void getTeachingLectures_success() {
      // given
      UUID instructorId = UUID.randomUUID();
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      CommonPageResponse<LectureSummary> expected = mock(CommonPageResponse.class);

      when(lectureQueryRepository.findAllByInstructorId(
              eq(instructorId), eq(pageRequest), any(Function.class)))
          .thenReturn(expected);

      // when
      CommonPageResponse<LectureSummary> result =
          lectureQueryService.getTeachingLectures(instructorId, pageRequest);

      // then
      assertThat(result).isSameAs(expected);
      verify(lectureQueryRepository, times(1))
          .findAllByInstructorId(eq(instructorId), eq(pageRequest), any(Function.class));
    }
  }
}
