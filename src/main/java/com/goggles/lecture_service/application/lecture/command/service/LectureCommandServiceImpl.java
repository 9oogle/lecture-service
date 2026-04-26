package com.goggles.lecture_service.application.lecture.command.service;

import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureCommandServiceImpl implements LectureCommandService {

  private final LectureRepository lectureRepository;

  @Override
  public LectureCreateResult createLecture(LectureCreateCommand command) {
    // Todo: APPROVED 상태 강사만 생성 가능 조건 추가
    Lecture lecture =
        Lecture.create(
            command.instructorId(),
            command.instructorName(),
            command.category(),
            command.title(),
            command.subtitle(),
            command.description(),
            command.durationPolicy(),
            command.price());

    Lecture saved = lectureRepository.save(lecture);
    return LectureCreateResult.from(saved);
  }
}
