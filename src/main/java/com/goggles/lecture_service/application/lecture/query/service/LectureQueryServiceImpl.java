package com.goggles.lecture_service.application.lecture.query.service;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureQueryRepository;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureQueryServiceImpl implements LectureQueryService {

  private final LectureRepository lectureRepository;
  private final LectureQueryRepository lectureQueryRepository;

  @Override
  public CommonPageResponse<LectureSummary> getLectures(
      LectureSearchCondition condition, CommonPageRequest pageRequest) {
    return lectureQueryRepository.findAllByCondition(condition, pageRequest, LectureSummary::from);
  }

  @Override
  public LectureDetail getLectureDetail(UUID lectureId) {
    Lecture lecture =
        lectureRepository
            .findByIdAndStatus(lectureId, LectureStatus.PUBLISHED)
            .orElseThrow(() -> new LectureNotFoundException(lectureId));

    return LectureDetail.from(lecture);
  }

  @Override
  public CommonPageResponse<LectureSummary> getTeachingLectures(
      UUID instructorId, CommonPageRequest pageRequest) {
    return lectureQueryRepository.findAllByInstructorId(
        instructorId, pageRequest, LectureSummary::from);
  }
}
