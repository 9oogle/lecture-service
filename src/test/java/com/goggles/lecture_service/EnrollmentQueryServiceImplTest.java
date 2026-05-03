package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureQuery;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;
import com.goggles.lecture_service.application.enrollment.query.service.EnrollmentQueryServiceImpl;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentStatusException;
import com.goggles.lecture_service.domain.enrollment.repository.EnrolledLecturePageQuery;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
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

@ExtendWith(MockitoExtension.class)
class EnrollmentQueryServiceImplTest {

  @InjectMocks private EnrollmentQueryServiceImpl enrollmentQueryService;

  @Mock private EnrollmentRepository enrollmentRepository;

  private UUID studentId;
  private CommonPageRequest pageRequest;

  @BeforeEach
  void setUp() {
    studentId = UUID.randomUUID();
    pageRequest = CommonPageRequest.of(0, 10);
  }

  @Nested
  @DisplayName("학생 수강 강의 목록 조회")
  class GetEnrolledLectures {

    @Test
    @DisplayName("성공: 수강 이력 페이지 위임 호출")
    @SuppressWarnings("unchecked")
    void getEnrolledLectures_success() {
      // given
      EnrolledLectureQuery query =
          EnrolledLectureQuery.of(studentId, null, null, null, pageRequest);
      CommonPageResponse<EnrolledLectureResult> expected = mock(CommonPageResponse.class);

      when(enrollmentRepository.findEnrolledLectures(
              any(EnrolledLecturePageQuery.class), any(Function.class)))
          .thenReturn(expected);

      // when
      CommonPageResponse<EnrolledLectureResult> result =
          enrollmentQueryService.getEnrolledLectures(query);

      // then
      assertThat(result).isSameAs(expected);
      verify(enrollmentRepository, times(1))
          .findEnrolledLectures(any(EnrolledLecturePageQuery.class), any(Function.class));
    }

    @Test
    @DisplayName("실패: studentId 누락 시 EnrolledLectureQuery 생성에서 예외")
    void enrolledLectureQuery_nullStudentId_throws() {
      assertThatThrownBy(() -> EnrolledLectureQuery.of(null, null, null, null, pageRequest))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: pageRequest 누락 시 EnrolledLectureQuery 생성에서 예외")
    void enrolledLectureQuery_nullPageRequest_throws() {
      assertThatThrownBy(() -> EnrolledLectureQuery.of(studentId, null, null, null, null))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: 검색 불가 status(RESERVE)는 예외")
    void enrolledLectureQuery_reserveStatus_throws() {
      assertThatThrownBy(
              () -> EnrolledLectureQuery.of(studentId, null, "RESERVE", null, pageRequest))
          .isInstanceOf(InvalidEnrollmentStatusException.class);
    }

    @Test
    @DisplayName("실패: 검색 불가 status(CANCELED)는 예외")
    void enrolledLectureQuery_canceledStatus_throws() {
      assertThatThrownBy(
              () -> EnrolledLectureQuery.of(studentId, null, "CANCELED", null, pageRequest))
          .isInstanceOf(InvalidEnrollmentStatusException.class);
    }
  }
}
