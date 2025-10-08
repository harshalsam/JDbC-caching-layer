import java.sql.*;
import java.util.*;

public class JDBCCache {
    static Map<Integer, String> cache = new HashMap<>();

    static Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:students.db");
    }

    static String getStudent(int id) throws SQLException {
        if (cache.containsKey(id)) {
            System.out.println("Cache hit: " + id);
            return cache.get(id);
        }
        System.out.println("Cache miss: " + id);
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT name FROM students WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) cache.put(id, rs.getString("name"));
            return rs.getString("name");
        }
    }

    static void updateStudent(int id, String name) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(
                "INSERT INTO students VALUES(?, ?) ON CONFLICT(id) DO UPDATE SET name=?")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, name);
            ps.executeUpdate();
        }
        cache.remove(id); // invalidate cache
        System.out.println("Cache invalidated: " + id);
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = connect()) {
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS students(id INTEGER PRIMARY KEY, name TEXT)");
        }
        updateStudent(1, "Bruce");
        System.out.println(getStudent(1)); // miss
        System.out.println(getStudent(1)); // hit
        updateStudent(1, "Sam");
        System.out.println(getStudent(1)); // miss again
    }
}Download the SQLite JDBC driver from here:
👉 https://github.com/xerial/sqlite-jdbc/releases

(Example: sqlite-jdbc-3.45.1.0.jar)

In Eclipse:

Right-click your project → Build Path → Configure Build Path → Libraries tab

Click Add External JARs...

Select the downloaded sqlite-jdbc.jar file → Apply and Close
