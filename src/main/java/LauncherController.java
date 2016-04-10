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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
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

	private void showErrorMessage(String error){
		Alert box = new Alert(AlertType.ERROR, error);
		box.showAndWait();
	}

     @FXML
    void launchFromEnter(KeyEvent event) {
    	if(event.getCode() == KeyCode.ENTER){
    		launch();
    	}
    }

    @FXML
    void launch() {
		try{
			if(serverOrClientButton.isSelected()){
				String enteredPort = portTextField.getText();
				if(enteredPort.equals("") || !enteredPort.matches(".*\\d+.*")){
					showErrorMessage("Please enter a numeric port");
				}
				else if(NetworkUtilities.isPortAvailable("localhost", Integer.valueOf(enteredPort)) || 
					!NetworkUtilities.hasPermissionToBindPort(Integer.valueOf(enteredPort))){
					showErrorMessage("Port is unavailable");
				}
				else{
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
					((Node)ipText).getScene().getWindow().hide();
				}
			}
			else{
				String username = userNameTextField.getText();
				String ip = ipTextField.getText();
				String port = portTextField.getText();
				if(username.equals("") || ip.equals("") || port.equals("")){
					showErrorMessage("All fields must not be empty");
				}
				else if(!port.matches(".*\\d+.*")){
					showErrorMessage("Port must be numeric");
				}
				else if(!NetworkUtilities.isPortAvailable(ip, Integer.valueOf(port))){
					showErrorMessage("Can't connect to the specified port and ip address");
				}
				else{
					URL url = getClass().getClassLoader().getResource("TextChatClientController.fxml");
					FXMLLoader fxmlLoader = new FXMLLoader(url);     

					Parent root = (Parent)fxmlLoader.load();          
					TextChatClientController controller = (TextChatClientController)fxmlLoader.getController();
					if(controller == null){
						System.out.println("Controller is null!");
					}
					controller.initClient(port, ip, username);

					Scene scene = new Scene(root); 
					Stage stage = new Stage();
					stage.setTitle("TextChat Client");
					stage.setScene(scene);    

					stage.show();   

					((Node)ipText).getScene().getWindow().hide();
				}
				
			}

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

