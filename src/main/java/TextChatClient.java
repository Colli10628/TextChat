package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TextChatClient{
	private int port;
	private String hostname;
	
	TextChatClient(int port, String hostname){
		this.port = port;
		this.hostname = hostname;
		initClient();
	}
	private void initClient(){
		try(Socket serverSocket = new Socket(hostname, port);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			Scanner inputScanner = new Scanner(System.in)){
				System.out.println("Sending output to server...");
				while(true){
					String input = inputScanner.nextLine();
					out.println(input);
				}
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
	}
}
