package teammates.it.sqllogic.core;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
// import teammates.sqllogic.core.FeedbackQuestionsLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackSessionsLogic}
 */
public class FeedbackSessionsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    // private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
    }

    @Test
    public void testPublishFeedbackSession()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession unpublishedFs = typicalDataBundle.feedbackSessions.get("unpublishedSession1InTypicalCourse");

        FeedbackSession publishedFs1 = fsLogic.publishFeedbackSession(
                unpublishedFs.getName(), unpublishedFs.getCourse().getId());

        assertEquals(publishedFs1.getName(), unpublishedFs.getName());
        assertTrue(publishedFs1.isPublished());

        assertThrows(InvalidParametersException.class, () -> fsLogic.publishFeedbackSession(
                publishedFs1.getName(), publishedFs1.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                "non-existent name", unpublishedFs.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                unpublishedFs.getName(), "random-course-id"));
    }

    @Test
    public void testUnpublishFeedbackSession()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession publishedFs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession unpublishedFs1 = fsLogic.unpublishFeedbackSession(
                publishedFs.getName(), publishedFs.getCourse().getId());

        assertEquals(unpublishedFs1.getName(), publishedFs.getName());
        assertFalse(unpublishedFs1.isPublished());

        assertThrows(InvalidParametersException.class, () -> fsLogic.unpublishFeedbackSession(
                unpublishedFs1.getName(), unpublishedFs1.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                "non-existent name", publishedFs.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                publishedFs.getName(), "random-course-id"));
    }

    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession retrievedFs = fsLogic.getFeedbackSession(fs.getName(), fs.getCourse().getId());

        // For using debugger
        // List<FeedbackQuestion> localVar = retrievedFs.getFeedbackQuestions();
        assertNotNull(retrievedFs);
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fs.getName(), fs.getCourse().getId()));

        // but this does not return empty list
        // assertFalse(fqLogic.getFeedbackQuestionsForSession(retrievedFs).isEmpty());

        // Why does this return empty list - it seems that it doesnt fetch the joins
        assertFalse(retrievedFs.getFeedbackQuestions().isEmpty());
        // assertTrue(retrievedFs.getFeedbackQuestions().stream().anyMatch(fbq -> !fbq.getFeedbackResponses().isEmpty()));
        // assertTrue(retrievedFs.getFeedbackQuestions().stream().anyMatch(
        //         fbq -> fbq.getFeedbackResponses().stream().anyMatch(fbr -> !fbr.getFeedbackResponseComments().isEmpty())));
        // assertFalse(retrievedFs.getDeadlineExtensions().isEmpty());

        // // delete existing feedback session directly
        // fsLogic.deleteFeedbackSessionCascade(fs.getName(), fs.getCourse().getId());

        // // check deletion is cascaded
        // assertNull(fsLogic.getFeedbackSession(fs.getName(), fs.getCourse().getId()));
        // assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fs.getName(), fs.getCourse().getId()));
        // assertTrue(retrievedFs.getFeedbackQuestions().isEmpty());
        // assertFalse(retrievedFs.getFeedbackQuestions().stream().anyMatch(fbq -> !fbq.getFeedbackResponses().isEmpty()));
        // assertFalse(retrievedFs.getFeedbackQuestions().stream().anyMatch(
        //         fbq -> fbq.getFeedbackResponses().stream().anyMatch(fbr -> !fbr.getFeedbackResponseComments().isEmpty())));
        // assertTrue(retrievedFs.getDeadlineExtensions().isEmpty());
    }
}
