package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TextChatServer{
	private int port;
	
	TextChatServer(int port){
		this.port = port;	
		initServer();
	}
	void initServer(){
		System.out.println("Trying to listen on port " + port);
		try(ServerSocket serverSocket = new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
			clientSocket.getInputStream()));){
			System.out.println("Client connected on port" + port);
			String inputLine = "";
			while((inputLine = in.readLine()) != null){
				System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
			}
		}	
		catch(IOException exc){
			System.out.println("Exception when trying to listen on port");
			exc.printStackTrace();
		}
	}
}
