
package registration.part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Message class for the QuickChat application.
 * It validates, creates, sends and stores messages.
 */
public class Message {

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static int totalMessagesSent = 0;
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final Set<String> usedMessageIDs = new HashSet<>();
    private static final Random random = new Random();

    public Message(String messageID, int messageNumber,
            String recipient, String messageText) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = "";
    }

    /**
     * Generates a ten-character numeric message ID.
     *
     * @return a unique message ID for this running application
     */
    public static String generateMessageID() {

        String id;

        do {
            StringBuilder newID = new StringBuilder();

            for (int i = 0; i < 10; i++) {
                newID.append(random.nextInt(10));
            }

            id = newID.toString();

        } while (usedMessageIDs.contains(id));

        usedMessageIDs.add(id);
        return id;
    }

    /**
     * Checks that the message ID contains no more than ten digits.
     *
     * @return true if the ID is valid
     */
    public boolean checkMessageID() {

        return messageID != null
                && messageID.matches("[0-9]{1,10}");
    }

    /**
     * Checks the recipient's South African international number.
     *
     * @return the appropriate validation message
     */
    public String checkRecipientCell() {

        if (recipient != null
                && recipient.matches("^\\+27[0-9]{9}$")) {

            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /**
     * Checks the message length.
     *
     * @return a message explaining whether the text is valid
     */
    public String checkMessageLength() {

        if (messageText == null) {
            return "Please enter a message.";
        }

        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }

        int excessCharacters = messageText.length() - 250;

        return "Message exceeds 250 characters by "
                + excessCharacters + "; please reduce the size.";
    }

    /**
     * Creates a hash using the first two ID digits, message number,
     * and first and last words of the message.
     *
     * @return the message hash in uppercase
     */
    public String createMessageHash() {

        if (messageID == null || messageID.length() < 2
                || messageText == null || messageText.trim().isEmpty()) {

            return "";
        }

        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll(
                "^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");

        String lastWord = words[words.length - 1].replaceAll(
                "^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");

        messageHash = (messageID.substring(0, 2)
                + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();

        return messageHash;
    }

    /**
     * Processes the user's choice for a completed message.
     *
     * 1 = Send, 2 = Disregard, 3 = Store.
     *
     * @param choice the selected option
     * @return the result of the selected action
     */
    
    public String SentMessage(int choice) {

        if (choice == 1) {

            if (!checkMessageID()
                    || !checkRecipientCell().equals(
                            "Cell phone number successfully captured.")
                    || !checkMessageLength().equals(
                            "Message ready to send.")) {

                return "Message could not be sent. Please check the details.";
            }

            createMessageHash();
            sentMessages.add(this);
            totalMessagesSent++;

            return "Message successfully sent.";
        }

        if (choice == 2) {
            return "Press 0 to delete the message.";
        }

        if (choice == 3) {
            return storeMessage();
        }

        return "Invalid option. Please choose 1, 2 or 3.";
    }


    /**
     * Stores the message in a JSON file.
     *
     * Existing JSON array entries are preserved when another message
     * is stored. Special characters are escaped for valid JSON.
     *
     * @return a message showing whether storage succeeded
     */
    public String storeMessage() {

        if (!checkMessageID()
                || !checkRecipientCell().equals(
                        "Cell phone number successfully captured.")
                || !checkMessageLength().equals(
                        "Message ready to send.")) {

            return "Message could not be stored. Please check the details.";
        }

        createMessageHash();

        File file = new File("messages.json");
        String existingContent = "";

        try {
            if (file.exists()) {
                StringBuilder content = new StringBuilder();

                try (BufferedReader reader =
                        new BufferedReader(new FileReader(file))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                }

                existingContent = content.toString().trim();
            }

            String jsonObject = "{"
                    + "\"messageID\":\"" + escapeJSON(messageID) + "\","
                    + "\"messageNumber\":" + messageNumber + ","
                    + "\"messageHash\":\"" + escapeJSON(messageHash) + "\","
                    + "\"recipient\":\"" + escapeJSON(recipient) + "\","
                    + "\"message\":\"" + escapeJSON(messageText) + "\""
                    + "}";

            String newContent;

            if (existingContent.isEmpty()
                    || existingContent.equals("[]")) {

                newContent = "[" + jsonObject + "]";
            } else if (existingContent.startsWith("[")
                    && existingContent.endsWith("]")) {

                String previousMessages = existingContent.substring(
                        1, existingContent.length() - 1).trim();

                if (previousMessages.isEmpty()) {
                    newContent = "[" + jsonObject + "]";
                } else {
                    newContent = "[" + previousMessages
                            + "," + jsonObject + "]";
                }
            } else {
                return "Unable to store message: existing JSON file is invalid.";
            }

            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(newContent);
            }

            return "Message successfully stored.";

        } catch (IOException exception) {
            return "Unable to store message: " + exception.getMessage();
        }
    }

    /**
     * Escapes special characters for JSON string values.
     *
     * @param value the text to escape
     * @return escaped text
     */
    private String escapeJSON(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Returns the details of all messages successfully sent
     * during this application run.
     *
     * @return all sent message details
     */
    public String printMessages() {

        StringBuilder output = new StringBuilder();

        for (Message sentMessage : sentMessages) {
            output.append("Message ID: ")
                    .append(sentMessage.messageID).append("\n");

            output.append("Message Hash: ")
                    .append(sentMessage.messageHash).append("\n");

            output.append("Recipient: ")
                    .append(sentMessage.recipient).append("\n");

            output.append("Message: ")
                    .append(sentMessage.messageText).append("\n\n");
        }

        return output.toString();
    }

    /**
     * Returns the total number of successfully sent messages.
     *
     * @return total messages sent
     */
    public static int returnTotalMessages() {
    return totalMessagesSent;
    }

    /**
     * Clears message tracking for isolated unit tests.
     */
    public static void resetMessages() {

        sentMessages.clear();
        usedMessageIDs.clear();
        totalMessagesSent = 0;
    }
}
