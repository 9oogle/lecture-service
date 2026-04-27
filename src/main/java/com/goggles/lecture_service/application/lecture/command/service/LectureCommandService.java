package com.goggles.lecture_service.application.lecture.command.service;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;

public interface LectureCommandService {
  LectureCreateResult createLecture(LectureCreateCommand command);

  ChapterCreateResult createChapter(ChapterCreateCommand command);
}
