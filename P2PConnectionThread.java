import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class P2PConnectionThread implements Runnable {
	// class that represents new threads for additional peer client connections

	P2PServer server;
	
	// peer information
	String unikey;
	ArrayList<String> messages;
	
	public P2PConnectionThread(P2PServer centralServer) {
		this.server = centralServer;
	}

	@Override
	public void run() {
		// listen for DatagramPackets, parse them, then sync that with the local server for printing
		
		// tracking the time since the last update for each client object
		while (true) {
			// System.out.println("Waiting for packet...");
			
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				server.serverSocket.receive(packet);
				String status = new String(packet.getData(), 0, packet.getLength());
				
				
				
				// need to parse things here, and check which client this new status belongs too. Then update that respective latestStatus
				String[] parsedElements = parseReceivedString(status);
				
				String recoveredUnikey = parsedElements[0];
				String recoveredStatus = parsedElements[1];
				int segnum = Integer.parseInt(parsedElements[2]);
				
				P2PClient currentClient = this.server.unikeyToClient.get(recoveredUnikey);
				
				// need to do a check with the segnum
				// if segnum is not present, the local var will be -1
				if (segnum >= currentClient.latestSegNum) {
					// Update the time since last update for the currentClient
					currentClient.timeOfLastUpdate = System.currentTimeMillis();
					
					// Update new status
					currentClient.statuses.addElement(recoveredStatus);
					currentClient.latestStatus = recoveredStatus;
					
					// Update segnum
					currentClient.latestSegNum = segnum;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static String[] parseReceivedString(String status) {
		int indexToID = status.indexOf(":");
		
		String recoveredUnikey = status.substring(0, indexToID);
		
		status = status.substring(indexToID + 1);
		String segNum = "-1";
		
		if (status.contains(":")) {
			int indexToStat = status.indexOf(":");
			segNum = status.substring(indexToStat + 1);
			status = status.substring(0, indexToStat);
		}
		
		String[] contents = new String[3];
		
		contents[0] = recoveredUnikey;
		contents[1] = status;
		contents[2] = segNum;
		
		return contents;
	}
}
