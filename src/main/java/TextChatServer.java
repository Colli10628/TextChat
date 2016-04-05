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
	private HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<>();
	ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).build());
	
	
	ObservableList getObservableClientList(){
		return clientList;
	}
	TextChatServer(int port){
		this.port = port;	
	}

	void sendNewClientListToClients(){
		try{
			ArrayList<ClientSerialized> toSend = new ArrayList<>();
			for(int counter = 0; counter < clientList.size(); counter++){
				toSend.add(new ClientSerialized(clientList.get(counter)));
			}
			for(ClientSerialized current : toSend){
				try{
					ObjectOutputStream out = clientOutputStreams.get(current.getOriginal().getIp()+current.getNum());
					out.writeObject(toSend);
					System.out.println("Trying to send " + toSend + " to " + current.getOriginal().getIp());
				}
				catch(IOException exc){
					exc.printStackTrace();
				}
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
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						System.out.println("Client connecting on port" + port);


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
						clientOutputStreams.put(newClient.getIp() + newClient.getNum(), out);

						
						lock.unlock();
						System.out.println("Client connected on port" + port);
						Runnable listenForData = () -> {

							String inputLine = "";
							try{
								sendNewClientListToClients();
								while((inputLine = (String)in.readObject()) != null){
									System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
								}							
							}
							catch(Exception exc){
								clientList.remove(newClient);
								sendNewClientListToClients();
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
