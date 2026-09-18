package registration.part1;

import java.util.Scanner;

/**
 * RegistrationPart1
 *
 * This class runs the registration and login console application.
 */
public class RegistrationPart1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        LoginPart1 login = new LoginPart1("", "");

        System.out.println("==========================================");
        System.out.println("       REGISTRATION AND LOGIN SYSTEM");
        System.out.println("==========================================");
        System.out.println();

        // Collect the user's personal details.
        
        // Get first name
        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();

        // Get surname
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

        // Register the validated user details.
        String registrationMessage = login.registerUser(
                username,
                password,
                cellPhoneNumber,
                firstName,
                surname,
                email
        );

        System.out.println();
        System.out.println("==========================================");
        System.out.println("             REGISTRATION");
        System.out.println("==========================================");
        System.out.println(registrationMessage);

        // Allow the registered user to log in.
        System.out.println();
        System.out.println("==========================================");
        System.out.println("                  LOGIN");
        System.out.println("==========================================");
        System.out.println();
        System.out.println("You can log in using your username OR email.");
        System.out.println();

        System.out.println("Enter your username or email:");
        String loginUsernameOrEmail = scanner.nextLine();

        System.out.println("Enter your password:");
        String loginPassword = scanner.nextLine();

        String loginMessage = login.returnLoginStatus(
                loginUsernameOrEmail,
                loginPassword
        );

        System.out.println();
        System.out.println(loginMessage);

        scanner.close();
    }
}