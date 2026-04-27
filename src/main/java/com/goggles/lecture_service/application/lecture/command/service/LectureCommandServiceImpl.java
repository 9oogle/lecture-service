package com.goggles.lecture_service.application.lecture.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.domain.lecture.Chapter;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.exception.LectureNotFoundException;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;

import lombok.RequiredArgsConstructor;

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

	@Override
	@Transactional
	public ChapterCreateResult createChapter(ChapterCreateCommand command) {
		Lecture lecture =
			lectureRepository
				.findById(command.lectureId())
				.orElseThrow(() -> new LectureNotFoundException(command.lectureId()));

		Chapter chapter =
			lecture.addChapter(
				command.title(), command.content(), command.sortOrder(), command.durationSeconds());

		return ChapterCreateResult.from(lecture.getId(), chapter.getId());
	}
}
