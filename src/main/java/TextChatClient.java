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
import javafx.collections.FXCollections;
import javafx.application.Platform;

public class TextChatClient{
	private int port;
	private String hostname;
	private String username;
	private boolean quit;
	private ObservableList<ClientSerialized> otherClientList = FXCollections.observableArrayList();
	
	TextChatClient(int port, String hostname, String username){
		this.port = port;
		this.hostname = hostname;
		this.username = username;
		quit = false;
		initClient();
	}
	private void setQuit(){
		quit = true;
	}
	public ObservableList<ClientSerialized> getObservableOtherClientsList(){
		return otherClientList;
	}
	private void initClient(){
		boolean quit = false;
		try{
			Runnable getNewClientList = () -> {
				try{
					Socket serverSocket = new Socket(hostname, port);
					PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
					ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
					Scanner inputScanner = new Scanner(System.in);
					System.out.println("Sending output to server...");
					out.println(username);

					while(true){
							ArrayList<ClientSerialized> list = (ArrayList<ClientSerialized>) in.readObject();	
							System.out.println(list);
							otherClientList.clear();
							otherClientList.addAll(list);	
					}
				}
				catch(Exception exc){
					exc.printStackTrace();
					Platform.exit();
					System.exit(0);
				}
			};
			
			Thread thread = new Thread(getNewClientList);
			thread.setDaemon(true);
			thread.start();
			/*
			while(true && !quit){
				String input = inputScanner.nextLine();
				out.println(input);
			}
			*/
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
