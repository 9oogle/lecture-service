package com.goggles.lecture_service.infrastructure.lecture.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goggles.lecture_service.domain.lecture.Lecture;

public interface LectureJpaRepository extends JpaRepository<Lecture, UUID> {

	List<Lecture> findAllByInstructor_InstructorId(UUID instructorId);

	// soft delete 필터 우회를 위해 native query 사용
	@Query(value = "SELECT * FROM lecture.p_lecture WHERE id = :id", nativeQuery = true)
	Optional<Lecture> findByIdIncludeDeleted(@Param("id") UUID id);
}
