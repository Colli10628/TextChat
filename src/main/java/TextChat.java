package textchat;


public class TextChat{
	public static void main(String[] args){
		if(args.length == 1){
			System.out.println("Initializing server");
			TextChatServer server = new TextChatServer(Integer.valueOf(args[0]));			
		}
		else if(args.length == 2){
			System.out.println("Initializing client");
			TextChatClient server = new TextChatClient(Integer.valueOf(args[0]), args[1]);			
		}
		else{
			System.out.println("Invalid number of arguments. Please launch the application as follows:");
			System.out.println("java textchat.TextChat portnumber"); 
			System.out.println("OR");
			System.out.println("java textchat.TextChat portnumber ipaddress");
			System.out.println("The default port number is 15023");
		}
	}
}
