package textchat;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.security.SecureRandom;
public class Client{
	StringProperty ip;
	StringProperty username;
	int num;	
	Client(){
		ip = new SimpleStringProperty("");
		username = new SimpleStringProperty("");
		SecureRandom random = new SecureRandom();
		num = random.nextInt(10);
	}	
	Client(String ip, String username){
		this();
		this.ip.set(ip);
		this.username.set(username);
	}
	public String getIp(){
		return ip.get();
	}
	public String getUsername(){
		return username.get();
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
		return obj.ip.equals(ip) || obj.username.equals(username);
	}
	public int getNum(){
		return num;
	}
}
