import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class P2PTwitter {

	public static void main(String[] args) {

		// first start by parsing the args to get unikey
		String unikey =  "test1111"; // args[0]; // "test1111";
		// String pseudo = "Scott";
		
		// port number and IP address will come from the property file
		int port = 7014;
		// String ipAddress = "10.19.61.96";
		
		DatagramSocket socketToClose = null;
		
		
		Properties props = new Properties();
		
		try {
			FileInputStream inStream = new FileInputStream("participants.properties");
			props.load(inStream);
			inStream.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		String participants[] = props.getProperty("participants").split(",");		
		
		try {			
			
			// need to get port number of the local peer first to create socket
			
			for (String peer : participants) {
				if (props.getProperty(peer + ".unikey").equals(unikey)) {
					port = Integer.parseInt(props.getProperty(peer + ".port"));
				}
 			}
			
			DatagramSocket socket = new DatagramSocket(port);
			socketToClose = socket;
			// establish a server to handle incoming requests
			P2PServer server = new P2PServer(socket);
			
			P2PLocalClient localClient = null;
			
			for (String peer : participants) {
				String ip = props.getProperty(peer + ".ip");
				String parsedPseudo = props.getProperty(peer + ".pseudo");
				String parsedUnikey = props.getProperty(peer + ".unikey");
				int parsedPort = Integer.parseInt(props.getProperty(peer + ".port"));
				
				InetAddress inetAdd = null;
				
				try { inetAdd = InetAddress.getByName(ip); } catch (Exception e) { System.err.println(e); }	
				
				if (unikey.equals(parsedUnikey)) {
					localClient = new P2PLocalClient(inetAdd, parsedPseudo, parsedUnikey, parsedPort, server);
					Thread localClientThread = new Thread(localClient);
					localClientThread.start();
					server.clientThreads.add(localClientThread);
					server.localClient = localClient;
				} else {
					P2PClient client = new P2PClient(inetAdd, parsedPseudo, parsedUnikey, parsedPort, server);
					Thread clientThread = new Thread(new P2PConnectionThread(server));
					//Thread clientThread = new Thread(client);
					clientThread.start();
					server.clientThreads.add(clientThread);
					server.clients.add(client);
					server.unikeyToClient.put(parsedUnikey, client);
				}	
			}
			
			P2PMainHelper helper = new P2PMainHelper(localClient, unikey, server);
			Thread helperThread = new Thread(helper);
			helperThread.start();
			
//			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			// if (socketToClose != null)
				// try {socketToClose.close();} catch (Exception e) { System.out.println("Close failed");} 
		}
	}

}
