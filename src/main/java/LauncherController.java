/**
 * Sample Skeleton for 'Launcher.fxml' Controller Class
 */

package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LauncherController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="serverOrClientButton"
    private ToggleButton serverOrClientButton; // Value injected by FXMLLoader

    @FXML // fx:id="portText"
    private Text portText; // Value injected by FXMLLoader

    @FXML // fx:id="userNameText"
    private Text userNameText; // Value injected by FXMLLoader

    @FXML // fx:id="ipText"
    private Text ipText; // Value injected by FXMLLoader

    @FXML // fx:id="portTextField"
    private TextField portTextField; // Value injected by FXMLLoader

    @FXML // fx:id="userNameTextField"
    private TextField userNameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="ipTextField"
    private TextField ipTextField; // Value injected by FXMLLoader

    @FXML
    void toggleServerOrClient(MouseEvent event) {
		userNameTextField.setText("");
		ipTextField.setText("");
    }

    @FXML
    void launch(MouseEvent event) {
		try{
			if(serverOrClientButton.isSelected()){
				String enteredPort = portTextField.getText();
				if(enteredPort.equals("") || !enteredPort.matches(".*\\d+.*")){
					Alert box = new Alert(AlertType.ERROR, "Please enter a numeric port.");
					box.showAndWait();
					return;
				}
				if(!NetworkUtilities.isPortAvailable("localhost", Integer.valueOf(enteredPort)) || 
					!NetworkUtilities.hasPermissionToBindPort(Integer.valueOf(enteredPort))){
					Alert box = new Alert(AlertType.ERROR, "The entered port is unavailable.");
					box.showAndWait();
					return;
				}
				URL url = getClass().getClassLoader().getResource("TextChatServerController.fxml");
				FXMLLoader fxmlLoader = new FXMLLoader(url);     

				Parent root = (Parent)fxmlLoader.load();          
				TextChatServerController controller = (TextChatServerController)fxmlLoader.getController();
				if(controller == null){
					System.out.println("Controller is null!");
				}
				controller.initPortAndIP(portTextField.getText(), NetworkUtilities.getExternalIp() + " / " + NetworkUtilities.getInternalIp());

				Scene scene = new Scene(root); 
				Stage stage = new Stage();
				stage.setTitle("TextChat Server");
				stage.setScene(scene);    

				stage.show();   
			}
			((Node)event.getSource()).getScene().getWindow().hide();

		}
		catch(Exception exc){
			exc.printStackTrace();
		}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert serverOrClientButton != null : "fx:id=\"serverOrClientButton\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert portText != null : "fx:id=\"portText\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert userNameText != null : "fx:id=\"userNameText\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert ipText != null : "fx:id=\"ipText\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert portTextField != null : "fx:id=\"portTextField\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert ipTextField != null : "fx:id=\"ipTextField\" was not injected: check your FXML file 'Launcher.fxml'.";

	userNameText.visibleProperty().bind(serverOrClientButton.selectedProperty().not());
	userNameTextField.visibleProperty().bind(serverOrClientButton.selectedProperty().not());
	ipText.visibleProperty().bind(serverOrClientButton.selectedProperty().not());
	ipTextField.visibleProperty().bind(serverOrClientButton.selectedProperty().not());
	userNameTextField.visibleProperty().bind(serverOrClientButton.selectedProperty().not());
    }
}

