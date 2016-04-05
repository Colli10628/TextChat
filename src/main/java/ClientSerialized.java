package textchat;

import java.io.Serializable;

public class ClientSerialized implements Serializable{
	String username;
	String ip;
	String clientInternalIP;
	String clientExternalIP;
	int num = 0;

	ClientSerialized(Client obj){
		username = obj.usernameProperty().get();
		ip = obj.ipProperty().get();
		num = obj.getNum();
		clientInternalIP = obj.getInternalIP();
		clientExternalIP = obj.getExternalIP();
	}

	public int getNum(){
		return num;
	}

	public String getIp(){
		return ip;
	}

	public String getUsername(){
		return username;
	}
	Client getOriginal(){
		return new Client(ip, username, clientInternalIP, clientExternalIP);
	}
	@Override 
	public String toString(){
		return username + " " + ip;
	}
}
