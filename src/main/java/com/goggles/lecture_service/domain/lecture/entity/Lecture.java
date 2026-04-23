package com.goggles.lecture_service.domain.lecture.entity;

import com.goggles.common.domain.BaseAudit;
import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.ChapterNotFoundException;
import com.goggles.lecture_service.domain.lecture.exception.DuplicateSortOrderException;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureStatusException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import com.goggles.lecture_service.domain.lecture.vo.InstructorInfo;
import com.goggles.lecture_service.domain.lecture.vo.LectureContent;
import com.goggles.lecture_service.domain.lecture.vo.Money;
import com.goggles.lecture_service.domain.lecture.vo.ChapterContent;
import com.goggles.lecture_service.domain.lecture.vo.ChapterDuration;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseAudit {

	@Id
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id = UUID.randomUUID();

	@Embedded
	private InstructorInfo instructor;

	@Column(nullable = false, length = 50)
	private String category;

	@Embedded
	private LectureContent content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private DurationPolicy durationPolicy;

	@Embedded
	private Money price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private LectureStatus status;

	@Column(columnDefinition = "TEXT")
	private String rejectionReason;

	@OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Chapter> chapters = new ArrayList<>();

	// ── 정적 팩토리 메서드 ──────────────────────────────────────────────

	public static Lecture create(
		UUID instructorId,
		String instructorName,
		String category,
		String title,
		String subtitle,
		String description,
		DurationPolicy durationPolicy,
		Long priceAmount) {

		validateCategory(category);

		return new Lecture(
			new InstructorInfo(instructorId, instructorName),
			category,
			new LectureContent(title, subtitle, description),
			durationPolicy,
			Money.of(priceAmount)
		);
	}

	private Lecture(
		InstructorInfo instructor,
		String category,
		LectureContent content,
		DurationPolicy durationPolicy,
		Money price) {
		this.instructor = instructor;
		this.category = category;
		this.content = content;
		this.durationPolicy = durationPolicy != null ? durationPolicy : DurationPolicy.DAYS_365;
		this.price = price;
		this.status = LectureStatus.DRAFT;
	}

	// 도메인 메서드 (챕터 관리)

	public void addChapter(String title, String content, int sortOrder, int durationSeconds) {
		validateDraftStatus();
		validateDuplicateSortOrder(sortOrder);

		// VO 생성을 엔티티 내부에서 처리하여 외부 의존성 감소
		ChapterContent chapterContent = new ChapterContent(title, content);
		ChapterDuration chapterDuration = new ChapterDuration(durationSeconds);

		chapters.add(Chapter.create(this, chapterContent, sortOrder, chapterDuration));
	}

	public void removeChapter(UUID chapterId) {
		validateDraftStatus();
		boolean removed = chapters.removeIf(chapter -> chapter.getId().equals(chapterId));
		if (!removed) {
			throw new ChapterNotFoundException(chapterId); // ← 이걸로 교체
		}
	}

	public void reorderChapter(UUID chapterId, int newSortOrder) {
		validateDraftStatus();
		Chapter target = findChapterById(chapterId);
		validateDuplicateSortOrder(newSortOrder);
		target.updateSortOrder(newSortOrder);
	}

	public List<Chapter> getChapters() {
		return Collections.unmodifiableList(chapters);
	}

	// 도메인 메서드 (정보 수정)

	public void updateMetadata(
		String title,
		String subtitle,
		String description,
		String category,
		DurationPolicy durationPolicy,
		Long priceAmount) {
		validateDraftStatus();
		validateCategory(category);

		this.content = new LectureContent(title, subtitle, description);
		this.category = category;
		this.durationPolicy = durationPolicy;
		this.price = Money.of(priceAmount);
	}

	// 도메인 메서드 (상태 전이)

	public void submitForReview() {
		if (this.status != LectureStatus.DRAFT) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_INVALID_STATUS);
		}
		if (chapters.isEmpty()) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_CHAPTER_REQUIRED);
		}
		this.status = LectureStatus.PENDING_REVIEW;
	}

	public void approve() {
		if (this.status != LectureStatus.PENDING_REVIEW) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_INVALID_STATUS);
		}
		this.status = LectureStatus.PUBLISHED;
		this.rejectionReason = null;
	}

	public void reject(String reason) {
		if (this.status != LectureStatus.PENDING_REVIEW) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_INVALID_STATUS);
		}
		if (reason == null || reason.isBlank()) {
			throw new BadRequestException("반려 사유는 필수입니다.");
		}
		this.status = LectureStatus.DRAFT;
		this.rejectionReason = reason;
	}

	public void hide() {
		if (this.status != LectureStatus.PUBLISHED) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_INVALID_STATUS);
		}
		this.status = LectureStatus.HIDDEN;
	}

	// 내부 검증 및 편의 메서드

	private void validateDraftStatus() {
		if (this.status != LectureStatus.DRAFT) {
			throw new InvalidLectureStatusException(LectureErrorCode.LECTURE_INVALID_STATUS);
		}
	}

	private void validateDuplicateSortOrder(int sortOrder) {
		boolean isDuplicate = chapters.stream().anyMatch(c -> c.getSortOrder() == sortOrder);
		if (isDuplicate) {
			throw new DuplicateSortOrderException(sortOrder);
		}
	}

	private Chapter findChapterById(UUID chapterId) {
		return chapters.stream()
			.filter(c -> c.getId().equals(chapterId))
			.findFirst()
			.orElseThrow(() -> new ChapterNotFoundException(chapterId));
	}

	private static void validateCategory(String category) {
		if (category == null || category.isBlank()) {
			throw new BadRequestException("카테고리는 필수입니다.");
		}
	}
}