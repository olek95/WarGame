package wargame;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        if(Player.getBase() != null) endGameAlert.setHeaderText("WYGRAŁEŚ!!");
        else endGameAlert.setHeaderText("PRZEGRAŁEŚ!!");
        endGameAlert.setContentText("Czy chcesz zapisać wynik?");
        endGameAlert.showAndWait();
    }
    public void setGameover(boolean value){
        gameover = value;
    }
    public boolean getGameover(){
        return gameover;
    }
}
