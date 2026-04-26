package com.goggles.lecture_service.application.lecture.query.service;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.UUID;

public interface LectureQueryService {

  CommonPageResponse<LectureSummary> getLectures(
      LectureSearchCondition condition, CommonPageRequest pageRequest);

  LectureDetail getLectureDetail(UUID lectureId);
}
