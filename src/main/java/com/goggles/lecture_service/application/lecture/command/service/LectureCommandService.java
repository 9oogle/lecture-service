package com.goggles.lecture_service.application.lecture.command.service;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureStatusChangeCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureStatusChangeResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureSubmitReviewCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;

public interface LectureCommandService {
  LectureCreateResult createLecture(LectureCreateCommand command);

  ChapterCreateResult createChapter(ChapterCreateCommand command);

  LectureUpdateResult updateLecture(LectureUpdateCommand command);

  LectureDeleteResult deleteLecture(LectureDeleteCommand command);

  LectureStatusChangeResult submitReview(LectureSubmitReviewCommand command);

  LectureStatusChangeResult changeStatus(LectureStatusChangeCommand command);

  ChapterUpdateResult updateChapter(ChapterUpdateCommand command);

  ChapterDeleteResult deleteChapter(ChapterDeleteCommand command);

  ChapterReorderResult reorderChapters(ChapterReorderCommand command);
}
