package com.goggles.lecture_service.domain.lecture.repository;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface LectureQueryRepository {

  Page<Lecture> findAllByCondition(LectureSearchCondition condition, CommonPageRequest pageRequest);

  Page<Lecture> findAllByInstructorId(UUID instructorId, CommonPageRequest pageRequest);
}
