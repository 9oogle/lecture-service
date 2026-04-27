package com.goggles.lecture_service.application.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.application.lecture.command.service.LectureCommandService;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.application.lecture.query.service.LectureQueryService;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureQueryService lectureQueryService;
  private final LectureCommandService lectureCommandService;

  @Override
  public CommonPageResponse<LectureSummary> getLectures(
      LectureSearchCondition condition, CommonPageRequest pageRequest) {
    return lectureQueryService.getLectures(condition, pageRequest);
  }

  @Override
  public LectureDetail getLectureDetail(UUID lectureId) {
    return lectureQueryService.getLectureDetail(lectureId);
  }

  @Override
  public LectureCreateResult createLecture(LectureCreateCommand command) {
    return lectureCommandService.createLecture(command);
  }

  @Override
  public ChapterCreateResult createChapter(ChapterCreateCommand command) {
    return lectureCommandService.createChapter(command);
  }
}
