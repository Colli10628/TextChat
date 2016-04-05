package textchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.InetAddress;
import java.net.Socket;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.ServerSocket;
import java.util.Enumeration;

public class NetworkUtilities {

    public static String getExternalIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	public static String getInternalIp() throws SocketException
	{
		Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
		while( ifaces.hasMoreElements() )
		{
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while( addresses.hasMoreElements() )
			{
				InetAddress addr = addresses.nextElement();
				if( addr instanceof Inet4Address && !addr.isLoopbackAddress() )
				{
					return addr.getHostAddress();
				}
			}
		}

		return null;
	}


	public static String loopBackAddress(){
		try{
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(java.net.UnknownHostException exc){
			return "unknown";
		}
	}
	public static boolean hasPermissionToBindPort(int port){
		ServerSocket s = null;
		try
		{
			s = new ServerSocket(port);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			if(s != null)
				try {s.close();}
				catch(Exception e){}
		}

	}
	public static boolean isPortAvailable(String host, int port)
	{
		Socket s = null;
		try
		{
			s = new Socket(host, port);
			s.close();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
