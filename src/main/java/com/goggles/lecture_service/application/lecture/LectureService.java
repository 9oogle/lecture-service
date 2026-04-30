package com.goggles.lecture_service.application.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
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
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.List;
import java.util.UUID;

public interface LectureService {

  CommonPageResponse<LectureSummary> getLectures(
      LectureSearchCondition condition, CommonPageRequest pageRequest);

  LectureDetail getLectureDetail(UUID lectureId);

  LectureCreateResult createLecture(LectureCreateCommand command);

  ChapterCreateResult createChapter(ChapterCreateCommand command);

  LectureUpdateResult updateLecture(LectureUpdateCommand command);

  LectureDeleteResult deleteLecture(LectureDeleteCommand command);

  LectureStatusChangeResult submitReview(LectureSubmitReviewCommand command);

  LectureStatusChangeResult changeStatus(LectureStatusChangeCommand command);

  List<LectureSummary> getTeachingLectures(UUID instructorId);

  ChapterUpdateResult updateChapter(ChapterUpdateCommand command);

  ChapterDeleteResult deleteChapter(ChapterDeleteCommand command);

  ChapterReorderResult reorderChapters(ChapterReorderCommand command);
}
