package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;

public class TextChatConversationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private SplitPane godSplitPane;

    @FXML
    void sendMessage(MouseEvent event) {

    }

	public void initSlider(){
			/*
			godSplitPane.getDividers().get(0).positionProperty()
					.bind(godSplitPane.heightProperty().divide(4));
			*/

	}
    @FXML
    void initialize() {
        assert godSplitPane != null : "fx:id=\"godSplitPane\" was not injected: check your FXML file 'TextChatConversationController.fxml'.";

    }
}

