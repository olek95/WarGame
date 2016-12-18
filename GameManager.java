package wargame;

import javafx.scene.control.TextField;

public class GameManager {
    private static GameManager manager;
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
}
