
package registration.part1;

import java.util.Scanner;

/**
 * Runs registration, login and the QuickChat messaging application.
 */
public class RegistrationPart1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        LoginPart1 login = new LoginPart1("", "");
        MessageManager manager = new MessageManager();

        // Show the exact JSON file location being used.
        System.out.println("JSON file location: "
                + new java.io.File("messages.json").getAbsolutePath());

        System.out.println("==========================================");
        System.out.println("       REGISTRATION AND LOGIN SYSTEM");
        System.out.println("==========================================");

        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter your surname:");
        String surname = scanner.nextLine();

        // Validate and capture the username.
        System.out.println();
        System.out.println("--------------- REGISTRATION ---------------");
        System.out.println();
        System.out.println("Username must contain an underscore (_)");
        System.out.println("and must be no more than 5 characters long.");
        System.out.println();

        System.out.println("Create a username:");
        String username = scanner.nextLine();

        while (!login.checkUserName(username)) {
            System.out.println();
            System.out.println(
                    "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length."
            );
            System.out.println();
            System.out.println("Please enter a valid username:");
            username = scanner.nextLine();
        }

        System.out.println("Username successfully captured.");

        // Validate and capture the email address.
        System.out.println();
        System.out.println("Enter your email address:");
        String email = scanner.nextLine();

        while (!login.checkEmail(email)) {
            System.out.println();
            System.out.println(
                    "Email address is not correctly formatted; "
                    + "please enter a valid email address."
            );
            System.out.println();
            System.out.println("Please enter a valid email address:");
            email = scanner.nextLine();
        }

        System.out.println("Email address successfully captured.");

        // Validate and capture the password.
        System.out.println();
        System.out.println("Password must contain:");
        System.out.println("- At least 8 characters");
        System.out.println("- A capital letter");
        System.out.println("- A number");
        System.out.println("- A special character");
        System.out.println();

        System.out.println("Create a password:");
        String password = scanner.nextLine();

        while (!login.checkPasswordComplexity(password)) {
            System.out.println();
            System.out.println(
                    "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, "
                    + "and a special character."
            );
            System.out.println();
            System.out.println("Please enter a valid password:");
            password = scanner.nextLine();
        }

        System.out.println("Password successfully captured.");

        // Validate and capture the South African cell phone number.
        System.out.println();
        System.out.println("Cell phone number must use the South African");
        System.out.println("international code (+27).");
        System.out.println();

        System.out.println("Enter your South African cell phone number (+27...):");
        String cellPhoneNumber = scanner.nextLine();

        while (!login.checkCellPhoneNumber(cellPhoneNumber)) {
            System.out.println();
            System.out.println(
                    "Cell phone number incorrectly formatted or does not contain "
                    + "international code."
            );
            System.out.println();
            System.out.println("Please enter your cell phone number again:");
            cellPhoneNumber = scanner.nextLine();
        }

        System.out.println("Cell phone number successfully added.");

        String registrationMessage = login.registerUser(
                username, password, cellPhoneNumber,
                firstName, surname, email);

        System.out.println(registrationMessage);

        System.out.println("Enter your username or email:");
        String loginUsernameOrEmail = scanner.nextLine();

        System.out.println("Enter your password:");
        String loginPassword = scanner.nextLine();

        System.out.println(login.returnLoginStatus(
                loginUsernameOrEmail, loginPassword));

        if (!login.loginUser(loginUsernameOrEmail, loginPassword)) {
            System.out.println(
                    "QuickChat is unavailable because login failed.");
            scanner.close();
            return;
        }

        System.out.println("Welcome to QuickChat.");

        int numberOfMessages = -1;

        while (numberOfMessages < 0) {
            System.out.println("How many messages would you like to enter?");

            try {
                numberOfMessages = Integer.parseInt(scanner.nextLine());

                if (numberOfMessages < 0) {
                    System.out.println("Enter zero or a positive number.");
                }

            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid whole number.");
            }
        }

        int messagesEntered = 0;
        boolean quit = false;

        while (!quit) {

            System.out.println("\n--------------- QUICKCHAT ---------------");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.println("Choose an option:");

            String menuChoice = scanner.nextLine();

            switch (menuChoice) {

                case "1":

                    if (messagesEntered >= numberOfMessages) {
                        System.out.println("You have reached your message limit.");
                        break;
                    }

                    // Retry invalid details without using the message quota.
                    System.out.println("Enter recipient cell number (+27...):");
                    String recipient = scanner.nextLine();

                    System.out.println("Enter your message:");
                    String messageText = scanner.nextLine();

                    String messageID = Message.generateMessageID();

                    Message message = new Message(
                            messageID,
                            messagesEntered,
                            recipient,
                            messageText);

                    // Validate both fields before allowing an action.
                    while (!message.checkRecipientCell().equals(
                            "Cell phone number successfully captured.")
                            || !message.checkMessageLength().equals(
                                    "Message ready to send.")
                            || messageText.trim().isEmpty()) {

                        System.out.println();

                        if (!message.checkRecipientCell().equals(
                                "Cell phone number successfully captured.")) {
                            System.out.println(message.checkRecipientCell());
                        }

                        if (!message.checkMessageLength().equals(
                                "Message ready to send.")) {
                            System.out.println(message.checkMessageLength());
                        }

                        if (messageText.trim().isEmpty()) {
                            System.out.println(
                                    "Message cannot be empty.");
                        }

                        System.out.println(
                                "Please correct the message details.");
                        System.out.println();

                        System.out.println(
                                "Enter recipient cell number (+27...):");
                        recipient = scanner.nextLine();

                        System.out.println("Enter your message:");
                        messageText = scanner.nextLine();

                        // Keep the same message ID and loop number
                        // while correcting an unsuccessful attempt.
                        message = new Message(
                                messageID,
                                messagesEntered,
                                recipient,
                                messageText);
                    }

                    System.out.println("Message ID: " + messageID);
                    System.out.println(message.checkRecipientCell());
                    System.out.println(message.checkMessageLength());

                    String hash = message.createMessageHash();
                    System.out.println("Message Hash: " + hash);

                    System.out.println("1) Send");
                    System.out.println("2) Disregard");
                    System.out.println("3) Store");

                    String action = scanner.nextLine();
                    String result;
                    boolean messageProcessed = false;

                    if (action.equals("1")) {

                        result = message.SentMessage(1);

                        if (result.equals("Message successfully sent.")) {
                            manager.addSentMessage(message);
                            messageProcessed = true;
                        }

                    } else if (action.equals("2")) {

                        System.out.println(message.SentMessage(2));
                        System.out.println(
                                "Enter 0 to delete the message:");

                        String deleteChoice = scanner.nextLine();

                        if (deleteChoice.equals("0")) {
                            manager.addDisregardedMessage(message);
                            result = "Message deleted successfully.";
                            messageProcessed = true;
                        } else {
                            result = "Invalid choice. Message was not discarded.";
                        }

                    } else if (action.equals("3")) {

                        result = message.SentMessage(3);

                        if (result.equals("Message successfully stored.")) {
                            manager.addStoredMessage(message);
                            messageProcessed = true;
                        }

                    } else {
                        result = "Invalid option.";
                    }

                    System.out.println(result);

                    // Only completed actions consume the message quota.
                    if (messageProcessed) {
                        messagesEntered++;
                    } else {
                        System.out.println(
                                "Message quota unchanged. "
                                + "You may try again.");
                    }

                    System.out.println("Total messages sent: "
                            + Message.returnTotalMessages());

                    break;

                case "2":

                    System.out.println("Coming Soon.");
                    break;

                case "3":

                    System.out.println("Thank you for using QuickChat.");
                    quit = true;
                    break;

                case "4":

                    storedMessagesMenu(
                            scanner, manager, cellPhoneNumber);
                    break;

                default:

                    System.out.println(
                            "Invalid option. Choose 1, 2, 3 or 4.");
            }
        }

        scanner.close();
    }

    /**
     * Displays the stored messages menu and its features.
     */
    private static void storedMessagesMenu(
            Scanner scanner,
            MessageManager manager,
            String sender) {

        // Load JSON only when no stored messages are available.
        if (manager.getStoredMessages().length == 0) {

            String loadResult =
                    manager.readStoredMessagesFromJSON("messages.json");

            System.out.println(loadResult);
        }

        boolean back = false;

        while (!back) {

            System.out.println("\n----------- STORED MESSAGES -----------");
            System.out.println("1) Display sender and recipient");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search by message ID");
            System.out.println("4) Search by recipient");
            System.out.println("5) Delete message using hash");
            System.out.println("6) Display stored message report");
            System.out.println("7) Display array counts");
            System.out.println("8) Back to main menu");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    System.out.println(
                            manager.displayStoredSendersAndRecipients(sender));
                    break;

                case "2":
                    System.out.println(
                            manager.displayLongestStoredMessage());
                    break;

                case "3":
                    System.out.println("Enter message ID:");
                    String id = scanner.nextLine();
                    System.out.println(manager.searchByMessageID(id));
                    break;

                case "4":
                    System.out.println("Enter recipient number:");
                    String searchRecipient = scanner.nextLine();
                    System.out.println(
                            manager.searchByRecipient(searchRecipient));
                    break;

                case "5":
                    System.out.println("Enter message hash:");
                    String searchHash = scanner.nextLine();
                    System.out.println(manager.deleteByHash(searchHash));
                    break;

                case "6":
                    System.out.println(manager.displayStoredReport());
                    break;

                case "7":
                    System.out.println(manager.displayArrayCounts());
                    break;

                case "8":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
