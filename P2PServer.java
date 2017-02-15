import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class P2PServer {
	
	DatagramSocket serverSocket;
	Vector<Thread> clientThreads = new Vector<>();
	Vector<P2PClient> clients = new Vector<>();
	P2PLocalClient localClient;
	
	Map<String, P2PClient> unikeyToClient = new HashMap<>();
	
	// constructor to create the DatagramSocket which will receive the packets
	public P2PServer(DatagramSocket socket) {
		try {
			serverSocket = socket;
		} catch (Exception e) {
			System.err.println("Error creating server socket: " + e);
		}
	}
	
	public synchronized void printSavedMessages() {
		for (P2PClient client : clients) {
			
			long timeSinceLastUpdate = (System.currentTimeMillis() - client.timeOfLastUpdate) / 1000;
			
			if (timeSinceLastUpdate >= 20) {
				// Don't print anything
			} else if (timeSinceLastUpdate >= 10 && timeSinceLastUpdate < 20) {
				// idle
				System.out.println("# [" + client.pseudo + " (" + client.unikey + "): idle]");
			} else if (client.latestStatus == null){
				System.out.println("# [" + client.pseudo + " (" + client.unikey + "): not yet initialized]");
			} else {
				System.out.println("# " + client.pseudo + " (" + client.unikey + "): " + client.latestStatus);
			}
		}
	}
}
