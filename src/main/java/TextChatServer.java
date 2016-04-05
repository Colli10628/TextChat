package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectOutputStream;
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
					System.out.println(exc.getMessage());
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
						lock.lock();
						System.out.println("Trying to listen on " + port);
						Socket clientSocket = serverSocket.accept();
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
						System.out.println("Client connected on port" + port);
						String name = "";
						String inputLine = "";

						name = in.readLine();

						String ip = clientSocket.getInetAddress().getHostAddress();
						Client newClient = new Client(ip, name);
						if(clientList.contains(newClient)){
							return;
						}

						clientList.add(newClient);
						clientOutputStreams.put(ip+newClient.getNum(), out);

						sendNewClientListToClients();
						lock.unlock();
						System.out.println(ip + " " + name);
						while((inputLine = in.readLine()) != null){
							System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
						}
						clientList.remove(newClient);
						sendNewClientListToClients();
						clientSocket.close();
					}	
					catch(SocketException exc){
						System.out.println(exc.getMessage());
						exc.printStackTrace();
					}
					catch(IOException exc){
						System.out.println("Exception when trying to listen on port");
						exc.printStackTrace();
					}
				};
				executor.submit(listenOnPort);
				Thread.sleep(1000);
			}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
