package textchat;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.InetAddress;

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
	private InetAddress ipOfSource;
	private T data;
	private Type dataType;

	TextChatData(){
		nameOfUser = "";
		socketToDestination = null;
		ipOfSource = null;
		data = null;
		dataType = Type.NONE;
	}

	TextChatData(Socket destination, InetAddress source, T newData, Type type, String nameOfUser){ //Typically used by clients sending messages to other clients
		this();
		socketToDestination = destination;
		ipOfSource = source;
		data = newData;
		dataType = type;
		this.nameOfUser = nameOfUser;
	}

	TextChatData(Socket destination, InetAddress source, T newData, Type type, ObjectOutputStream out){ //Typically used by clients sending messages to other clients
		this();
		socketToDestination = destination;
		ipOfSource = source;
		data = newData;
		dataType = type;
		this.nameOfUser = nameOfUser;
		this.out = out;
	}

	TextChatData(Socket destination, InetAddress source, boolean isNone){
		this();
		socketToDestination = destination;
		ipOfSource = source;
		if(isNone){
			dataType = Type.NONE;
		}
		else{
			dataType = Type.PORT_TEST;
		}
	}

	public void send(){
		try{
			out.writeObject(data);
			out.flush();
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}

	public void setData(T newData){
		data = newData;	
	}

	public void setSource(InetAddress newSource){
		ipOfSource = newSource;
	}

	public void setDestination(Socket newDestination){
		socketToDestination = newDestination;
	}

	public InetAddress getSource(){
		return ipOfSource;
	}

	public Socket getDestination(){
		return socketToDestination;
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
