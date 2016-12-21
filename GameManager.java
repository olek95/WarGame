package wargame;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

/**
 * Obiekt klasy <code>GameManager</code> reprezentuje zarządcę gry, który steruje 
 * niektórymi jej elementami. Może istnieć tylko jeden zarządca, jeśli już jakiś
 * istnieje jest on zwracany podczas tworzenia. 
 * @author AleksanderSklorz
 */
public class GameManager {
    private static GameManager manager;
    private static boolean gameover;
    private static boolean interruptedGame;
    private GameManager(){
    }
    /**
     * Tworzy zarządcę sterującego niektórymi elementami gry. Jeśli istnieje 
     * zwraca istniejącego zarządcę, bez tworzenia nowego. 
     * @return zarządca gry
     */
    public static synchronized GameManager createGameManager(){
        if(manager == null) manager = new GameManager(); 
        return manager;
    }
    /**
     * Ustawia początkowe wartości w grze. Ustawiane wartości to: ilość 
     * pieniędzy na 20, aktualny stan punktów na 0 oraz początkowy stan obszaru 
     * z wynikami bitew. 
     */
    public void setInitialValues(){
        WarGameFXMLController controller = WarGameFXMLController.getController();
        controller.getCashTextField().setText("20");
        controller.getPointsTextField().setText("0");
        controller.getBattleTextArea().setText("1.\n2.\n3.\n4.\n");
    }
    /**
     * Zmienia aktualny stan pieniędzy gracza. Sprawdza czy dana zmiana jest 
     * możliwa. 
     * @param price ilość pieniędzy o jaką ma się zmienić stan konta gracza.
     * @return true, jeśli gracz miał wystarczającą ilość pieniedzy do zmiany, false w przeciwnym razie 
     */
    public boolean changeCash(int price){
        TextField cashTextField = WarGameFXMLController.getController().getCashTextField();
        int cash = (Integer.parseInt(cashTextField.getText()) - price);
        if(cash >= 0){
            cashTextField.setText(cash + "");
            return true;
        }
        return false;
    }
    /**
     * Zmienia aktualną liczbę punktów gracza. 
     * @param points liczba punktów o ile mają zmienić się punkty gracza
     */
    public void changePoints(int points){
        TextField pointsTextField = WarGameFXMLController.getController().getPointsTextField();
        pointsTextField.textProperty().set((Integer.parseInt(pointsTextField.getText()) + points) + "");
    }
    /**
     * Wyświetla alert informujący o końcu gry. Podaje informację kto wygrał
     * oraz jeśli gracz nie był anonimem - pozwala na zapisanie wyniku do bazy 
     * danych. 
     */
    public void showEndGameAlerts(){
        Alert endGameAlert = new Alert(AlertType.CONFIRMATION);
        endGameAlert.setTitle("Koniec gry");
        if(Player.getBase() != null) endGameAlert.setHeaderText("WYGRAŁEŚ!!");
        else endGameAlert.setHeaderText("PRZEGRAŁEŚ!!");
        if(!Player.getLogin().toLowerCase().equals("anonim")){
            String login = Player.getLogin();
            endGameAlert.setContentText("Czy chcesz zapisać wynik?\nAktualny stan w bazie to: kasa - " + DatabaseManager.getCash(login)
            + " punkty - " + DatabaseManager.getPoints(login));
            endGameAlert.showAndWait().ifPresent(option -> {
                if(option.equals(ButtonType.OK)) DatabaseManager.saveScore();
            });
        }else endGameAlert.showAndWait();
    }
    /**
     * Zwiększa aktualny stan konta gracza, co 20 sekund o 5. 
     */
    public void increaseCash(){
        new Thread(() -> {
            do{
                try{
                    synchronized(this){
                        wait(20000);
                    }
                }catch(InterruptedException e){
                    Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, e);
                }
                if(!gameover){
                    TextField cashTextField = WarGameFXMLController.getController().getCashTextField();
                    cashTextField.setText(((Integer.parseInt(cashTextField.getText())) + 5) + "");
                }
            }while(!gameover);
        }).start();
    }
    /**
     * Ustawia koniec gry.
     * @param value true oznacza koniec gry, false grę jeszcze nieskończoną
     */
    public void setGameover(boolean value){
        gameover = value;
    }
    /**
     * Zwraca koniec gry. 
     * @return true oznacza koniec gry, false grę jeszcze nieskończoną
     */
    public boolean getGameover(){
        return gameover;
    }
    /**
     * Ustawia przerwanie gry, czyli sytuację gdy gracz rozpoczął nową grę, 
     * przerywając aktualną. 
     * @param value true - przerwanie gry, false - gra nieprzerwana
     */
    public void setInterruptedGame(boolean value){
        interruptedGame = value; 
    }
    /**
     * Zwraca przerwanie gry, czyli sytuację gdy gracz rozpoczął nową grę, 
     * przerywając aktualną. 
     * @return true - przerwanie gry, false - gra nieprzerwana
     */
    public boolean getInterruptedGame(){
        return interruptedGame;
    }
}
