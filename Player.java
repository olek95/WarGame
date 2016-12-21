package wargame;

import java.util.ArrayList;
import javafx.scene.image.ImageView;

/**
 * Obiekt klasy <code>Player</code> reprezentuje gracza w grze.
 * @author olek1
 */
public class Player {
    private static ArrayList<MilitaryUnit> units = new ArrayList();
    private static String login;
    public Player(){
        units = new ArrayList();
        units.add(MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.BASE));
        if(login == null) login = "Anonim";
    }
    /**
     * Dodaje nowe jednostki do listy przechowującej wszystkie jednostki gracza. 
     * @param unit dodawana jednostka. 
     */
    public void addUnit(MilitaryUnit unit){
        units.add(unit);
    }
    /**
     * Zwraca listę przechowującą wszystkie jednostki gracza. 
     * @return lista z jednostkami gracza. 
     */
    public static ArrayList<MilitaryUnit> getUnits(){
        return units;
    }
    /**
     * Sprawdza na podstawie obiektu przechowującego obrazek, czy dana jednostka
     * znajduje się na liście jednostek gracza, czyli czy gracz posiada tą jednostke.
     * @param img komponent przechowujący obrazek jednostki. 
     * @return true jeśli gracz posiada daną jednostkę, false w przeciwnym razie. 
     */
    public static boolean hasImageView(ImageView img){
        if(img == null) return false;
        for(MilitaryUnit unit : units)
            if(unit.getImg().equals(img)) return true;
        return false;
    }
    /**
     * Usuwa podaną jednostkę z listy jednostek gracza. 
     * @param unit jednostka do usunięcia. 
     */
    public static void remove(MilitaryUnit unit){
        units.remove(unit);
    }
    /**
     * Zwraca bazę gracza. Ma zabezpieczenie na wypadek gdyby została już usunięta.
     * @return baza gracza. 
     */
    public static MilitaryUnit getBase(){
        if(units.size() > 0 && units.get(0) instanceof Base)  return units.get(0);
        return null;
    }
    /**
     * Ustawia login gracza.
     * @param login login gracza.
     */
    public static void setLogin(String login){
        Player.login = login;
    }
    /**
     * Pobiera login gracza.
     * @return login gracza. 
     */
    public static String getLogin(){
        return login;
    }
}
