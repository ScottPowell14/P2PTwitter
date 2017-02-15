import java.net.InetAddress;
import java.util.Vector;
public class P2PClient {
	
	// Peer Client Info
	InetAddress ip;
	String pseudo;
	String unikey;
	int port;
	
	// Local Server Reference
	P2PServer localServer;
	
	// Status-related variables
	Vector<String> statuses = new Vector<>();
	String latestStatus;
	int latestSegNum;
	
	// Last updated status time
	long timeOfLastUpdate;
	
	public P2PClient(InetAddress ip, String pseudo, String unikey, int port, P2PServer server) {
		this.ip = ip;
		this.pseudo = pseudo;
		this.unikey = unikey;
		this.port = port;
		this.localServer = server;
		this.latestSegNum = -1;
		this.timeOfLastUpdate = System.currentTimeMillis();
	}

}
