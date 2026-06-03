package invox.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static Database instance;

    private final String url;
    private final String user;
    private final String password;

    private Database() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("[DB] Nu am putut citi db.properties, folosesc valori implicite.");
        }
        this.url = props.getProperty("db.url");
        if (url == null || url.isBlank()) {
            throw new RuntimeException("db.url lipseste din db.properties");
        }

        this.user = props.getProperty("db.user");
        if (user == null || user.isBlank()) {
            throw new RuntimeException("db.user lipseste din db.properties");
        }

        this.password = props.getProperty("db.password");
        if (password == null || password.isBlank()) {
            throw new RuntimeException("db.password lipseste din db.properties");
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
