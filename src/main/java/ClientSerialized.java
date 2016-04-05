package textchat;

import java.io.Serializable;

public class ClientSerialized implements Serializable{
	public String username;
	public String ip;

	ClientSerialized(Client obj){
		username = obj.usernameProperty().get();
		ip = obj.ipProperty().get();
	}

	Client getOriginal(){
		return new Client(ip, username);
	}
	@Override 
	public String toString(){
		return username + " " + ip;
	}
}
