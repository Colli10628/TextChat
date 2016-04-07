package textchat;

import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.Serializable;

public class TransmittableSocket implements Serializable{
	private InetAddress address;
	private int port;
	private int localPort;

	TransmittableSocket(Socket socket){
		address = socket.getInetAddress();
		port = socket.getPort();
		localPort = socket.getLocalPort();
	}
	public Socket getSocket(){
		try{
			return new Socket(address, port, null, localPort);
		}
		catch(IOException exc){
			exc.printStackTrace();
			return null;
		}
	}
}
