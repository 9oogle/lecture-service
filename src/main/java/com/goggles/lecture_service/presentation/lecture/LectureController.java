package com.goggles.lecture_service.presentation.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.LectureService;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureListQuery;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterCreateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterCreateResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureCreateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.LectureCreateResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureDeleteResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureUpdateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.LectureUpdateResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

  // 강의 생성
  // TODO: 추후 강사 활성/비활성 상태 검증이 필요하면 user-service Feign 호출 추가
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LectureCreateResponse createLecture(
      @RequestHeader("X-User-Id") UUID instructorId,
      @RequestHeader("X-User-Name") String instructorName,
      @Valid @RequestBody LectureCreateRequest request) {
    return LectureCreateResponse.from(
        lectureService.createLecture(request.toCommand(instructorId, instructorName)));
  }

  // 강의 수정(DRAFT 상태에서만, 강사 본인 또는 MASTER)
  @PatchMapping("/{lectureId}")
  public LectureUpdateResponse updateLecture(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @Valid @RequestBody LectureUpdateRequest request) {

    LectureUpdateResult result =
        lectureService.updateLecture(request.toCommand(lectureId, userId, userRole));

    return LectureUpdateResponse.from(result);
  }

  // 강의 삭제 (DRAFT 상태에서만, 강사 본인 또는 MASTER, soft delete)
  @DeleteMapping("/{lectureId}")
  public LectureDeleteResponse deleteLecture(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId) {

    LectureDeleteResult result =
        lectureService.deleteLecture(new LectureDeleteCommand(lectureId, userId, userRole));

    return LectureDeleteResponse.from(result);
  }

  // 챕터 생성
  @PostMapping("/{lectureId}/chapters")
  @ResponseStatus(HttpStatus.CREATED)
  public ChapterCreateResponse createChapter(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Name") String userName,
      // TODO(#3 user-service 로그인 API 연동 후): instructorId 일치 검증
      @PathVariable UUID lectureId,
      @Valid @RequestBody ChapterCreateRequest request) {
    ChapterCreateResult result = lectureService.createChapter(request.toCommand(lectureId));
    return ChapterCreateResponse.from(result);
  }
}
