package com.goggles.lecture_service.infrastructure.lecture.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goggles.lecture_service.domain.lecture.entity.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public interface LectureJpaRepository extends JpaRepository<Lecture, UUID> {

	Optional<Lecture> findByIdAndDeletedAtIsNull(UUID id);

	@Query(
		value = "SELECT * FROM lecture.p_lecture WHERE instructor_id = :instructorId AND deleted_at IS NULL",
		nativeQuery = true
	)
	List<Lecture> findAllByInstructorId(@Param("instructorId") UUID instructorId);

	List<Lecture> findAllByStatus(LectureStatus status);
}
