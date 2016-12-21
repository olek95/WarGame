package wargame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;

/**
 * Obiekt klacy <code>DatabaseManager</code> reprezentuje zarządcę bazy danych 
 * dla gry WarGame. Baza ta przechowuje informacje na temat nicku użytkownika
 * oraz ilości pieniedzy i punktów uzyskanych w grze. Informacje są przechowywane 
 * w tabeli Statistics. Zarządca umożliwia komunikację pomiędzy bazą danych a grą. 
 * @author AleksanderSklorz
 */
public class DatabaseManager {
    private static DatabaseManager manager;
    private static Connection conn;
    private DatabaseManager(String jdbcDriver, String url, String user, String password) throws ClassNotFoundException, SQLException{
        Class.forName(jdbcDriver);
        conn = DriverManager.getConnection(url, user, password);
    }
    /**
     * Tworzy zarządcę bazy danych jeśli nie istnieje. 
     * @param jdbcDriver używany sterownik 
     * @param url adres url do bazy danych 
     * @param user nazwa użytkownika 
     * @param password hasło użytkownika 
     * @return zarządcę bazy danych 
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public static synchronized DatabaseManager createDatabaseManager(String jdbcDriver, String url, String user, String password) throws ClassNotFoundException, SQLException{
        if(manager == null) manager = new DatabaseManager(jdbcDriver, url, user, password); 
        return manager;
    }
    /**
     * Dodaje nowego gracza do bazy danych. Wpisuje tylko jego login, natomiast 
     * kolumnę z pieniędzmi i punktami uzupełnia zerami, gdyż jest to nowy gracz. 
     * @param login login gracza. 
     */
    public void addPlayer(String login){
        try(PreparedStatement stat = conn.prepareStatement("INSERT INTO Statistics VALUES(?, 0, 0)")){
            stat.setString(1, login);
            stat.executeUpdate();
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    /**
     * Sprawdza czy gracz o podanym loginie istnieje w bazie danych. 
     * @param login true jeśli gracz istnieje, false w przeciwnym wypadku. 
     * @return 
     */
    public boolean existsPlayer(String login){
        try(PreparedStatement stat = conn.prepareStatement("SELECT login FROM Statistics WHERE login = ?")){
            stat.setString(1, login);
            ResultSet rs = stat.executeQuery();
            return rs.next();
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
    /**
     * Uzupełnia obszar tekstowy w oknie dialogowym statystyką pobraną z tabeli
     * Statistics. Wyświetla login, pieniądze oraz punkty w formacie: 
     * Login|Pieniądze|Punkty. Każdy rekord jest wyświetlany w osobnej linii. 
     * @param statisticsDialog okno dialogowe wyświetlające statystykę. 
     */
    public void showStatistics(Dialog<String> statisticsDialog){
        TextArea statisticsTextArea = (TextArea)statisticsDialog.getDialogPane().getContent();
        statisticsTextArea.appendText("Login|Pieniądze|Punkty\n");
        try(Statement stat = conn.createStatement()){
            ResultSet rs = stat.executeQuery("SELECT * FROM Statistics");
            while(rs.next())
                statisticsTextArea.appendText(rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getInt(3) + "\n");
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    /**
     * Aktualizuje wyniki gracza w bazie danych. Aktualizowane informacje to: 
     * stan konta oraz liczba punktów uzyskanych podczas gry. 
     */
    public static void saveScore(){
        try(PreparedStatement stat = conn.prepareStatement("UPDATE Statistics SET cash = ?, points = ? WHERE login = ?")){
            WarGameFXMLController controller = WarGameFXMLController.getController();
            stat.setString(1, controller.getCashTextField().getText());
            stat.setString(2, controller.getPointsTextField().getText());
            stat.setString(3, Player.getLogin());
            stat.executeUpdate();
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    /**
     * Zwraca ilość pieniędzy zapisaną w bazie danych dla danego użytkownika. 
     * @param login login użytkownika 
     * @return ilość pieniędzy w bazie danych 
     */
    public static String getCash(String login){
        try(PreparedStatement stat = conn.prepareStatement("SELECT cash FROM Statistics WHERE login = ?")){
            stat.setString(1, login);
            ResultSet rs = stat.executeQuery(); 
            rs.next(); 
            return rs.getString(1);
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    /**
     * Zwraca ilość punktów zapisaną w bazie danych dla danego użytkownika. 
     * @param login login użytkownika 
     * @return ilość punktów w bazie danych 
     */
    public static String getPoints(String login){
        try(PreparedStatement stat = conn.prepareStatement("SELECT points FROM Statistics WHERE login = ?")){
            stat.setString(1, login);
            ResultSet rs = stat.executeQuery(); 
            rs.next(); 
            return rs.getString(1);
        }catch(SQLException e){
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
}


