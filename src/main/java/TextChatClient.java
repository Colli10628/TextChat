package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
					ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
					Scanner inputScanner = new Scanner(System.in);
					System.out.println("Sending output to server...");
					Client newClient = new Client(hostname, username, NetworkUtilities.getInternalIp(), NetworkUtilities.getExternalIp());
					out.writeObject(new TextChatData(serverSocket, serverSocket.getInetAddress(), true));
					out.writeObject(new ClientSerialized(newClient));

					while(true){
						System.out.println("Listening for data");
						ArrayList<ClientSerialized> list = (ArrayList<ClientSerialized>) in.readObject();
						in.skip(in.available());
						System.out.println(list);
						System.out.println("Clearing list");
						otherClientList.clear();
						Client temp = new Client(hostname, username);
						ArrayList<ClientSerialized> toRemove = new ArrayList<>();
						for(ClientSerialized curr : list){
							System.out.println(curr);
							if(!temp.equals(curr.getOriginal())){
								otherClientList.add(curr);
							}
						}
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
