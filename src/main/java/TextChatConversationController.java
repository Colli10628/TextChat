package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class TextChatConversationController {

	private String localUsername;
	private String remoteUsername;
	private TextChatClient tClient;
	public boolean isClosed;
    @FXML
    private ResourceBundle resources;

	@FXML
	private TextArea messageBox;

    @FXML
    private URL location;

	@FXML
	private ListView<String> messageList;

    @FXML
    private Button sendButton;

	public void addToConvoList(String newMessage){
		messageList.getItems().add(newMessage);	
	}

    @FXML
    void sendMessage() {
			tClient.sendMessage(remoteUsername, messageBox.getText());
			messageList.getItems().add(messageBox.getText());
			messageBox.setText("");
    }
	public void initConversationWindow(String localUser, String remoteUser, TextChatClient client){
		localUsername = localUser;
		remoteUsername = remoteUser;
		tClient = client;
	}

	public Stage getWindow(){
		return (Stage)((Node)messageList).getScene().getWindow();
	}

 	public void setTitle(String title){
 		Stage stage = (Stage)((Node)messageList).getScene().getWindow();
 		stage.setTitle(title);
 	}

     @FXML
    void sendMessageFromEnter(KeyEvent event) {
    	if(event.getCode() == KeyCode.ENTER){
    		sendMessage();
    	}
    }


    @FXML
    void initialize() {
    	isClosed = false;
        assert messageList != null : "fx:id=\"messageList\" was not injected: check your FXML file 'TextChatConversationController.fxml'.";
        assert messageBox != null : "fx:id=\"messageBox\" was not injected: check your FXML file 'TextChatConversationController.fxml'.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'TextChatConversationController.fxml'.";

    }

}

