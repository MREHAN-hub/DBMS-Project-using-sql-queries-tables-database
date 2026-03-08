package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://DESKTOP-6RJULTB:3306/BloodBankDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";              // ✅ your MySQL username
        String password = "Tron13Legacy";   // ✅ your MySQL password

        Class.forName("com.mysql.cj.jdbc.Driver"); // ✔ MySQL JDBC driver
        return DriverManager.getConnection(url, username, password);
    }
}
