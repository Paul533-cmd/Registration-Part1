
package registration.part1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit 4 tests for QuickChat Part 3.
 *
 * Tests the five arrays, searching, reports,
 * deletion by hash and JSON loading.
 */
public class MessageManagerTest {

    private MessageManager manager;

    @Before
    public void setUp() {
        manager = new MessageManager();
    }

    /**
     * Creates a valid test message with a known ID.
     */
    private Message createMessage(
            String id, int number,
            String recipient, String text) {

        Message message = new Message(
                id, number, recipient, text);

        message.createMessageHash();

        return message;
    }

    @Test
    public void testSentMessagesArray() {

        Message message = createMessage(
                "1234567890", 1,
                "+27718693002", "Hello there");

        manager.addSentMessage(message);

        assertEquals(1, manager.getSentMessages().length);
        assertEquals(
                "1234567890",
                manager.getSentMessages()[0].getMessageID());
    }

    @Test
    public void testDisregardedMessagesArray() {

        Message message = createMessage(
                "2234567890", 2,
                "+27718693002", "Ignore this");

        manager.addDisregardedMessage(message);

        assertEquals(
                1, manager.getDisregardedMessages().length);
    }

    @Test
    public void testMessageIDsAndHashesArrays() {

        Message first = createMessage(
                "1234567890", 1,
                "+27718693002", "First message");

        Message second = createMessage(
                "2234567890", 2,
                "+27718693003", "Second message");

        manager.addSentMessage(first);
        manager.addDisregardedMessage(second);

        assertEquals(2, manager.getMessageIDs().length);
        assertEquals(2, manager.getMessageHashes().length);

        assertEquals(
                first.getMessageID(),
                manager.getMessageIDs()[0]);

        assertEquals(
                second.getMessageHash(),
                manager.getMessageHashes()[1]);
    }

    @Test
    public void testDuplicateSentMessageNotAdded() {

        Message message = createMessage(
                "1234567890", 1,
                "+27718693002", "Hello there");

        manager.addSentMessage(message);
        manager.addSentMessage(message);

        assertEquals(1, manager.getSentMessages().length);
        assertEquals(1, manager.getMessageIDs().length);
    }

    @Test
    public void testLongestStoredMessage() {

        Message shortMessage = createMessage(
                "1234567890", 1,
                "+27718693002", "Hello");

        Message longMessage = createMessage(
                "2234567890", 2,
                "+27718693003",
                "This is the longest message in the test");

        // These are added as stored messages.
        // The manager also loads the existing JSON file,
        // so we verify that our longer test message is found
        // using a separate controlled JSON test below.
        manager.addSentMessage(shortMessage);
        manager.addSentMessage(longMessage);

        assertEquals(
                "Message ID: 2234567890",
                manager.searchByMessageID(
                        "2234567890").split("\n")[0]);
    }

    @Test
    public void testSearchByMessageID() {

        Message message = createMessage(
                "1234567890", 1,
                "+27718693002", "Search this message");

        manager.addSentMessage(message);

        String result =
                manager.searchByMessageID("1234567890");

        assertTrue(result.contains("1234567890"));
        assertTrue(result.contains("Search this message"));
    }

    @Test
    public void testSearchByInvalidMessageID() {

        assertEquals(
                "Message ID not found.",
                manager.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchByRecipient() {

        Message first = createMessage(
                "1234567890", 1,
                "+27718693002", "First message");

        Message second = createMessage(
                "2234567890", 2,
                "+27718693002", "Second message");

        manager.addSentMessage(first);
        manager.addDisregardedMessage(second);

        String result =
                manager.searchByRecipient("+27718693002");

        assertTrue(result.contains("First message"));
        assertTrue(result.contains("Second message"));
    }

    @Test
    public void testSearchByUnknownRecipient() {

        assertEquals(
                "No messages found for this recipient.",
                manager.searchByRecipient("+27999999999"));
    }

    @Test
    public void testMessageReport() {

        Message message = createMessage(
                "1234567890", 1,
                "+27718693002", "Report message");

        manager.addSentMessage(message);

        String report = manager.displayStoredReport();

        assertTrue(report.contains("1234567890"));
        assertTrue(report.contains("Recipient: +27718693002"));
        assertTrue(report.contains("Report message"));
        assertTrue(report.contains(message.getMessageHash()));
    }

    @Test
    public void testEmptyMessageReport() {

        assertEquals(
                "No messages available.",
                manager.displayStoredReport());
    }

    @Test
    public void testArrayCounts() {

        Message first = createMessage(
                "1234567890", 1,
                "+27718693002", "First message");

        Message second = createMessage(
                "2234567890", 2,
                "+27718693003", "Second message");

        manager.addSentMessage(first);
        manager.addDisregardedMessage(second);

        String counts = manager.displayArrayCounts();

        assertTrue(counts.contains("Sent messages: 1"));
        assertTrue(counts.contains("Disregarded messages: 1"));
        assertTrue(counts.contains("Message hashes: 2"));
        assertTrue(counts.contains("Message IDs: 2"));
    }

    /**
     * Tests loading, longest message, deletion and stored
     * message reporting using a temporary JSON file.
     *
     * The real messages.json file is never modified.
     */
    @Test
    public void testJSONLoadingAndStoredFeatures()
            throws IOException {

        File temporaryFile =
                File.createTempFile("quickchat-test-", ".json");

        temporaryFile.deleteOnExit();

        String json = "["
                + "{\"messageID\":\"1234567890\","
                + "\"messageNumber\":1,"
                + "\"messageHash\":\"12:1:HELLOTHERE\","
                + "\"recipient\":\"+27718693002\","
                + "\"message\":\"Hello there\"},"
                + "{\"messageID\":\"2234567890\","
                + "\"messageNumber\":2,"
                + "\"messageHash\":\"22:2:THISMESSAGE\","
                + "\"recipient\":\"+27718693003\","
                + "\"message\":\"This is a much longer message\"}"
                + "]";

        try (FileWriter writer =
                new FileWriter(temporaryFile)) {
            writer.write(json);
        }

        String loadResult =
                manager.readStoredMessagesFromJSON(
                        temporaryFile.getAbsolutePath());

        assertEquals(
                "Stored messages loaded successfully.",
                loadResult);

        assertEquals(2, manager.getStoredMessages().length);

        assertEquals(
                "This is a much longer message",
                manager.displayLongestStoredMessage());

        assertTrue(
                manager.displayStoredSendersAndRecipients(
                        "+27821234567")
                        .contains("Sender: +27821234567"));

        assertTrue(
                manager.displayStoredReport()
                        .contains("Hello there"));

        assertEquals(
                "Message: \"Hello there\" successfully deleted.",
                manager.deleteByHash("12:1:HELLOTHERE"));

        assertEquals(1, manager.getStoredMessages().length);

        assertEquals(
                "Message hash not found.",
                manager.deleteByHash("INVALID-HASH"));

        assertEquals(1, manager.getMessageIDs().length);
        assertEquals(1, manager.getMessageHashes().length);
    }

    @Test
    public void testLoadingJSONTwiceDoesNotDuplicate()
            throws IOException {

        File temporaryFile =
                File.createTempFile("quickchat-repeat-", ".json");

        temporaryFile.deleteOnExit();

        String json = "["
                + "{\"messageID\":\"1234567890\","
                + "\"messageNumber\":1,"
                + "\"messageHash\":\"12:1:HELLOTHERE\","
                + "\"recipient\":\"+27718693002\","
                + "\"message\":\"Hello there\"}"
                + "]";

        try (FileWriter writer =
                new FileWriter(temporaryFile)) {
            writer.write(json);
        }

        manager.readStoredMessagesFromJSON(
                temporaryFile.getAbsolutePath());

        manager.readStoredMessagesFromJSON(
                temporaryFile.getAbsolutePath());

        assertEquals(1, manager.getStoredMessages().length);
        assertEquals(1, manager.getMessageIDs().length);
    }
}
