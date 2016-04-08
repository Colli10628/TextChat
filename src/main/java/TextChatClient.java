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
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.application.Platform;

public class TextChatClient{
	private int port;
	private String hostname;
	private String username;
	private boolean quit;
	private Socket serverSocket;
	private ObjectOutputStream out;
	private ObservableList<ClientSerialized> otherClientList = FXCollections.observableArrayList();
	private HashMap<String, TextChatConversationController> mapOfConversationWindows = new HashMap<>();
	public TextChatClientController controller;
	
	
	TextChatClient(int port, String hostname, String username){
		this.port = port;
		this.hostname = hostname;
		this.username = username;
		quit = false;
		controller = null;
		initClient();
	}
	TextChatClient(int port, String hostname, String username, TextChatClientController ctroller){
		this(port, hostname,username);
		controller = ctroller;
	}
	public void appendWindowToMap(String username, TextChatConversationController window){
		mapOfConversationWindows.put(username, window);
	}
	private void setQuit(){
		quit = true;
	}
	public ObservableList<ClientSerialized> getObservableOtherClientsList(){
		return otherClientList;
	}
	public void sendMessage(String username, String message){
		TextChatData sender = new TextChatData(serverSocket, this.username, message, TextChatData.Type.MESSAGE, username, out);
		sender.send();
	}
	private void initClient(){
		boolean quit = false;
		try{
			serverSocket = new Socket(hostname, port);
			out = new ObjectOutputStream(serverSocket.getOutputStream());
			Runnable getNewClientList = () -> {
				try{
					ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());
					Scanner inputScanner = new Scanner(System.in);
					System.out.println("Sending output to server...");
					Client newClient = new Client(hostname, username, NetworkUtilities.getInternalIp(), NetworkUtilities.getExternalIp());
					out.writeObject(new TextChatData(serverSocket, "username", true));
					out.writeObject(new ClientSerialized(newClient));

					while(true){
						System.out.println("Listening for data");
						TextChatData serverOutput = (TextChatData)in.readObject();
						if(serverOutput.isMeta()){
							ArrayList<ClientSerialized> list = (ArrayList<ClientSerialized>)serverOutput.getData();
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
						if(serverOutput.isMessage()){
							Platform.runLater(new Runnable(){
								@Override public void run(){
									System.out.println("Source :" + serverOutput.getSource());
									TextChatConversationController window = mapOfConversationWindows.get(serverOutput.getSource());
									if(window == null){
										TextChatConversationController convController = controller.openChatWindow(serverOutput.getSource());
										mapOfConversationWindows.put(serverOutput.getSource(), convController);	
										window = mapOfConversationWindows.get(serverOutput.getSource());
									}
									else if(window.isClosed){
										mapOfConversationWindows.remove(serverOutput.getSource());
										TextChatConversationController convController = controller.openChatWindow(serverOutput.getSource());
										mapOfConversationWindows.put(serverOutput.getSource(), convController);	
										window = mapOfConversationWindows.get(serverOutput.getSource());

									}
									window.addToConvoList((String)serverOutput.getData());
									System.out.println("Message " + (String)serverOutput.getData());
								}
							});
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
