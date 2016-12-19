package wargame;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class WarGameFXMLController implements Initializable {
    private static double BATTLEFIELD_WIDTH, MARGIN;
    private static WarGameFXMLController controller;
    private MilitaryUnit unit;
    private Player p;
    private Enemy e;
    private GameManager manager;
    Thread t;
    @FXML
    private Button buySoldierButton, buyTankButton, buyHelicopterButton, exitButton;
    @FXML
    private Pane battlefieldPane;
    @FXML
    private TextArea battleTextArea;
    @FXML
    private TextField cashTextField, pointsTextField;
    @FXML
    private void play(ActionEvent event){
        if(p != null) battlefieldPane.getChildren().clear(); // jeśli gracz nie jest nullem to gra się 2 raz, więc czyszczę planszę
        p = new Player();
        e = new Enemy();
        manager.setGameover(false);
        buySoldierButton.setOnAction(value -> {
            unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
            if(!manager.changeCash(unit.price)) unit = null;
        });
        battlefieldPane.setOnMouseClicked(click -> {
            if(unit != null){
                battlefieldPane.getChildren().add(unit.getImg());
                p.addUnit(unit);
                unit.getImg().setLayoutY(getLocationY(click.getY()));
                new Thread(unit).start();
                unit = null;
            }
        });
        t = new Thread(e);
        t.start();
    }
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        controller = this;
        manager = new GameManager();
        manager.setInitialValues();
        BATTLEFIELD_WIDTH = battlefieldPane.getPrefWidth();
        MARGIN = 165;
       /*MilitaryUnit u = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
       battlefieldPane.getChildren().add(u.getImg());
       e.addUnit(u);
        u.getImg().setLayoutX(BATTLEFIELD_WIDTH - u.getImg().getImage().getWidth());
        u.getImg().setLayoutY(0);
        new Thread(u).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
          MilitaryUnit u2 = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
        u2.getImg().setLayoutX(BATTLEFIELD_WIDTH - u2.getImg().getImage().getWidth());
        u2.getImg().setLayoutY(97);
         battlefieldPane.getChildren().add(u2.getImg());
         e.addUnit(u2);
         new Thread(u2).start();*/
        exitButton.setOnAction(value -> {
            System.exit(0);
        });
    }    
    public double getLocationY(double click){
        if(click < 97) return 0; // 97 to wysokość najwyższego obrazka 
        else if(click > 97 && click < 194) return 97;
        else if(click > 194 && click < 291) return 194;
        return 291;
    }
    public static double getBattlefieldWidth(){
        return BATTLEFIELD_WIDTH;
    }
    public static double getMargin(){
        return MARGIN;
    }
    public static WarGameFXMLController getController(){
        return controller;
    }
    public Pane getBattlefieldPane(){
        return battlefieldPane;
    }
    public TextArea getBattleTextArea(){
        return battleTextArea;
    }
    public TextField getCashTextField(){
        return cashTextField;
    }
    public TextField getPointsTextField(){
        return pointsTextField;
    }
    public void set(){
        battlefieldPane = null;
    }
}
