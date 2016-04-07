package textchat;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.security.SecureRandom;
public class Client{
	StringProperty ip;
	StringProperty username;
	String clientInternalIP;
	String clientExternalIP;
	int num;	
	Client(){
		try{
			ip = new SimpleStringProperty("");
			username = new SimpleStringProperty("");
			SecureRandom random = new SecureRandom();
			num = random.nextInt(10);
			clientInternalIP = NetworkUtilities.getInternalIp();
			clientExternalIP = NetworkUtilities.getExternalIp();
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}	
	Client(String ip, String username){
		this();
		this.ip.set(ip);
		this.username.set(username);
	}
	Client(String ip, String username, String internalIP, String externalIP){
		this();
		this.ip.set(ip);
		this.username.set(username);
		clientInternalIP = internalIP;
		clientExternalIP = externalIP;
	}
	public String getInternalIP(){
		return clientInternalIP;
	}
	public String getExternalIP(){
		return clientExternalIP;
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
		return username.get().equals(obj.username.get());
	}
	public boolean fullEquals(Object other){
		Client obj = (Client)other;
		return username.get().equals(obj.username.get()) || (clientExternalIP.equals(obj.clientExternalIP) && clientInternalIP.equals(obj.clientInternalIP));
	}
	public int getNum(){
		return num;
	}
}
