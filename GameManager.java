package wargame;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class GameManager {
    private static GameManager manager;
    private static boolean gameover;
    public static synchronized GameManager createGameManager(){
        if(manager == null) manager = new GameManager(); 
        return manager;
    }
    public void setInitialValues(){
        WarGameFXMLController controller = WarGameFXMLController.getController();
        controller.getCashTextField().setText("20");
        controller.getPointsTextField().setText("0");
        controller.getBattleTextArea().setText("1.\n2.\n3.\n4.\n");
    }
    public boolean changeCash(int price){
        TextField cashTextField = WarGameFXMLController.getController().getCashTextField();
        int cash = (Integer.parseInt(cashTextField.getText()) - price);
        if(cash >= 0){
            cashTextField.setText(cash + "");
            return true;
        }
        return false;
    }
    public void changePoints(int points){
        TextField pointsTextField = WarGameFXMLController.getController().getPointsTextField();
        pointsTextField.textProperty().set((Integer.parseInt(pointsTextField.getText()) + points) + "");
    }
    public void showEndGameAlerts(){
        Alert endGameAlert = new Alert(AlertType.CONFIRMATION);
        endGameAlert.setTitle("Koniec gry");
        if(Player.getBase() != null) endGameAlert.setHeaderText("WYGRAŁEŚ!!");
        else endGameAlert.setHeaderText("PRZEGRAŁEŚ!!");
        if(!Player.getLogin().toLowerCase().equals("anonim")){
            endGameAlert.setContentText("Czy chcesz zapisać wynik?");
            endGameAlert.showAndWait().ifPresent(option -> {
                if(option.equals(ButtonType.OK)) DatabaseManager.saveScore();
            });
        }else endGameAlert.showAndWait();
    }
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
    public void setGameover(boolean value){
        gameover = value;
    }
    public boolean getGameover(){
        return gameover;
    }
}
