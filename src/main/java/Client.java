package textchat;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
public class Client{
	StringProperty ip;
	StringProperty username;
	
	Client(){
		ip = new SimpleStringProperty("");
		username = new SimpleStringProperty("");
	}	
	Client(String ip, String username){
		this();
		this.ip.set(ip);
		this.username.set(username);
	}
	public StringProperty usernameProperty(){
		return username;
	}
	public StringProperty ipProperty(){
		return ip;
	}
	@Override
	public boolean equals(Object other){
		Client obj = (Client)other;
		return obj.ip.equals(ip) && obj.username.equals(username);
	}
}
