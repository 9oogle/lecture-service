package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveResult;
import com.goggles.lecture_service.application.enrollment.command.service.EnrollmentCommandServiceImpl;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.ReserveFailReason;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentReserveFailedException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.List;
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
@DisplayName("EnrollmentCommandServiceImpl 테스트")
class EnrollmentCommandServiceImplTest {

  @Mock private LectureRepository lectureRepository;
  @Mock private EnrollmentRepository enrollmentRepository;

  @InjectMocks private EnrollmentCommandServiceImpl service;

  private UUID userId;
  private String userName;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userName = "홍길동";
  }

  @Nested
  @DisplayName("수강 등록 예약")
  class Reserve {

    @Test
    @DisplayName("성공: 단건 강의 예약")
    void reserve_singleProduct_success() {
      Lecture lecture = publishedLecture();
      when(lectureRepository.findAllByIdIn(List.of(lecture.getId()))).thenReturn(List.of(lecture));
      when(enrollmentRepository.existsActiveByStudentAndLecture(userId, lecture.getId()))
          .thenReturn(false);
      when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

      List<LectureEnrollmentReserveResult> results =
          service.reserve(
              new LectureEnrollmentReserveCommand(List.of(lecture.getId()), userId, userName));

      assertThat(results).hasSize(1);
      assertThat(results.get(0).productId()).isEqualTo(lecture.getId());
      verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("성공: 다건 강의 예약")
    void reserve_multiProduct_success() {
      Lecture lecture1 = publishedLecture();
      Lecture lecture2 = publishedLecture();
      List<UUID> productIds = List.of(lecture1.getId(), lecture2.getId());

      when(lectureRepository.findAllByIdIn(productIds)).thenReturn(List.of(lecture1, lecture2));
      when(enrollmentRepository.existsActiveByStudentAndLecture(userId, lecture1.getId()))
          .thenReturn(false);
      when(enrollmentRepository.existsActiveByStudentAndLecture(userId, lecture2.getId()))
          .thenReturn(false);
      when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

      List<LectureEnrollmentReserveResult> results =
          service.reserve(new LectureEnrollmentReserveCommand(productIds, userId, userName));

      assertThat(results).hasSize(2);
      assertThat(results)
          .extracting(LectureEnrollmentReserveResult::productId)
          .containsExactlyElementsOf(productIds);
      verify(enrollmentRepository, times(2)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("실패: productIds가 비어 있으면 예외")
    void reserve_emptyProductIds_throws() {
      assertThatThrownBy(() -> new LectureEnrollmentReserveCommand(List.of(), userId, userName))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: 다건 중 일부 미출간이면 전체 예약 실패")
    void reserve_partialNotPublished_allOrNothing() {
      Lecture published = publishedLecture();
      Lecture draft = draftLecture();
      List<UUID> productIds = List.of(published.getId(), draft.getId());

      when(lectureRepository.findAllByIdIn(productIds)).thenReturn(List.of(published, draft));
      when(enrollmentRepository.existsActiveByStudentAndLecture(userId, published.getId()))
          .thenReturn(false);

      assertThatThrownBy(
              () ->
                  service.reserve(
                      new LectureEnrollmentReserveCommand(productIds, userId, userName)))
          .isInstanceOf(EnrollmentReserveFailedException.class)
          .extracting("reason")
          .isEqualTo(ReserveFailReason.NOT_PUBLISHED);

      verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("실패: 강의 미존재")
    void reserve_lectureNotFound_throws() {
      UUID missing = UUID.randomUUID();
      when(lectureRepository.findAllByIdIn(List.of(missing))).thenReturn(List.of());

      assertThatThrownBy(
              () ->
                  service.reserve(
                      new LectureEnrollmentReserveCommand(List.of(missing), userId, userName)))
          .isInstanceOf(EnrollmentReserveFailedException.class)
          .extracting("reason")
          .isEqualTo(ReserveFailReason.LECTURE_NOT_FOUND);
    }

    @Test
    @DisplayName("실패: 강의가 PUBLISHED 가 아닌 경우")
    void reserve_notPublished_throws() {
      Lecture draft = draftLecture(); // status = DRAFT
      when(lectureRepository.findAllByIdIn(List.of(draft.getId()))).thenReturn(List.of(draft));

      assertThatThrownBy(
              () ->
                  service.reserve(
                      new LectureEnrollmentReserveCommand(
                          List.of(draft.getId()), userId, userName)))
          .isInstanceOf(EnrollmentReserveFailedException.class)
          .extracting("reason")
          .isEqualTo(ReserveFailReason.NOT_PUBLISHED);
    }

    @Test
    @DisplayName("실패: 이미 수강 중인 강의")
    void reserve_duplicate_throws() {
      Lecture lecture = publishedLecture();
      when(lectureRepository.findAllByIdIn(List.of(lecture.getId()))).thenReturn(List.of(lecture));
      when(enrollmentRepository.existsActiveByStudentAndLecture(userId, lecture.getId()))
          .thenReturn(true);

      assertThatThrownBy(
              () ->
                  service.reserve(
                      new LectureEnrollmentReserveCommand(
                          List.of(lecture.getId()), userId, userName)))
          .isInstanceOf(EnrollmentReserveFailedException.class)
          .extracting("reason")
          .isEqualTo(ReserveFailReason.DUPLICATE_ENROLLMENT);
    }
  }

  // --- 헬퍼: 테스트용 Lecture 생성 ---
  private Lecture publishedLecture() {
    Lecture l =
        Lecture.create(
            UUID.randomUUID(),
            "강사명",
            "BACKEND",
            "스프링 강의",
            "부제",
            "설명",
            DurationPolicy.DAYS_365,
            10000L);
    // 상태를 PUBLISHED 로 만들기 위해 도메인 메서드 시퀀스 호출
    l.addChapter("챕터1", "내용", 1, 600);
    l.submitForReview();
    l.approve();
    return l;
  }

  private Lecture draftLecture() {
    return Lecture.create(
        UUID.randomUUID(), "강사명", "BACKEND", "스프링 강의", "부제", "설명", DurationPolicy.DAYS_365, 10000L);
  }
}
