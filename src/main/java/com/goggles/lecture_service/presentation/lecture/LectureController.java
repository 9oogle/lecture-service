package com.goggles.lecture_service.presentation.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.LectureService;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureListQuery;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {

  private final LectureService lectureService;

  // 강의 목록 조회 (교육생용 - PUBLISHED만)
  @GetMapping
  public CommonPageResponse<LectureSummary> getLectures(
      @ModelAttribute LectureListQuery query, CommonPageRequest pageRequest) {
    return lectureService.getLectures(query.toCondition(), pageRequest);
  }

  // 강의 상세 조회
  @GetMapping("/{lectureId}")
  public LectureDetail getLectureDetail(@PathVariable UUID lectureId) {
    return lectureService.getLectureDetail(lectureId);
  }
}
