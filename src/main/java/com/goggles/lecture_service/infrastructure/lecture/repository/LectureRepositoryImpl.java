package com.goggles.lecture_service.infrastructure.lecture.repository;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

  private final LectureJpaRepository lectureJpaRepository;

  @Override
  public Lecture save(Lecture lecture) {
    return lectureJpaRepository.save(lecture);
  }

  @Override
  public Optional<Lecture> findById(UUID id) {
    return lectureJpaRepository.findById(id);
  }

  @Override
  public List<Lecture> findAllByInstructorId(UUID instructorId) {
    return lectureJpaRepository.findAllByInstructor_InstructorId(instructorId);
  }

  @Override
  public Optional<Lecture> findByIdAndStatus(UUID id, LectureStatus status) {
    return lectureJpaRepository.findByIdAndStatus(id, status);
  }

  @Override
  public Optional<Lecture> findByIdIncludeDeleted(UUID id) {
    return lectureJpaRepository.findByIdIncludeDeleted(id);
  }
}
