package wargame;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
    private DatabaseManager dbManager;
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
        manager.setInitialValues();
        manager.setGameover(false);
        manager.increaseCash();
        t = new Thread(e);
        t.start();
    }
    @FXML
    private void signIn(ActionEvent event){
        TextInputDialog accountDialog = new TextInputDialog();
        accountDialog.setTitle("Konto");
        accountDialog.setContentText("Podaj login: ");
        boolean ok;
        do{
            Optional<String> result = accountDialog.showAndWait();
            ok = result.isPresent();
            if(ok){
                String login = result.get().trim();
                if(!login.equals("")){
                    if(login.length() <= 30){
                        if(!dbManager.existsPlayer(login)){
                            dbManager.addPlayer(login);
                            Player.setLogin(login);
                        }else{
                            Alert duplicateAlert = new Alert(AlertType.WARNING);
                            duplicateAlert.setTitle("Powtórzony login");
                            duplicateAlert.setContentText("Taki gracz już istnieje, podaj inny nick.");
                        }
                    }else{
                        Alert duplicateAlert = new Alert(AlertType.WARNING);
                        duplicateAlert.setTitle("Przekroczona długość");
                        duplicateAlert.setContentText("Login nie może przekraczać 30 znaków. Podaj krótszy login.");
                    }
                }else{
                    Alert badLoginAlert = new Alert(AlertType.WARNING);
                    badLoginAlert.setTitle("Zły login");
                    badLoginAlert.setContentText("Podałeś zły login. Login nie może być pusty.");
                }
            }
        }while(Player.getLogin() == null && ok);
    }
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        controller = this;
        manager = new GameManager();
        try{
            dbManager = DatabaseManager.createDatabaseManager("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/wargame?dontTrackOpenResources=true",
                    "olek", "haslo12345");
        }catch (ClassNotFoundException | SQLException e){
            Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        manager.setGameover(true);
        BATTLEFIELD_WIDTH = battlefieldPane.getPrefWidth();
        MARGIN = 165;
        buySoldierButton.setOnAction(value -> {
            if(!manager.getGameover()){
                unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
                if(!manager.changeCash(unit.price)) unit = null;
            }
        });
        buyTankButton.setOnAction(value -> {
            if(!manager.getGameover()){
                unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.TANK);
                if(!manager.changeCash(unit.price)) unit = null;
            }
        });
        buyHelicopterButton.setOnAction(value -> {
            if(!manager.getGameover()){
                unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.HELICOPTER);
                if(!manager.changeCash(unit.price)) unit = null;
            }
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
