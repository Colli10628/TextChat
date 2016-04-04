package textchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TextChatServerController {

	@FXML
	private StringProperty portTextProperty;

	@FXML
	private StringProperty ipTextProperty;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox portTextField;

    @FXML
    private Text portText;

    @FXML
    private Text ipText;

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, String> usernameColumn;

    @FXML
    private TableColumn<Client, String> ipColumn;


	TextChatServer server;
	public void initPortAndIP(String port, String ip){
		portTextProperty.set(port);
		ipTextProperty.set(ip);
		server = new TextChatServer(Integer.valueOf(port));
		clientsTable.setItems(server.getObservableClientList());
		usernameColumn.setCellValueFactory(new PropertyValueFactory("username"));
		ipColumn.setCellValueFactory(new PropertyValueFactory("ip"));
		Runnable task = () ->{
			server.start();
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
    @FXML
    void initialize() {
        assert portTextField != null : "fx:id=\"portTextField\" was not injected: check your FXML file 'Untitled'.";
        assert portText != null : "fx:id=\"portText\" was not injected: check your FXML file 'Untitled'.";
        assert ipText != null : "fx:id=\"ipText\" was not injected: check your FXML file 'Untitled'.";
		portTextProperty = new SimpleStringProperty();
		ipTextProperty = new SimpleStringProperty();
		portText.textProperty().bind(portTextProperty);
		ipText.textProperty().bind(ipTextProperty);
    }
}

