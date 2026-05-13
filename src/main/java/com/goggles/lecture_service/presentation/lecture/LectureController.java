package com.goggles.lecture_service.presentation.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.lecture.LectureService;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderResult;
import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureStatusChangeResult;
import com.goggles.lecture_service.application.lecture.command.dto.LectureSubmitReviewCommand;
import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;
import com.goggles.lecture_service.application.lecture.query.dto.LectureDetail;
import com.goggles.lecture_service.application.lecture.query.dto.LectureListQuery;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.domain._common.UserType;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterCreateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterCreateResponse;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterDeleteResponse;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterReorderRequest;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterReorderResponse;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterUpdateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.ChapterUpdateResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureCreateRequest;
import com.goggles.lecture_service.presentation.lecture.dto.LectureCreateResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureDeleteResponse;
import com.goggles.lecture_service.presentation.lecture.dto.LectureStatusChangeRequest;
import com.goggles.lecture_service.presentation.lecture.dto.LectureStatusChangeResponse;
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

  // 강의 생성 (강사 또는 MASTER)
  // TODO: 추후 강사 활성/비활성 상태 검증이 필요하면 user-service Feign 호출 추가
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LectureCreateResponse createLecture(
      @RequestHeader("X-User-Id") UUID instructorId,
      @RequestHeader("X-User-Name") String instructorName,
      @RequestHeader("X-User-Role") String userRole,
      @Valid @RequestBody LectureCreateRequest request) {
    return LectureCreateResponse.from(
        lectureService.createLecture(
            request.toCommand(instructorId, instructorName, UserType.from(userRole))));
  }

  // 강의 수정(DRAFT 상태에서만, 강사 본인 또는 MASTER)
  @PatchMapping("/{lectureId}")
  public LectureUpdateResponse updateLecture(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @Valid @RequestBody LectureUpdateRequest request) {

    LectureUpdateResult result =
        lectureService.updateLecture(request.toCommand(lectureId, userId, UserType.from(userRole)));

    return LectureUpdateResponse.from(result);
  }

  // 강의 승인 요청 (강사 본인만, DRAFT → PENDING_REVIEW)
  @PatchMapping("/{lectureId}/review-requests")
  public LectureStatusChangeResponse submitReview(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId) {

    LectureStatusChangeResult result =
        lectureService.submitReview(
            new LectureSubmitReviewCommand(lectureId, userId, UserType.from(userRole)));

    return LectureStatusChangeResponse.from(result);
  }

  // 관리자: 승인/반려/숨김 (Status 변경)
  @PatchMapping("/{lectureId}/status")
  public LectureStatusChangeResponse changeStatus(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @Valid @RequestBody LectureStatusChangeRequest request) {

    LectureStatusChangeResult result =
        lectureService.changeStatus(request.toCommand(lectureId, userId, UserType.from(userRole)));

    return LectureStatusChangeResponse.from(result);
  }

  // 강의 삭제 (DRAFT 상태에서만, 강사 본인 또는 MASTER, soft delete)
  @DeleteMapping("/{lectureId}")
  public LectureDeleteResponse deleteLecture(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId) {

    LectureDeleteResult result =
        lectureService.deleteLecture(
            new LectureDeleteCommand(lectureId, userId, UserType.from(userRole)));

    return LectureDeleteResponse.from(result);
  }

  // 챕터 생성 (DRAFT 상태에서만, 강사 본인 또는 MASTER)
  @PostMapping("/{lectureId}/chapters")
  @ResponseStatus(HttpStatus.CREATED)
  public ChapterCreateResponse createChapter(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @Valid @RequestBody ChapterCreateRequest request) {
    ChapterCreateResult result =
        lectureService.createChapter(request.toCommand(lectureId, userId, UserType.from(userRole)));
    return ChapterCreateResponse.from(result);
  }

  // 챕터 수정 (DRAFT 상태에서만)
  @PatchMapping("/{lectureId}/chapters/{chapterId}")
  public ChapterUpdateResponse updateChapter(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @PathVariable UUID chapterId,
      @Valid @RequestBody ChapterUpdateRequest request) {

    ChapterUpdateResult result =
        lectureService.updateChapter(
            request.toCommand(lectureId, chapterId, userId, UserType.from(userRole)));

    return ChapterUpdateResponse.from(result);
  }

  // 챕터 삭제 (DRAFT 상태에서만)
  @DeleteMapping("/{lectureId}/chapters/{chapterId}")
  public ChapterDeleteResponse deleteChapter(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @PathVariable UUID chapterId) {

    ChapterDeleteResult result =
        lectureService.deleteChapter(
            new ChapterDeleteCommand(lectureId, chapterId, userId, UserType.from(userRole)));

    return ChapterDeleteResponse.from(result);
  }

  // 챕터 일괄 순서 변경 (DRAFT 상태에서만)
  @PatchMapping("/{lectureId}/chapters/reorder")
  public ChapterReorderResponse reorderChapters(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable UUID lectureId,
      @Valid @RequestBody ChapterReorderRequest request) {

    ChapterReorderResult result =
        lectureService.reorderChapters(
            request.toCommand(lectureId, userId, UserType.from(userRole)));

    return ChapterReorderResponse.from(result);
  }
}
