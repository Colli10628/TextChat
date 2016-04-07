package textchat;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TextChatData<T> implements Serializable{
	public enum Type{
		ERROR,
		MESSAGE,
		PORT_TEST,
		META,
		NONE
	};
	
	transient private Socket socketToDestination;
	transient private ObjectOutputStream out;
	private String nameOfUser;
	private String nameOfSender;
	private T data;
	private Type dataType;

	TextChatData(){
		nameOfUser = "";
		socketToDestination = null;
		nameOfSender = null;
		data = null;
		dataType = Type.NONE;
	}

	TextChatData(Socket destination, String sender , T newData, Type type, String nameOfDestinationUser, ObjectOutputStream outputStreamToServer){ //Typically used by clients sending messages to other clients
		this();
		socketToDestination = destination;
		nameOfSender = sender;
		data = newData;
		dataType = type;
		this.nameOfUser = nameOfDestinationUser;
		out = outputStreamToServer;
	}

	TextChatData(Socket destination, String source, T newData, Type type, ObjectOutputStream out){ 
		this();
		socketToDestination = destination;
		nameOfSender = source;
		data = newData;
		dataType = type;
		this.out = out;
	}

	TextChatData(Socket destination, String source, boolean isNone){
		this();
		socketToDestination = destination;
		nameOfSender = source;
		if(isNone){
			dataType = Type.NONE;
		}
		else{
			dataType = Type.PORT_TEST;
		}
	}

	public void send(){
		try{
			out.writeObject(this);
			out.flush();
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}

	public void setData(T newData){
		data = newData;	
	}

	public void setSource(String newSource){
		nameOfSender = newSource;
	}

	public void setDestination(Socket newDestination){
		socketToDestination = newDestination;
	}

	public String getSource(){
		return nameOfSender;
	}

	public Socket getDestination(){
		return socketToDestination;
	}

	public String getDestinationUsername(){
		return nameOfUser;
	}
	public T getData(){
		return data;
	}

	public boolean isError(){
		return dataType == Type.ERROR;
	}

	public boolean isMessage(){
		return dataType == Type.MESSAGE;
	}

	public boolean isPortTest(){
		return dataType == Type.PORT_TEST;
	}

	public boolean isNone(){
		return dataType == Type.NONE;
	}

	public boolean isMeta(){
		return dataType == Type.META;
	}
}
