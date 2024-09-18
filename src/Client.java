import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private static JFrame frame;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static JTextArea userListArea;

    public static void main(String[] args) {
        frame = new JFrame("Login");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 30, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(150, 30, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 70, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 70, 200, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 110, 100, 30);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private static void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Socket socket = new Socket("localhost", 8080);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeUTF(username);
            dos.writeUTF(password);

            String response = dis.readUTF();

            if (response.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(frame, "Login thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                int userCount = dis.readInt();
                ArrayList<String> users = new ArrayList<>();
                for (int i = 0; i < userCount; i++) {
                    users.add(dis.readUTF());
                }

                showUserList(users);
            } else {
                JOptionPane.showMessageDialog(frame, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void showUserList(ArrayList<String> users) {
        JFrame listFrame = new JFrame("User List");
        listFrame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel searchLabel = new JLabel("Search User:");
        searchLabel.setBounds(10, 10, 100, 25);
        panel.add(searchLabel);

        JTextField searchField = new JTextField(20);
        searchField.setBounds(120, 10, 150, 25);
        panel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(280, 10, 80, 25);
        panel.add(searchButton);

        userListArea = new JTextArea();
        userListArea.setEditable(false);

        updateUserList(users);

        JScrollPane scrollPane = new JScrollPane(userListArea);
        scrollPane.setBounds(10, 50, 360, 300);
        panel.add(scrollPane);

        listFrame.add(panel);
        listFrame.setVisible(true);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().toLowerCase();
                ArrayList<String> filteredUsers = new ArrayList<>();

                for (String user : users) {
                    if (user.toLowerCase().contains(keyword)) {
                        filteredUsers.add(user);
                    }
                }

                updateUserList(filteredUsers);
            }
        });
    }

    private static void updateUserList(ArrayList<String> users) {
        userListArea.setText("");
        for (String user : users) {
            userListArea.append(user + "\n");
        }
    }

}
