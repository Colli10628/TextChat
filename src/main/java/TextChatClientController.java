package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class TextChatClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<ClientSerialized> clientList;

	private TextChatClient tClient;
	private Client associatedClient;
	public void initClient(String port, String ip, String username){
		associatedClient = new Client(ip, username);
		tClient = new TextChatClient(Integer.valueOf(port), ip, username);
		clientList.setItems(tClient.getObservableOtherClientsList());
	}

    @FXML
    void openChatWindow(MouseEvent event) {
	try{
		ClientSerialized selected = clientList.getSelectionModel().getSelectedItem();
		if(selected != null){
			URL url = getClass().getClassLoader().getResource("TextChatConversationController.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(url);     

			Parent root = (Parent)fxmlLoader.load();          
			TextChatConversationController controller = (TextChatConversationController)fxmlLoader.getController();
			if(controller == null){
				System.out.println("Controller is null!");
			}
		//	controller.initPortAndIP(portTextField.getText(), NetworkUtilities.getExternalIp() + " / " + NetworkUtilities.getInternalIp());

			controller.initSlider();
			Scene scene = new Scene(root); 
			Stage stage = new Stage();
			stage.setTitle("TextChat Conversation");
			stage.setScene(scene);    

			stage.show();   
			clientList.getSelectionModel().clearSelection();

		}
	}
	catch(IOException exc){
		exc.printStackTrace();
	}
    }


    @FXML
    void initialize() {
        assert clientList != null : "fx:id=\"clientList\" was not injected: check your FXML file 'TextChatClientController.fxml'.";
		clientList.setCellFactory(new Callback<ListView<ClientSerialized>, ListCell<ClientSerialized>>() {
            @Override
            public ListCell<ClientSerialized> call(ListView<ClientSerialized> param) {
                ListCell<ClientSerialized> cell = new ListCell<ClientSerialized>() {
                    @Override
                    protected void updateItem(ClientSerialized item, boolean empty) {
                        super.updateItem(item, empty);
			if(item != null){
				Client temp = item.getOriginal();
				if(!associatedClient.equals(temp)){
					setText(item.getUsername());
				}
				else{
					return;
				}
			}
                        else {
                            return;
                        }
                    }
                };
                return cell;
            }
        });

    }
}

