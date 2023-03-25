package teammates.storage.sqlentity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "FeedbackSessions", uniqueConstraints = @UniqueConstraint(columnNames = {"courseId", "name"}))
public class FeedbackSession extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String creatorEmail;

    @Column(nullable = false)
    private String instructions;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private Instant sessionVisibleFromTime;

    @Column(nullable = false)
    private Instant resultsVisibleFromTime;

    @Column(nullable = false)
    @Convert(converter = DurationLongConverter.class)
    private Duration gracePeriod;

    @Column(nullable = false)
    private boolean isOpeningEmailEnabled;

    @Column(nullable = false)
    private boolean isClosingEmailEnabled;

    @Column(nullable = false)
    private boolean isPublishedEmailEnabled;

    @Column(nullable = false)
    private boolean isPublishedEmailSent;

    @OneToMany(mappedBy = "feedbackSession", cascade = CascadeType.REMOVE)
    @Fetch(FetchMode.JOIN)
    private List<DeadlineExtension> deadlineExtensions = new ArrayList<>();

    @OneToMany(mappedBy = "feedbackSession", cascade = CascadeType.REMOVE)
    @Fetch(FetchMode.JOIN)
    private List<FeedbackQuestion> feedbackQuestions = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    protected FeedbackSession() {
        // required by Hibernate
    }

    public FeedbackSession(String name, Course course, String creatorEmail, String instructions, Instant startTime,
            Instant endTime, Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, Duration gracePeriod,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled) {
        this.setId(UUID.randomUUID());
        this.setName(name);
        this.setCourse(course);
        this.setCreatorEmail(creatorEmail);
        this.setInstructions(StringUtils.defaultString(instructions));
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setSessionVisibleFromTime(sessionVisibleFromTime);
        this.setResultsVisibleFromTime(resultsVisibleFromTime);
        this.setGracePeriod(gracePeriod);
        this.setOpeningEmailEnabled(isOpeningEmailEnabled);
        this.setClosingEmailEnabled(isClosingEmailEnabled);
        this.setPublishedEmailEnabled(isPublishedEmailEnabled);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, name), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("instructions to students", instructions),
                errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(creatorEmail), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGracePeriod(gracePeriod), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission opening time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission closing time", endTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the responses to become visible", resultsVisibleFromTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(startTime, endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                sessionVisibleFromTime, startTime), errors);

        Instant actualSessionVisibleFromTime = sessionVisibleFromTime;

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = startTime;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                actualSessionVisibleFromTime, resultsVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                endTime, deadlineExtensions), errors);

        return errors;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Instant sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Instant getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Instant resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    public Duration getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Duration gracePeriod) {
        this.gracePeriod = Objects.requireNonNullElse(gracePeriod, Duration.ZERO);
    }

    public boolean isOpeningEmailEnabled() {
        return isOpeningEmailEnabled;
    }

    public void setOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setClosingEmailEnabled(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public List<DeadlineExtension> getDeadlineExtensions() {
        return deadlineExtensions;
    }

    public void setDeadlineExtensions(List<DeadlineExtension> deadlineExtensions) {
        this.deadlineExtensions = deadlineExtensions;
    }

    public List<FeedbackQuestion> getFeedbackQuestions() {
        return feedbackQuestions;
    }

    public void setFeedbackQuestions(List<FeedbackQuestion> feedbackQuestions) {
        this.feedbackQuestions = feedbackQuestions;
    }

    public boolean isPublishedEmailSent() {
        return isPublishedEmailSent;
    }

    public void setPublishedEmailSent(boolean isPublishedEmailSent) {
        this.isPublishedEmailSent = isPublishedEmailSent;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "FeedbackSession [id=" + id + ", course=" + course.getId() + ", name=" + name
                + ", creatorEmail=" + creatorEmail
                + ", instructions=" + instructions + ", startTime=" + startTime + ", endTime=" + endTime
                + ", sessionVisibleFromTime=" + sessionVisibleFromTime + ", resultsVisibleFromTime="
                + resultsVisibleFromTime + ", gracePeriod=" + gracePeriod + ", isOpeningEmailEnabled="
                + isOpeningEmailEnabled + ", isClosingEmailEnabled=" + isClosingEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + ", deadlineExtensions=" + deadlineExtensions
                + ", feedbackQuestions=" + feedbackQuestions + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSession otherFs = (FeedbackSession) other;
            return Objects.equals(this.getId(), otherFs.getId());
        } else {
            return false;
        }
    }

    /**
     * Returns {@code true} if the session is visible; {@code false} if not.
     *         Does not care if the session has started or not.
     */
    public boolean isVisible() {
        Instant visibleTime = this.sessionVisibleFromTime;

        if (visibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            visibleTime = this.startTime;
        }

        Instant now = Instant.now();
        return now.isAfter(visibleTime) || now.equals(visibleTime);
    }

    /**
     * Gets the instructions of the feedback session.
     */
    public String getInstructionsString() {
        return SanitizationHelper.sanitizeForRichText(instructions);
    }

    /**
     * Checks if the feedback session is closed.
     * This occurs only when the current time is after both the deadline and the grace period.
     */
    public boolean isClosed() {
        return !isOpened() && Instant.now().isAfter(endTime);
    }

    /**
     * Checks if the feedback session is open.
     * This occurs when the current time is either the start time or later but before the deadline.
     */
    public boolean isOpened() {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime);
    }

    /**
     * Checks if the feedback session is during the grace period.
     * This occurs when the current time is after end time, but before the end of the grace period.
     */
    public boolean isInGracePeriod() {
        return Instant.now().isAfter(endTime) && !isClosed();
    }

    /**
     * Checks if the feedback session is opened given the extendedDeadline and grace period.
     */
    public boolean isOpenedGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime))
                && now.isBefore(extendedDeadline.plus(gracePeriod)) || now.isBefore(endTime.plus(gracePeriod));
    }

    /**
     * Checks if the feedback session is closed given the extendedDeadline and grace period.
     * This occurs only when it is after the extended deadline or end time plus grace period.
     */
    public boolean isClosedGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return !isOpenedGivenExtendedDeadline(extendedDeadline)
                && now.isAfter(endTime.plus(gracePeriod)) && now.isAfter(extendedDeadline.plus(gracePeriod));
    }

    /**
     * Checks if the feedback session is during the grace period given the extendedDeadline.
     */
    public boolean isInGracePeriodGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return now.isAfter(endTime) && now.isAfter(extendedDeadline) && !isClosedGivenExtendedDeadline(extendedDeadline);
    }

    /**
     * Returns {@code true} if the results of the feedback session is visible; {@code false} if not.
     *         Does not care if the session has ended or not.
     */
    public boolean isPublished() {
        Instant publishTime = this.resultsVisibleFromTime;

        if (publishTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            return isVisible();
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return false;
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_NOW)) {
            return true;
        }

        Instant now = Instant.now();
        return now.isAfter(publishTime) || now.equals(publishTime);
    }

}
