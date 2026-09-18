package registration.part1;

/**
 * LoginPart1
 *
 * This class contains the methods used to validate registration
 * details and verify the user's login details.
 */
public class LoginPart1 {

    private String registeredUsername;
    private String registeredPassword;
    private String registeredFirstName;
    private String registeredSurname;
    private String registeredCellPhoneNumber;
    private String registeredEmail;

    /**
     * Constructor for the LoginPart1 class.
     *
     * @param firstName the user's first name
     * @param surname the user's surname
     */
    public LoginPart1(String firstName, String surname) {
        this.registeredFirstName = firstName;
        this.registeredSurname = surname;
    }

    /**
     * Checks whether the username contains an underscore
    * and is no more than five characters long.
    */
    public boolean checkUserName(String username) {

        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    /**
    * Checks whether the email address has a basic valid format.
    *
    * @param email the email address to validate
    * @return true if the email address is correctly formatted,
    *         otherwise false
    */
    public boolean checkEmail(String email) {

        return email != null
                && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
    * Checks whether the password has at least eight characters,
     * a capital letter, a number, and a special character.
    */
    public boolean checkPasswordComplexity(String password) {

        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter
                && hasNumber
                && hasSpecialCharacter;
    }

    /**
     * Checks whether the South African cell phone number is correctly formatted.
    *
     * The number must start with +27 followed by 9 digits.
     *
     * Regex reference:
     * Oracle Java Pattern documentation:
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     *
     * @param cellPhoneNumber the cell phone number to validate
     * @return true if the cell phone number is correctly formatted,
     *         otherwise false
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {

        // Regex uses Java Pattern syntax.
        // Reference: Oracle Java Pattern documentation.
        String cellPhoneRegex = "^\\+27[0-9]{9}$";

        return cellPhoneNumber != null
                && cellPhoneNumber.matches(cellPhoneRegex);
    }

    /**
     * Registers the user if all required details are valid.
     *
     * @param username the username
     * @param password the password
     * @param cellPhoneNumber the user's cell phone number
     * @param firstName the user's first name
     * @param surname the user's surname
     * @param email the user's email address
     * @return the appropriate registration message
     */
    public String registerUser(
            String username,
            String password,
            String cellPhoneNumber,
            String firstName,
            String surname,
            String email) {

        if (!checkUserName(username)) {

            return "Username is not correctly formatted, please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }

        if (!checkEmail(email)) {

            return "Email address is not correctly formatted; please enter a valid email address.";
        }

        if (!checkPasswordComplexity(password)) {

            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, "
                    + "and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {

            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredFirstName = firstName;
        registeredSurname = surname;
        registeredCellPhoneNumber = cellPhoneNumber;
        registeredEmail = email;

        return "Registration successful.";
    }

    /**
    * Checks whether the supplied username or email and password
    * match the registered login details.
    *
    * @param usernameOrEmail the username or email entered by the user
    * @param password the password entered by the user
    * @return true if the login details are correct, otherwise false
    */
    public boolean loginUser(String usernameOrEmail, String password) {

        boolean correctUsername = usernameOrEmail != null
                && usernameOrEmail.equals(registeredUsername);

        boolean correctEmail = usernameOrEmail != null
                && usernameOrEmail.equals(registeredEmail);

        boolean correctPassword = password != null
                && password.equals(registeredPassword);

        return (correctUsername || correctEmail) && correctPassword;
    }

    /**
    * Returns a message showing whether the login was successful.
    *
    * @param usernameOrEmail the username or email entered by the user
    * @param password the password entered by the user
    * @return a message showing the login result
    */
    public String returnLoginStatus(
            String usernameOrEmail,
            String password) {

        if (loginUser(usernameOrEmail, password)) {

            return "Welcome "
                    + registeredFirstName
                    + " "
                    + registeredSurname
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}