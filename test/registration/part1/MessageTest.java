
package registration.part1;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit 4 tests for the QuickChat Part 2 Message class.
 *
 * Tests message IDs, recipient validation, message length,
 * message hashes, sending, disregarding and message totals.
 *
 * These tests do not modify the real messages.json file.
 */
public class MessageTest {

    private Message message;

    @Before
    public void setUp() {
        Message.resetMessages();

        message = new Message(
                "1234567890",
                1,
                "+27718693002",
                "Hello world"
        );
    }

    @Test
    public void testGenerateMessageIDHasTenDigits() {

        String id = Message.generateMessageID();

        assertNotNull(id);
        assertTrue(id.matches("[0-9]{10}"));
    }

    @Test
    public void testGeneratedMessageIDsAreUnique() {

        String firstID = Message.generateMessageID();
        String secondID = Message.generateMessageID();

        assertNotEquals(firstID, secondID);
    }

    @Test
    public void testValidMessageID() {

        assertTrue(message.checkMessageID());
    }

    @Test
    public void testInvalidMessageID() {

        Message invalid = new Message(
                "12345678901",
                1,
                "+27718693002",
                "Hello world"
        );

        assertFalse(invalid.checkMessageID());
    }

    @Test
    public void testValidRecipient() {

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testInvalidRecipientWithoutInternationalCode() {

        Message invalid = new Message(
                "1234567890",
                1,
                "0718693002",
                "Hello world"
        );

        assertTrue(
                invalid.checkRecipientCell().contains(
                        "incorrectly formatted")
        );
    }

    @Test
    public void testInvalidRecipientWithTooManyDigits() {

        Message invalid = new Message(
                "1234567890",
                1,
                "+277186930021",
                "Hello world"
        );

        assertTrue(
                invalid.checkRecipientCell().contains(
                        "incorrectly formatted")
        );
    }

    @Test
    public void testValidMessageLength() {

        assertEquals(
                "Message ready to send.",
                message.checkMessageLength()
        );
    }

    @Test
    public void testMessageExactly250Characters() {

        String text = repeatCharacter('A', 250);

        Message valid = new Message(
                "1234567890",
                1,
                "+27718693002",
                text
        );

        assertEquals(
                "Message ready to send.",
                valid.checkMessageLength()
        );
    }

    @Test
    public void testMessageExceeds250Characters() {

        String text = repeatCharacter('A', 251);

        Message invalid = new Message(
                "1234567890",
                1,
                "+27718693002",
                text
        );

        assertTrue(
                invalid.checkMessageLength().contains(
                        "exceeds 250 characters by 1")
        );
    }

    @Test
    public void testMessageHash() {

        assertEquals(
                "12:1:HELLOWORLD",
                message.createMessageHash()
        );
    }

    @Test
    public void testMessageHashWithMultipleWords() {

        Message another = new Message(
                "9876543210",
                2,
                "+27718693002",
                "This is a test message"
        );

        assertEquals(
                "98:2:THISMESSAGE",
                another.createMessageHash()
        );
    }

    @Test
    public void testSendValidMessage() {

        assertEquals(
                "Message successfully sent.",
                message.SentMessage(1)
        );

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testSendInvalidRecipient() {

        Message invalid = new Message(
                "1234567890",
                1,
                "0718693002",
                "Hello world"
        );

        assertEquals(
                "Message could not be sent. Please check the details.",
                invalid.SentMessage(1)
        );

        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testDisregardMessage() {

        assertEquals(
                "Press 0 to delete the message.",
                message.SentMessage(2)
        );

        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testSentMessageDetails() {

        message.SentMessage(1);

        String details = message.printMessages();

        assertTrue(details.contains("1234567890"));
        assertTrue(details.contains("12:1:HELLOWORLD"));
        assertTrue(details.contains("+27718693002"));
        assertTrue(details.contains("Hello world"));
    }

    @Test
    public void testTotalMessagesSent() {

        Message second = new Message(
                "2234567890",
                2,
                "+27718693003",
                "Second message"
        );

        message.SentMessage(1);
        second.SentMessage(1);

        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testInvalidStoreDoesNotWriteJSON() {

        Message invalid = new Message(
                "1234567890",
                1,
                "0718693002",
                "Invalid recipient"
        );

        assertEquals(
                "Message could not be stored. Please check the details.",
                invalid.SentMessage(3)
        );
    }

    /**
     * Creates a test string of the requested length.
     */
    private String repeatCharacter(char character, int count) {

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < count; i++) {
            text.append(character);
        }

        return text.toString();
    }
}
