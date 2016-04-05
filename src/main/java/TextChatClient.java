package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javafx.collections.ObservableList;
import java.util.ArrayList;

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
			ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
			Scanner inputScanner = new Scanner(System.in)){
				System.out.println("Sending output to server...");
				out.println("Luke");
				while(true){
					String input = inputScanner.nextLine();
					out.println(input);
					ArrayList<String> list = (ArrayList<String>)in.readObject();
					System.out.println(list);
				}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
