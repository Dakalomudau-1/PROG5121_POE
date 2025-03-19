/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
   import java.util.Scanner;

/**
 *
 * @author RC_Student_lab
 */
public class ChatApp {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            
            System.out.print("Enter South African phone number (+27831234567): ");
            String phoneNumber = scanner.nextLine();
            
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            
            // Validate user registration
            UserRegistration user = new UserRegistration(username, password, phoneNumber);
            if (!user.validateAll()) {
                System.out.println(" Registration failed. Please fix errors and try again.");
                return;
            }
            
            // Store login details
            Login login = new Login(username, password, firstName, lastName);
            
            // Login authentication
            System.out.print("\nEnter username to log in: ");
            String loginUsername = scanner.nextLine();
            
            System.out.print("Enter password to log in: ");
            String loginPassword = scanner.nextLine();
            
            if (login.authenticate(loginUsername, loginPassword)) {
                System.out.println(login.getWelcomeMessage());
            } else {
                System.out.println(" Username or password incorrect. Please try again.");
            }
        }
    }
}

class UserRegistration {
    private final String username;
    private final String password;
    private final String phoneNumber;

    public UserRegistration(String username, String password, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public boolean validateUsername() {
        return username.contains("_") && username.length() >= 6;
    }

    public boolean validatePassword() {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    }

    public boolean validatePhoneNumber() {
        return phoneNumber.matches("^\\+27[6-8]\\d{8}$");
    }

    public boolean validateAll() {
        boolean validUsername = validateUsername();
        boolean validPassword = validatePassword();
        boolean validPhoneNumber = validatePhoneNumber();

        if (validUsername) {
            System.out.println(" Username successfully captured.");
        } else {
            System.out.println(" Username must contain an underscore and be at least 6 characters long.");
        }

        if (validPassword) {
            System.out.println(" Password successfully captured.");
        } else {
            System.out.println(" Password must be at least 8 characters long, include a capital letter, a number, and a special character.");
        }

        if (validPhoneNumber) {
            System.out.println(" Cell number successfully captured.");
        } else {
            System.out.println(" Invalid phone number format. Use: +27831234567.");
        }

        return validUsername && validPassword && validPhoneNumber;
    }
}

class Login {
    private final String storedUsername;
    private final String storedPassword;
    private final String firstName;
    private final String lastName;

    public Login(String storedUsername, String storedPassword, String firstName, String lastName) {
        this.storedUsername = storedUsername;
        this.storedPassword = storedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean authenticate(String inputUsername, String inputPassword) {
        return inputUsername.equals(storedUsername) && inputPassword.equals(storedPassword);
    }

    public String getWelcomeMessage() {
        return "🎉 Welcome " + firstName + " " + lastName + ", great to see you!";
    }
}
