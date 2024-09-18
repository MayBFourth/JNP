import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Server {
    private static Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/jnp";
        String user = "root";
        String password = "Trancuongphu14@";
        return DriverManager.getConnection(url, user, password);
    }

    private static boolean validateLogin(String username, String password) throws SQLException {
        Connection conn = connectToDatabase();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private static ArrayList<String> getUsers() throws SQLException {
        Connection conn = connectToDatabase();
        String query = "SELECT username FROM users";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ArrayList<String> users = new ArrayList<>();
        while (rs.next()) {
            users.add(rs.getString("username"));
        }
        return users;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                String username = dis.readUTF();
                String password = dis.readUTF();

                if (validateLogin(username, password)) {
                    dos.writeUTF("SUCCESS");
                    ArrayList<String> users = getUsers();

                    dos.writeInt(users.size());
                    for (String user : users) {
                        dos.writeUTF(user);
                    }
                } else {
                    dos.writeUTF("FAIL");
                }

                dis.close();
                dos.close();
                socket.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
