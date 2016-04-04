package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class TextChatServer{
	private int port;
	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).build());
	
	
	ObservableList getObservableClientList(){
		return clientList;
	}
	TextChatServer(int port){
		this.port = port;	
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
						PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
						System.out.println("Client connected on port" + port);
						String inputLine = "";
						String name = in.readLine();
						String ip = clientSocket.getInetAddress().getHostAddress();
						Client newClient = new Client(ip, name);
						if(clientList.contains(newClient)){
							return;
						}
						lock.lock();
						clientList.add(newClient);
						lock.unlock();
						System.out.println(ip + " " + name);
						while((inputLine = in.readLine()) != null){
							System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
						}
						clientList.remove(newClient);
						clientSocket.close();
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
