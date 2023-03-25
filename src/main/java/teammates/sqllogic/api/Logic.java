package teammates.sqllogic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.sqllogic.core.AccountRequestsLogic;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.sqllogic.core.DeadlineExtensionsLogic;
import teammates.sqllogic.core.FeedbackQuestionsLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.sqllogic.core.UsageStatisticsLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.storage.sqlentity.User;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final AccountsLogic accountsLogic = AccountsLogic.inst();
    final AccountRequestsLogic accountRequestLogic = AccountRequestsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final UsersLogic usersLogic = UsersLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();
    final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    /**
     * Creates an account request.
     *
     * @return newly created account request.
     * @throws InvalidParametersException if the account request details are invalid.
     * @throws EntityAlreadyExistsException if the account request already exists.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute)
            throws InvalidParametersException, EntityAlreadyExistsException {

        return accountRequestLogic.createAccountRequest(name, email, institute);
    }

    /**
     * Gets the account request with the given email and institute.
     *
     * @return account request with the given email and institute.
     */
    public AccountRequest getAccountRequest(String email, String institute) {
        return accountRequestLogic.getAccountRequest(email, institute);
    }

    /**
     * Creates/Resets the account request with the given email and institute
     * such that it is not registered.
     *
     * @return account request that is unregistered with the
     *         email and institute.
     */
    public AccountRequest resetAccountRequest(String email, String institute)
            throws EntityDoesNotExistException, InvalidParametersException {
        return accountRequestLogic.resetAccountRequest(email, institute);
    }

    /**
     * Deletes account request by email and institute.
     *
     * <ul>
     * <li>Fails silently if no such account request.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccountRequest(String email, String institute) {
        accountRequestLogic.deleteAccountRequest(email, institute);
    }

    /**
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        return accountsLogic.getAccount(id);
    }

    /**
     * Gets an account by googleId.
     */
    public Account getAccountForGoogleId(String googleId) {
        return accountsLogic.getAccountForGoogleId(googleId);
    }

    /**
     * Get a list of accounts associated with email provided.
     */
    public List<Account> getAccountsForEmail(String email) {
        return accountsLogic.getAccountsForEmail(email);
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsLogic.createAccount(account);
    }

    /**
     * Deletes account by googleId.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccount(String googleId) {
        accountsLogic.deleteAccount(googleId);
    }

    /**
     * Deletes account and all users by googleId.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {
        accountsLogic.deleteAccountCascade(googleId);
    }

    /**
     * Gets a course by course id.
     * @param courseId courseId of the course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Creates a course.
     * @param course the course to create.
     * @return the created course.
     * @throws InvalidParametersException if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourse(course);
    }

    /**
     * Deletes a course by course id.
     * @param courseId of course.
     */
    public void deleteCourseCascade(String courseId) {
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     * @return the deletion timestamp assigned to the course.
     */
    public Course moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        return coursesLogic.moveCourseToRecycleBin(courseId);
    }

    /**
     * Restores a course and all data related to the course from Recycle Bin by
     * its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        coursesLogic.restoreCourseFromRecycleBin(courseId);
    }

    /**
     * Updates a course.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public Course updateCourse(String courseId, String name, String timezone)
            throws InvalidParametersException, EntityDoesNotExistException {
        return coursesLogic.updateCourse(courseId, name, timezone);
    }

    /**
     * Gets a list of section names for the given {@code courseId}.
     */
    public List<String> getSectionNamesForCourse(String courseId)
            throws EntityDoesNotExistException {
        return coursesLogic.getSectionNamesForCourse(courseId);
    }

    /**
     * Get section by {@code courseId} and {@code teamName}.
     */
    public Section getSectionByCourseIdAndTeam(String courseId, String teamName) {
        return coursesLogic.getSectionByCourseIdAndTeam(courseId, teamName);
    }

    /**
     * Creates a deadline extension.
     *
     * @return created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension already exist
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Fetch the deadline extension for a given user and session feedback.
     *
     * @return deadline extension instant if exists, else the default end time instant
     *         for the session feedback.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        return deadlineExtensionsLogic.getDeadlineForUser(session, user);
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(UUID id) {
        return feedbackSessionsLogic.getFeedbackSession(id);
    }

    /**
     * Gets a feedback session for {@code feedbackSessionName} and {@code courseId}.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(String feedbackSessionName, String courseId) {
        return feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Creates a feedback session.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackSessionsLogic.createFeedbackSession(session);
    }

    /**
     * Unpublishes a feedback session.
     * @return the unpublished feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be unpublished.
     */
    public FeedbackSession unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Creates a new feedback question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestion createFeedbackQuestion(FeedbackQuestion feedbackQuestion) throws InvalidParametersException {
        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }

    /**
     * Publishes a feedback session.
     * @return the published feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException if session is already published
     */
    public FeedbackSession publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.publishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }

    /**
     * Soft-deletes a specific  session to Recycle Bin.
     */
    public void moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Get usage statistics within a time range.
     */
    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Calculate usage statistics within a time range.
     */
    public UsageStatistics calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Create usage statistics within a time range.
     */
    public void createUsageStatistics(UsageStatistics attributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        usageStatisticsLogic.createUsageStatistics(attributes);
    }

    /**
     * Creates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification exists in the database
     */
    public Notification createNotification(Notification notification) throws
            InvalidParametersException, EntityAlreadyExistsException {
        return notificationsLogic.createNotification(notification);
    }

    /**
     * Gets a notification by ID.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public Notification getNotification(UUID notificationId) {
        return notificationsLogic.getNotification(notificationId);
    }

    /**
     * Updates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     * @return updated notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityDoesNotExistException if the notification does not exist in the database
     */
    public Notification updateNotification(UUID notificationId, Instant startTime, Instant endTime,
                                           NotificationStyle style, NotificationTargetUser targetUser, String title,
                                           String message) throws
            InvalidParametersException, EntityDoesNotExistException {
        return notificationsLogic.updateNotification(notificationId, startTime, endTime, style, targetUser, title, message);
    }

    /**
     * Deletes notification by ID.
     *
     * <ul>
     * <li>Fails silently if no such notification.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteNotification(UUID notificationId) {
        notificationsLogic.deleteNotification(notificationId);
    }

    /**
     * Get a list of IDs of the read notifications of the account.
     */
    public List<UUID> getReadNotificationsId(String id) {
        return accountsLogic.getReadNotificationsId(id);
    }

    /**
     * Updates user read status for notification with ID {@code notificationId} and expiry time {@code endTime}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null. {@code endTime} must be after current moment.
     */
    public List<UUID> updateReadNotifications(String id, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountsLogic.updateReadNotifications(id, notificationId, endTime);
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id    Id of Instructor.
     * @return      Returns Instructor if found else null.
     */
    public Instructor getInstructor(UUID id) {
        return usersLogic.getInstructor(id);
    }

    /**
     * Gets instructor associated with {@code courseId} and {@code email}.
     */
    public Instructor getInstructorForEmail(String courseId, String email) {
        return usersLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Gets an instructor by associated {@code regkey}.
     */
    public Instructor getInstructorByRegistrationKey(String regKey) {
        return usersLogic.getInstructorByRegistrationKey(regKey);
    }

    /**
     * Gets an instructor by associated {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        return usersLogic.getInstructorByGoogleId(courseId, googleId);
    }

    /**
     * Gets list of instructors by {@code googleId}.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        return usersLogic.getInstructorsForGoogleId(googleId);
    }

    /**
     * Gets instructors by associated {@code courseId}.
     */
    public List<Instructor> getInstructorsByCourse(String courseId) {
        return usersLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Creates an instructor.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createInstructor(instructor);
    }

    /**
     * Checks if an instructor with {@code googleId} can create a course with {@code institute}.
     */
    public boolean canInstructorCreateCourse(String googleId, String institute) {
        return usersLogic.canInstructorCreateCourse(googleId, institute);
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id    Id of Student.
     * @return      Returns Student if found else null.
     */
    public Student getStudent(UUID id) {
        return usersLogic.getStudent(id);
    }

    /**
     * Gets student associated with {@code courseId} and {@code email}.
     */
    public Student getStudentForEmail(String courseId, String email) {
        return usersLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;
        return usersLogic.getStudentsForCourse(courseId);
    }

    /**
     * Gets a student by associated {@code regkey}.
     */
    public Student getStudentByRegistrationKey(String regKey) {
        return usersLogic.getStudentByRegistrationKey(regKey);
    }

    /**
     * Gets a student by associated {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        return usersLogic.getStudentByGoogleId(courseId, googleId);
    }

    /**
     * Gets students by associated {@code teamName} and {@code courseId}.
     */
    public List<Student> getStudentsByTeamName(String teamName, String courseId) {
        return usersLogic.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Creates a student.
     *
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public Student createStudent(Student student) throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createStudent(student);
    }

    /**
     * Gets all instructors and students by associated {@code googleId}.
     */
    public List<User> getAllUsersByGoogleId(String googleId) {
        return usersLogic.getAllUsersByGoogleId(googleId);
    }

    /**
     * Deletes a user.
     *
     * <p>Fails silently if the user does not exist.</p>
     */
    public <T extends User> void deleteUser(T user) {
        usersLogic.deleteUser(user);
    }

    public List<Notification> getAllNotifications() {
        return notificationsLogic.getAllNotifications();
    }

    /**
     * Resets the googleId associated with the instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If instructor cannot be found with given email and courseId.
     */
    public void resetInstructorGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If student cannot be found with given email and courseId.
     */
    public void resetStudentGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetStudentGoogleId(email, courseId, googleId);
    }

    /**
     * Returns active notification for general users and the specified {@code targetUser}.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSession);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForStudents(FeedbackSession feedbackSession) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSession);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForInstructors(
            FeedbackSession feedbackSession, String instructorEmail) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructors(feedbackSession, instructorEmail);
    }

    /**
     * Persists the given data bundle to the database.
     */
    public SqlDataBundle persistDataBundle(SqlDataBundle dataBundle)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * @param feedbackQuestion the question to populate
     * @param courseId the ID of the course
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestion feedbackQuestion,
            String courseId, String emailOfEntityDoingQuestion,
            String teamOfEntityDoingQuestion) {
        assert feedbackQuestion != null;
        assert courseId != null;
        assert emailOfEntityDoingQuestion != null;

        feedbackQuestionsLogic.populateFieldsToGenerateInQuestion(
                feedbackQuestion, courseId, emailOfEntityDoingQuestion, teamOfEntityDoingQuestion);
    }

    /**
     * Gets a feedback question.
     *
     * @return null if not found.
     */
    public FeedbackQuestion getFeedbackQuestion(UUID id) {
        return feedbackQuestionsLogic.getFeedbackQuestion(id);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Map<String, FeedbackQuestionRecipient> getRecipientsOfQuestion(
            FeedbackQuestion question,
            @Nullable Instructor instructorGiver, @Nullable Student studentGiver) {
        assert question != null;

        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorGiver, studentGiver, null);
    }

}
