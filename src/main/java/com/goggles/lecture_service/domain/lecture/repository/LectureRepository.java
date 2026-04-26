package com.goggles.lecture_service.domain.lecture.repository;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface LectureRepository {
  Lecture save(Lecture lecture);

  // 일반 조회 - @SQLRestriction으로 삭제된 거 자동 필터링
  Optional<Lecture> findById(UUID id);

  List<Lecture> findAllByInstructorId(UUID instructorId);

  Page<Lecture> findAllByCondition(LectureSearchCondition condition, CommonPageRequest pageRequest);

  // 관리자 전용 - 삭제된 것도 포함
  Optional<Lecture> findByIdIncludeDeleted(UUID id);
}
