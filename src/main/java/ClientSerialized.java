package textchat;

import java.io.Serializable;

public class ClientSerialized implements Serializable{
	public String username;
	public String ip;
	int num = 0;

	ClientSerialized(Client obj){
		username = obj.usernameProperty().get();
		ip = obj.ipProperty().get();
		num = obj.getNum();
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
		return new Client(ip, username);
	}
	@Override 
	public String toString(){
		return username + " " + ip;
	}
}
