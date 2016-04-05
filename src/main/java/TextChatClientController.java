package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class TextChatClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<ClientSerialized> clientList;

	private TextChatClient client;
	
	public void initClient(String port, String ip, String username){
		client = new TextChatClient(Integer.valueOf(port), ip, username);
		clientList.setItems(client.getObservableOtherClientsList());
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
							if(!client.equals(temp)){
								setText(item.getUsername());
							}
							else{
								setText("");
							}
						}
                        else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

    }
}

