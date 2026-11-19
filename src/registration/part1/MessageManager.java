
package registration.part1;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Manages QuickChat messages and the five Part 3 arrays.
 */
public class MessageManager {

    private Message[] sentMessages = new Message[0];
    private Message[] disregardedMessages = new Message[0];
    private Message[] storedMessages = new Message[0];

    private String[] messageHashes = new String[0];
    private String[] messageIDs = new String[0];

    private boolean storedMessagesLoaded = false;

    public void addSentMessage(Message message) {
        if (!containsID(sentMessages, message.getMessageID())) {
            sentMessages = appendMessage(sentMessages, message);
            rebuildDetails();
        }
    }

    public void addDisregardedMessage(Message message) {
        if (!containsID(disregardedMessages, message.getMessageID())) {
            disregardedMessages =
                    appendMessage(disregardedMessages, message);
            rebuildDetails();
        }
    }

    public void addStoredMessage(Message message) {

        // Load previous JSON messages before adding current messages.
        if (!storedMessagesLoaded) {
            readStoredMessagesFromJSON("messages.json");
        }

        if (!containsID(storedMessages, message.getMessageID())) {
            storedMessages = appendMessage(storedMessages, message);
            rebuildDetails();
        }
    }

    private Message[] appendMessage(Message[] messages, Message message) {
        Message[] updated = Arrays.copyOf(
                messages, messages.length + 1);
        updated[updated.length - 1] = message;
        return updated;
    }

    private boolean containsID(Message[] messages, String id) {
        for (Message message : messages) {
            if (message.getMessageID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads stored messages from the existing messages.json file.
     * Preserves messages already held in memory and avoids duplicates.
     */
    public String readStoredMessagesFromJSON(String fileName) {

        try (BufferedReader reader =
                new BufferedReader(new FileReader(fileName))) {

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            String content = json.toString().trim();

            if (content.isEmpty() || content.equals("[]")) {
                storedMessagesLoaded = true;
                return "No stored messages were found.";
            }

            if (!content.startsWith("[") || !content.endsWith("]")) {
                return "The JSON file has an invalid format.";
            }

            String body = content.substring(1, content.length() - 1);
            String[] objects = body.split(",(?=\\s*\\{)");

            Message[] loadedMessages = new Message[0];

            for (String object : objects) {

                String id = getJSONValue(object, "messageID");
                String number = getJSONValue(object, "messageNumber");
                String hash = getJSONValue(object, "messageHash");
                String recipient = getJSONValue(object, "recipient");
                String messageText = getJSONValue(object, "message");

                if (id == null || number == null || hash == null
                        || recipient == null || messageText == null) {
                    return "Unable to read a stored message from JSON.";
                }

                Message message = new Message(
                        id,
                        Integer.parseInt(number),
                        recipient,
                        messageText
                );

                message.createMessageHash();

                if (!containsID(loadedMessages, id)) {
                    loadedMessages = appendMessage(
                            loadedMessages, message);
                }
            }

            // Merge with messages already created in this session.
            for (Message message : loadedMessages) {
                if (!containsID(storedMessages,
                        message.getMessageID())) {
                    storedMessages = appendMessage(
                            storedMessages, message);
                }
            }

            storedMessagesLoaded = true;
            rebuildDetails();

            return "Stored messages loaded successfully.";

        } catch (IOException | NumberFormatException exception) {
            return "Unable to read JSON file: "
                    + exception.getMessage();
        }
    }

    /**
     * Reads a value from one JSON message object.
     */
    private String getJSONValue(String object, String key) {

        String search = "\"" + key + "\":";
        int start = object.indexOf(search);

        if (start == -1) {
            return null;
        }

        start += search.length();

        while (start < object.length()
                && Character.isWhitespace(object.charAt(start))) {
            start++;
        }

        if (start < object.length()
                && object.charAt(start) == '"') {

            start++;

            StringBuilder value = new StringBuilder();
            boolean escaped = false;

            for (int i = start; i < object.length(); i++) {

                char character = object.charAt(i);

                if (escaped) {
                    switch (character) {
                        case 'n':
                            value.append('\n');
                            break;
                        case 'r':
                            value.append('\r');
                            break;
                        case 't':
                            value.append('\t');
                            break;
                        default:
                            value.append(character);
                    }
                    escaped = false;

                } else if (character == '\\') {
                    escaped = true;

                } else if (character == '"') {
                    return value.toString();

                } else {
                    value.append(character);
                }
            }

        } else {

            int end = object.indexOf(',', start);

            if (end == -1) {
                end = object.indexOf('}', start);
            }

            if (end != -1) {
                return object.substring(start, end).trim();
            }
        }

        return null;
    }

    /**
     * Displays the longest stored message.
     */
    public String displayLongestStoredMessage() {

        if (storedMessages.length == 0) {
            return "No stored messages available.";
        }

        Message longest = storedMessages[0];

        for (Message message : storedMessages) {
            if (message.getMessageText().length()
                    > longest.getMessageText().length()) {
                longest = message;
            }
        }

        return longest.getMessageText();
    }

    /**
     * Searches all three message arrays by message ID.
     */
    public String searchByMessageID(String id) {

        for (Message message : getAllMessages()) {
            if (message.getMessageID().equals(id)) {
                return formatMessage(message);
            }
        }

        return "Message ID not found.";
    }

    /**
     * Searches all three message arrays by recipient.
     */
    public String searchByRecipient(String recipient) {

        StringBuilder result = new StringBuilder();

        for (Message message : getAllMessages()) {
            if (message.getRecipient().equals(recipient)) {
                result.append(formatMessage(message))
                        .append("\n\n");
            }
        }

        if (result.length() == 0) {
            return "No messages found for this recipient.";
        }

        return result.toString().trim();
    }

    /**
     * Combines sent, disregarded and stored messages.
     */
    private Message[] getAllMessages() {

        Message[] allMessages = new Message[
                sentMessages.length
                + disregardedMessages.length
                + storedMessages.length
        ];

        int index = 0;

        for (Message message : sentMessages) {
            allMessages[index++] = message;
        }

        for (Message message : disregardedMessages) {
            allMessages[index++] = message;
        }

        for (Message message : storedMessages) {
            allMessages[index++] = message;
        }

        return allMessages;
    }

    /**
     * Deletes a stored message from the current session by hash.
     */
    public String deleteByHash(String hash) {

        for (int i = 0; i < storedMessages.length; i++) {

            if (storedMessages[i].getMessageHash().equals(hash)) {

                String deletedText =
                        storedMessages[i].getMessageText();

                storedMessages = removeMessage(storedMessages, i);
                rebuildDetails();

                return "Message: \"" + deletedText
                        + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    private Message[] removeMessage(Message[] messages, int index) {

        Message[] updated = new Message[messages.length - 1];

        for (int i = 0, j = 0; i < messages.length; i++) {
            if (i != index) {
                updated[j++] = messages[i];
            }
        }

        return updated;
    }

    /**
     * Rebuilds the ID and hash arrays without duplicate entries.
     */
    private void rebuildDetails() {

        messageHashes = new String[0];
        messageIDs = new String[0];

        for (Message message : getAllMessages()) {

            if (!containsString(messageIDs, message.getMessageID())) {

                messageIDs = Arrays.copyOf(
                        messageIDs, messageIDs.length + 1);
                messageIDs[messageIDs.length - 1] =
                        message.getMessageID();

                messageHashes = Arrays.copyOf(
                        messageHashes, messageHashes.length + 1);
                messageHashes[messageHashes.length - 1] =
                        message.getMessageHash();
            }
        }
    }

    private boolean containsString(String[] values, String target) {

        for (String value : values) {
            if (value.equals(target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Formats a message for searching and reporting.
     */
    private String formatMessage(Message message) {

        return "Message ID: " + message.getMessageID()
                + "\nMessage Hash: " + message.getMessageHash()
                + "\nRecipient: " + message.getRecipient()
                + "\nMessage: " + message.getMessageText();
    }

    /**
     * Displays the report for all three message types.
     */
    public String displayStoredReport() {

        Message[] allMessages = getAllMessages();

        if (allMessages.length == 0) {
            return "No messages available.";
        }

        StringBuilder report = new StringBuilder();

        for (Message message : allMessages) {
            report.append(formatMessage(message))
                    .append("\n\n");
        }

        return report.toString().trim();
    }

    /**
     * Displays sender and recipient for stored messages.
     */
    public String displayStoredSendersAndRecipients(String sender) {

        if (storedMessages.length == 0) {
            return "No stored messages available.";
        }

        StringBuilder result = new StringBuilder();

        for (Message message : storedMessages) {
            result.append("Sender: ")
                    .append(sender).append("\n");
            result.append("Recipient: ")
                    .append(message.getRecipient())
                    .append("\n\n");
        }

        return result.toString().trim();
    }

    public Message[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentMessages.length);
    }

    public Message[] getDisregardedMessages() {
        return Arrays.copyOf(
                disregardedMessages, disregardedMessages.length);
    }

    public Message[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedMessages.length);
    }

    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageHashes.length);
    }

    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageIDs.length);
    }

    /**
     * Displays the number of entries in all five arrays.
     */
    public String displayArrayCounts() {

        return "Sent messages: " + sentMessages.length
                + "\nDisregarded messages: " + disregardedMessages.length
                + "\nStored messages: " + storedMessages.length
                + "\nMessage hashes: " + messageHashes.length
                + "\nMessage IDs: " + messageIDs.length;
    }
}
