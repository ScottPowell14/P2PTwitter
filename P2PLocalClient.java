import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class P2PLocalClient implements Runnable {
	// this local client handles outgoing messages and sending requests
	
	// Local Client Info
	InetAddress ip;
	String pseudo;
	String unikey;
	int port;
		
	// Local Server Reference
	P2PServer localServer;
	
	// Status-related variables
	Vector<String> statuses = new Vector<>();
	String latestStatus;
	
	public P2PLocalClient(InetAddress ip, String pseudo, String unikey, int port, P2PServer server) {
		this.ip = ip;
		this.pseudo = pseudo;
		this.unikey = unikey;
		this.port = port;
		this.localServer = server;
		this.latestStatus = null;
	}
	
	// need a "send" method which sends messages outward using the local client socket -- may need to be synchronized as well
	public void sendPacket(String status) {
		
		// System.out.println("Sending packet with status: " + status);
		
		// String message = unikey + ":" + status + ":" + segnum;
		this.latestStatus = status;
		byte[] buf = status.getBytes();
		
		for (P2PClient client : localServer.clients) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length, client.ip, client.port);
			try { localServer.serverSocket.send(packet); } catch (Exception e) { System.err.println(e); }
		}
	}
	
	@Override
	public void run() {
		while (true) {	
			try { Thread.sleep(ThreadLocalRandom.current().nextLong(1, 3) * 1000); } catch (Exception e) { System.err.println(e); }
			if (latestStatus != null) {
				sendPacket(latestStatus);
			}
		}
	}
	
	
}
