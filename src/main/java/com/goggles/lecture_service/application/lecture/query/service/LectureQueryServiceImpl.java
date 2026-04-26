package com.goggles.lecture_service.application.lecture.query.service;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureQueryServiceImpl implements LectureQueryService {

  private final LectureRepository lectureRepository;

  @Override
  public CommonPageResponse<LectureSummary> getLectures(
      LectureSearchCondition condition, CommonPageRequest pageRequest) {
    Page<Lecture> page = lectureRepository.findAllByCondition(condition, pageRequest);
    return CommonPageResponse.of(page, LectureSummary::from);
  }

  @Override
  public LectureDetail getLectureDetail(UUID lectureId) {
    Lecture lecture =
        lectureRepository
            .findById(lectureId)
            .orElseThrow(() -> new LectureNotFoundException(lectureId));

    validatePublished(lecture);

    return LectureDetail.from(lecture);
  }

  private void validatePublished(Lecture lecture) {
    if (lecture.getStatus() != LectureStatus.PUBLISHED) {
      throw new LectureNotFoundException(lecture.getId());
    }
  }
}
