package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.SocketException;

public class TextChatServer{
	private int port;
	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	private HashMap<String, Socket> clientSockets = new HashMap<>();
	private HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<>();
	private HashMap<String, ObjectOutputStream> userOutputStreamMap = new HashMap<>();
	ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).build());
	ReentrantLock lock = new ReentrantLock();
	
	
	ObservableList getObservableClientList(){
		return clientList;
	}
	TextChatServer(int port){
		this.port = port;	
	}

	void forwardMessage(String destinationUsername, String senderUsername, String message){
		ObjectOutputStream out = clientOutputStreams.get(destinationUsername);
		Socket socket = clientSockets.get(destinationUsername);
		TextChatData toUser = new TextChatData(socket, senderUsername, senderUsername + ": " + message, TextChatData.Type.MESSAGE, out);
		toUser.send();
	}
	void sendNewClientListToClients(ServerSocket serverSocket){
		try{
			ArrayList<ClientSerialized> toSend = new ArrayList<>();
			for(int counter = 0; counter < clientList.size(); counter++){
				ClientSerialized temp = new ClientSerialized(clientList.get(counter));
				if(!temp.getUsername().equals("") && !temp.equals("")){
					toSend.add(temp);
					System.out.println(temp);
				}
			}
			for(ClientSerialized current : toSend){
				Socket tempSock = clientSockets.get(current.getUsername());
				ObjectOutputStream out = clientOutputStreams.get(current.getUsername());
				TextChatData<ArrayList<ClientSerialized>> data = new TextChatData<ArrayList<ClientSerialized>>(tempSock, "Server", toSend, TextChatData.Type.META, out);
				data.send();
				System.out.println("Trying to send " + toSend + " to " + current.getOriginal().getIp() + " " + current.getOriginal().getUsername());
			}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	void start(){
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			while(true){
				Runnable listenOnPort = () -> {
					try{						
						System.out.println("Trying to listen on " + port);
						Socket clientSocket = serverSocket.accept();
						System.out.println(clientSocket);
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						System.out.println("Client connecting on port" + port);


						TextChatData initialClientInfo = (TextChatData)in.readObject();
						if(initialClientInfo.isPortTest()){
							TextChatData clientPortProbeResponse = new TextChatData(clientSocket, "Server", false);
							out.writeObject(initialClientInfo);
							return;
						}

						ClientSerialized temp = (ClientSerialized)in.readObject();
						Client newClient = temp.getOriginal();
						if(clientList.contains(newClient)){
							System.out.println("Duplicate rejected");
							return;
						}
						System.out.println("Waiting for lock");
						lock.lock();
						System.out.println("Acquired lock");
						clientList.add(newClient);
						clientSockets.put(temp.getUsername(), clientSocket);
						clientOutputStreams.put(temp.getUsername(), out);
						userOutputStreamMap.put(temp.getUsername(), out);

						
						lock.unlock();
						System.out.println("Client connected on port" + port);

						try{
							lock.lock();
							sendNewClientListToClients(serverSocket);
							lock.unlock();
							TextChatData clientInput;
							while((clientInput = (TextChatData)in.readObject()) != null){
								if(clientInput.isMessage()){
									System.out.println(clientInput.getData());
									forwardMessage(clientInput.getDestinationUsername(), clientInput.getSource(), (String)clientInput.getData());
								}
							}
						}
						catch(Exception exc){
							exc.printStackTrace();
							try{
								lock.lock();
								clientList.remove(newClient);
								sendNewClientListToClients(serverSocket);
								clientSocket.close();
							}
							catch(IOException exc1){
								System.out.println(exc1.getMessage());
							}							
							finally{
								lock.unlock();
							}
						}		
						/*
						Thread tempThread = new Thread(listenForData);
						tempThread.setDaemon(true);
						tempThread.start();
						*/

					}	
					catch(SocketException exc){
						System.out.println(exc.getMessage());
					}
					catch(IOException | ClassNotFoundException exc){
						System.out.println("Exception when trying to listen on port");
						exc.printStackTrace();
					}
					finally{
						lock.unlock();
					}
				};
				executor.submit(listenOnPort);
				Thread.sleep(1);
			}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
