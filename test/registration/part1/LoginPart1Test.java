package registration.part1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for the LoginPart1 registration and login functionality.
 */
public class LoginPart1Test {

    private LoginPart1 login;

    @Before
    public void setUp() {
        login = new LoginPart1("Kyle", "Smith");
    }

    @Test
    public void testValidUsername() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testInvalidUsername() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testValidEmail() {
        assertTrue(login.checkEmail("kyle@example.com"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(login.checkEmail("kyleexample.com"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testValidCellPhoneNumber() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testInvalidCellPhoneNumber() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testSuccessfulRegistration() {

        String result = login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals("Registration successful.", result);
    }

    @Test
    public void testInvalidUsernameRegistration() {

        String result = login.registerUser(
                "kyle!!!!!!!",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Username is not correctly formatted, please ensure that your username "
                + "contains an underscore and is no more than five characters in length.",
                result
        );
    }

    @Test
    public void testInvalidEmailRegistration() {

        String result = login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyleexample.com"
        );

        assertEquals(
                "Email address is not correctly formatted; please enter a valid email address.",
                result
        );
    }

    @Test
    public void testInvalidPasswordRegistration() {

        String result = login.registerUser(
                "kyl_1",
                "password",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, "
                + "and a special character.",
                result
        );
    }

    @Test
    public void testInvalidCellPhoneRegistration() {

        String result = login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "08966553",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Cell phone number incorrectly formatted or does not contain international code.",
                result
        );
    }

    @Test
    public void testSuccessfulLoginWithUsername() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertTrue(
                login.loginUser("kyl_1", "Ch&&sec@ke99!")
        );
    }

    @Test
    public void testSuccessfulLoginWithEmail() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertTrue(
                login.loginUser("kyle@example.com", "Ch&&sec@ke99!")
        );
    }

    @Test
    public void testFailedLogin() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertFalse(
                login.loginUser("kyl_1", "wrongPassword")
        );
    }

    @Test
    public void testSuccessfulLoginStatusWithUsername() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Welcome Kyle Smith it is great to see you again.",
                login.returnLoginStatus(
                        "kyl_1",
                        "Ch&&sec@ke99!"
                )
        );
    }

    @Test
    public void testSuccessfulLoginStatusWithEmail() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Welcome Kyle Smith it is great to see you again.",
                login.returnLoginStatus(
                        "kyle@example.com",
                        "Ch&&sec@ke99!"
                )
        );
    }

    @Test
    public void testFailedLoginStatus() {

        login.registerUser(
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976",
                "Kyle",
                "Smith",
                "kyle@example.com"
        );

        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus(
                        "kyl_1",
                        "wrongPassword"
                )
        );
    }
    
    @Test
    public void testUsernameWithNoUnderscore() {
        assertFalse(login.checkUserName("kyle1"));
    }

    @Test
    public void testPasswordWithoutCapitalLetter() {
        assertFalse(login.checkPasswordComplexity("password1!"));
    }

    @Test
    public void testCellPhoneWithoutInternationalCode() {
        assertFalse(login.checkCellPhoneNumber("0838968976"));
    }
}