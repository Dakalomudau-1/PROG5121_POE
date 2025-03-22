/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
  import java.util.*;
import java.io.*;
import java.nio.file.Files;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author RC_Student_lab
 */
public class ChatApp {
    private static boolean isLoggedIn = false;
    private static final List<String> messages = new ArrayList<>();
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_PATH = "messages.json";
    private static Login login;
    private static int messageCount = 0;

    public static void main(String[] args) {
        try (scanner) {
            registerUser();
            loginUser();
            displayMenu();
        }
    }

    private static void registerUser() {
        System.out.println("🔹 QuickChat - User Registration");

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

        UserRegistration user = new UserRegistration(username, password, phoneNumber);
        if (!user.validateAll()) {
            System.out.println("❌ Registration failed.");
            System.exit(0);
        }

        login = new Login(username, password, firstName, lastName);
        System.out.println("\n✅ Registration successful! Please log in.\n");
    }

    private static void loginUser() {
        System.out.println("🔹 QuickChat - Login");

        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        if (login.authenticate(loginUsername, loginPassword)) {
            isLoggedIn = true;
            System.out.println("\n✅ Login successful!");
            System.out.println(login.getWelcomeMessage() + "\n");
        } else {
            System.out.println("❌ Username or password incorrect.");
            System.exit(0);
        }
    }

    private static void displayMenu() {
        while (true) {
            System.out.println("\n📌 Choose an option:");
            System.out.println("1️⃣ Send Messages");
            System.out.println("2️⃣ Show All Sent Messages");
            System.out.println("3️⃣ View Stored Messages");
            System.out.println("4️⃣ Quit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> sendMessages();
                case 2 -> showAllMessages();
                case 3 -> viewStoredMessages();
                case 4 -> {
                    System.out.println("🚪 Exiting QuickChat. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static void sendMessages() {
        if (!isLoggedIn) {
            System.out.println("❌ You must be logged in to send messages.");
            return;
        }

        System.out.println("\n✉️ Enter details for your message");

        String messageID = generateMessageID();
        messageCount++;

        String recipient;
        while (true) {
            System.out.print("Enter recipient phone number (+27xxxxxxxxx): ");
            recipient = scanner.nextLine();
            if (recipient.matches("\\+27[6-8]\\d{8}")) break;
            System.out.println("❌ Invalid phone number! Must start with +27.");
        }

        String message;
        while (true) {
            System.out.print("Enter your message (Max 250 characters): ");
            message = scanner.nextLine();
            if (message.length() <= 250) break;
            System.out.println("❌ Message too long!");
        }

        Message msg = new Message(messageID, recipient, message);

        while (true) {
            System.out.println("\n🔹 Choose an action:");
            System.out.println("1️⃣ Send Message Now");
            System.out.println("2️⃣ Disregard Message");
            System.out.println("3️⃣ Store Message for Later");
            System.out.print("Enter your choice: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    messages.add(msg.formatMessage());
                    JOptionPane.showMessageDialog(null, msg.formatMessage(), "📩 Message Sent", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("✅ Message sent!");
                    return;
                }
                case 2 -> {
                    System.out.println("🗑️ Message discarded.");
                    return;
                }
                case 3 -> {
                    msg.storeMessage();
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }


    private static void viewStoredMessages() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                System.out.println("📭 No stored messages.");
                return;
            }

            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONArray messageArray = new JSONArray(content);

            System.out.println("\n📜 Stored Messages:");
            for (int i = 0; i < messageArray.length(); i++) {
                System.out.println(formatMessage(
                    messageArray.getJSONObject(i).getString("MessageID"),
                    messageArray.getJSONObject(i).getString("MessageHash"),
                    messageArray.getJSONObject(i).getString("Recipient"),
                    messageArray.getJSONObject(i).getString("Message")
                ));
                System.out.println("--------------------");
            }
        } catch (IOException | JSONException e) {
            System.out.println("❌ Error reading stored messages.");
        }
    }

    private static void showAllMessages() {
        if (messages.isEmpty()) {
            System.out.println("📭 No messages sent yet.");
            return;
        }

        StringBuilder allMessages = new StringBuilder("📜 All Sent Messages:\n");
        for (String msg : messages) {
            allMessages.append(msg).append("\n--------------------\n");
        }

        JOptionPane.showMessageDialog(null, allMessages.toString(), "📩 All Sent Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String generateMessageID() {
        return String.valueOf(1000000000L + random.nextInt(900000000));
    }

    private static String formatMessage(String messageID, String messageHash, String recipient, String message) {
        return "📩 Message ID: " + messageID + "\n🔹 Hash: " + messageHash + "\n👤 To: " + recipient + "\n📝 " + message;
    }

    // Message Class (to be used in ChatApp)
    public static final class Message {
        private String messageID;
        private String messageHash;
        private String recipient;
        private String messageContent;
        private static final List<String> sentMessages = new ArrayList<>();
        private static final String FILE_PATH = "messages.json"; // Path to the file where messages are stored

        // Constructor to initialize the Message object
        public Message(String messageID, String recipient, String messageContent) {
            if (!checkMessageID(messageID)) {
                throw new IllegalArgumentException("Message ID must not exceed 10 characters.");
            }

            if (!checkRecipientCell(recipient)) {
                throw new IllegalArgumentException("Recipient cell number must be valid.");
            }

            this.messageID = messageID;
            this.recipient = recipient;
            this.messageContent = messageContent;
            this.messageHash = createMessageHash();  // Generate hash on message creation
        }

        // Method to check if the message ID is not more than ten characters
        public boolean checkMessageID(String messageID) {
            return messageID.length() <= 10;
        }

        // Method to check if the recipient's cell number is valid
        public boolean checkRecipientCell(String recipient) {
            return recipient.length() == 13 && recipient.startsWith("+27");
        }

        // Method to create a message hash based on ID, recipient, and message content
        public String createMessageHash() {
            return (messageID.substring(0, 2) + ":" + recipient.substring(1, 4) + ":" + messageContent.hashCode());
        }

        // Method to store the message in a JSON file
        public void storeMessage() {
            try {
                JSONArray messageArray = new JSONArray();
                File file = new File(FILE_PATH);

                // If the file exists, read existing messages and add the new message to it
                if (file.exists()) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    if (!content.isEmpty()) {
                        messageArray = new JSONArray(content);
                    }
                }

                // Create a new JSONObject for the message
                JSONObject newMessage = new JSONObject();
                newMessage.put("MessageID", messageID);
                newMessage.put("MessageHash", messageHash);
                newMessage.put("Recipient", recipient);
                newMessage.put("Message", messageContent);

                // Add the new message to the JSON array
                messageArray.put(newMessage);

                // Write the updated array back to the file
                try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
                    fileWriter.write(messageArray.toString(4)); // Pretty print with 4 spaces
                }

                System.out.println("📥 Message stored for later!");

            } catch (IOException e) {
                System.out.println("❌ Error storing message.");
            }
        }

        // Method to return the formatted message
        public String formatMessage() {
            return "📩 Message ID: " + messageID + "\n🔹 Hash: " + messageHash + "\n👤 To: " + recipient + "\n📝 " + messageContent;
        }

        // Method to return the total number of messages sent
        public static int returnTotalMessage() {
            return sentMessages.size(); // Return the size of the list of sent messages
        }
    }

    private static class Login {
        private final String username;
        private final String password;
        private final String firstName;
        private final String lastName;

        public Login(String username, String password, String firstName, String lastName) {
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public boolean authenticate(String username, String password) {
            return this.username.equals(username) && this.password.equals(password);
        }

        public String getWelcomeMessage() {
            return "Welcome, " + firstName + " " + lastName + "!";
        }
    }

    // UserRegistration Class
    public static class UserRegistration {
        private final String username;
        private final String password;
        private final String phoneNumber;

        public UserRegistration(String username, String password, String phoneNumber) {
            this.username = username;
            this.password = password;
            this.phoneNumber = phoneNumber;
        }

        public boolean validateAll() {
            return validateUsername() && validatePassword() && validatePhoneNumber();
        }

        private boolean validateUsername() {
            return username != null && !username.isEmpty();
        }

        private boolean validatePassword() {
            return password != null && !password.isEmpty();
        }

        private boolean validatePhoneNumber() {
            return phoneNumber.matches("\\+27[6-8]\\d{8}");
        }
    }
}
