package wargame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static DatabaseManager manager;
    private static Connection conn;
    private DatabaseManager(String jdbcDriver, String url, String user, String password) throws ClassNotFoundException, SQLException{
        Class.forName(jdbcDriver);
        conn = DriverManager.getConnection(url, user, password);
    }
    public void addPlayer(String login){
        try{
            PreparedStatement stat = conn.prepareStatement("INSERT INTO Statistics VALUES(?, 0, 0)");
            stat.setString(1, login);
            stat.executeUpdate();
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
