package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientRequestor extends Thread {
	
	OngoingTorrent ot;
	String sender;
	
	public ClientRequestor(OngoingTorrent ot, String sender) {
		this.ot = ot;
		this.sender = sender;
	}
	
	public void run() {
		while (!ot.isCompletelyDownloaded()) {
			System.out.println("inside client requestor");
			List<Integer> chunks = new ArrayList<Integer>();
			int numOfChunks = ot.getNumOfChunks();
			
			for (int i = 0; i < numOfChunks; i++) {
				if (ot.getChunkStatus(i) == -1) {
					chunks.add(i);
				}
			}
			
			Random random = new Random();
			int chunkNumber = chunks.get(random.nextInt(chunks.size()));
			
			try {
				if (chunkNumber == (numOfChunks - 1)) {
					requestChunk(chunkNumber, true);
				} 
				else {
					requestChunk(chunkNumber, false);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(FileHandler.getHash(ot.getFileName()));
	}
	
	private void requestChunk(int chunkNumber, boolean isLastChunk) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		
		// Create request packet
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byteStream.write(1);
		byteStream.write(Utility.intToByteArray(chunkNumber));
		
		if (isLastChunk) {
			byteStream.write(Utility.intToByteArray(ot.getLastChunkSize()));
		}
		else {
			byteStream.write(Utility.intToByteArray(ot.getChunkSize()));
		}
		byte[] fileName = ot.getFileName().getBytes();
		byteStream.write(Utility.intToByteArray(fileName.length));
		byteStream.write(fileName);
		
		byte[] requestBuffer = byteStream.toByteArray(); 
		DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, InetAddress.getByName(sender), Utility.CLIENT_PORT);
		
		// Send upload packet
		socket.send(requestPacket);
		
		// Close socket
		socket.close();
	}
}
