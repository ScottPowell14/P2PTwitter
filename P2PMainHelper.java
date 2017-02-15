import java.util.Scanner;

public class P2PMainHelper implements Runnable {
	// Helper class that creates a new thread that handles intermittent posting of status updates
	
	P2PLocalClient localClient;
	String unikey;
	P2PServer server;
	
	public P2PMainHelper(P2PLocalClient localClient, String unikey, P2PServer server) {
		this.localClient = localClient;
		this.unikey = unikey;
		this.server = server;
	}
	
	
	@Override
	public void run() {
		int segnum = 0;
		Scanner in = new Scanner(System.in);
		// Start status retrieving and printing once a status is entered
		while (true) {
			System.out.print("Status: ");
			String status = in.nextLine();
			
			if (status.isEmpty()) {
				System.out.println("Status is empty. Retry.");
				continue;
			} else if (status.length() > 140) {
				System.out.println("Status is too long, 140 characters max. Retry.");
				continue;
			}
			
			System.out.println("### P2P tweets ###");
			System.out.println("# " + localClient.pseudo + " (myself): " + status);
			
			// send the datagram first to each of the peers
			if (localClient != null) {
				String newStatus = unikey + ":" + status + ":" + segnum;
				localClient.sendPacket(newStatus);
			}
			
			server.printSavedMessages();
			
			System.out.println("### End tweets ###");
			segnum += 1;
		}
	}
	
	
	

}
