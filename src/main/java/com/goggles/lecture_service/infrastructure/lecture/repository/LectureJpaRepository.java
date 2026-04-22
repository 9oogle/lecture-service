package com.goggles.lecture_service.infrastructure.lecture.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goggles.lecture_service.domain.lecture.entity.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public interface LectureJpaRepository extends JpaRepository<Lecture, UUID> {

	Optional<Lecture> findByIdAndDeletedAtIsNull(UUID id);

	List<Lecture> findAllByInstructorInstructorId(UUID instructorId);

	List<Lecture> findAllByStatus(LectureStatus status);
}
