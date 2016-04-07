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
	ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).build());
	
	
	ObservableList getObservableClientList(){
		return clientList;
	}
	TextChatServer(int port){
		this.port = port;	
	}

	void sendNewClientListToClients(ServerSocket serverSocket, ObjectOutputStream out){
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
				Socket tempSock = clientSockets.get(current.getOriginal().getIp()+current.getNum());
				TextChatData<ArrayList<ClientSerialized>> data = new TextChatData<ArrayList<ClientSerialized>>(tempSock, serverSocket.getInetAddress(), toSend, TextChatData.Type.META,  out);
				data.send();
				System.out.println("Trying to send " + toSend + " to " + current.getOriginal().getIp());
			}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	void start(){
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			ReentrantLock lock = new ReentrantLock();
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
							TextChatData clientPortProbeResponse = new TextChatData(clientSocket, clientSocket.getInetAddress(), false);
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
						clientSockets.put(newClient.getIp() + newClient.getNum(), clientSocket);

						
						lock.unlock();
						System.out.println("Client connected on port" + port);
						Runnable listenForData = () -> {

							String inputLine = "";
							try{
								sendNewClientListToClients(serverSocket, out);
								while((inputLine = (String)in.readObject()) != null){
									System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
								}							
							}
							catch(Exception exc){
								clientList.remove(newClient);
								sendNewClientListToClients(serverSocket, out);
								try{
									clientSocket.close();
								}
								catch(IOException exc1){
									System.out.println(exc1.getMessage());
								}							
							}		

						};
						Thread tempThread = new Thread(listenForData);
						tempThread.setDaemon(true);
						tempThread.start();

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
