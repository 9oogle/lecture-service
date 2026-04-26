package com.goggles.lecture_service.domain.lecture.repository;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.function.Function;

public interface LectureQueryRepository {

  <T> CommonPageResponse<T> findAllByCondition(
      LectureSearchCondition condition, CommonPageRequest pageRequest, Function<Lecture, T> mapper);
}
