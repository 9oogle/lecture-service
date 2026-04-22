package com.goggles.lecture_service.domain.lecture.entity;


import com.goggles.common.domain.BaseAudit;
import com.goggles.lecture_service.domain.lecture.vo.ChapterContent;
import com.goggles.lecture_service.domain.lecture.vo.ChapterDuration;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "p_chapter",
	uniqueConstraints =
	@UniqueConstraint(
		name = "uk_chapter_lecture_sort_order",
		columnNames = {"lecture_id", "sort_order"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter extends BaseAudit {

	@Id
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@Embedded
	private ChapterContent content;

	@Column(nullable = false)
	private int sortOrder;

	@Embedded
	private ChapterDuration duration;

	// Lecture.addChapter()를 통해서만 생성 가능 (패키지 접근)
	static Chapter create(
		Lecture lecture, ChapterContent content, int sortOrder, ChapterDuration duration) {
		validateSortOrder(sortOrder);
		return new Chapter(lecture, content, sortOrder, duration);
	}

	private Chapter(
		Lecture lecture, ChapterContent content, int sortOrder, ChapterDuration duration) {
		this.lecture = lecture;
		this.content = content;
		this.sortOrder = sortOrder;
		this.duration = duration;
	}

	public UUID getLectureId() {
		return lecture.getId();
	}

	public void updateContent(ChapterContent newContent) {
		this.content = newContent;
	}

	public void updateSortOrder(int newSortOrder) {
		validateSortOrder(newSortOrder);
		this.sortOrder = newSortOrder;
	}

	public void updateDuration(ChapterDuration newDuration) {
		this.duration = newDuration;
	}

	private static void validateSortOrder(int sortOrder) {
		if (sortOrder < 1) {
			throw new IllegalArgumentException("챕터 순서는 1 이상이어야 합니다.");
		}
	}
}