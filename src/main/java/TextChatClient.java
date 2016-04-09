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
import javafx.stage.Window;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.media.AudioClip;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


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
	AudioClip clip;
	
	TextChatClient(int port, String hostname, String username){
		clip = new AudioClip(this.getClass().getClassLoader().getResource("si.wav").toString());
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

	public boolean isWindowInMap(String username){
		return mapOfConversationWindows.get(username) != null;
	}

	public TextChatConversationController getWindowController(String username){
		return mapOfConversationWindows.get(username);
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
									String source = serverOutput.getSource();
									TextChatConversationController windowController = mapOfConversationWindows.get(serverOutput.getSource());
									if(windowController == null){
										TextChatConversationController convController = controller.openChatWindow(source);
										mapOfConversationWindows.put(source, convController);	
										windowController = mapOfConversationWindows.get(source);
										windowController.setTitle("Conversation with " + source);
									}
									else if(windowController.isClosed){
										mapOfConversationWindows.remove(source);
										TextChatConversationController convController = controller.openChatWindow(source);
										mapOfConversationWindows.put(source, convController);	
										windowController = mapOfConversationWindows.get(source);

									}
									
									Stage window = windowController.getWindow();
									if(!window.isFocused()){
										
										clip.play(1.0);
										Notifications notif = Notifications.create().title("New Message").text(source + " sent you a new message!");
										notif.onAction(new EventHandler<ActionEvent>(){
											public void handle(ActionEvent e){
												window.requestFocus();
											}
										});
										notif.showInformation();
									}
									
									windowController.addToConvoList((String)serverOutput.getData());
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
