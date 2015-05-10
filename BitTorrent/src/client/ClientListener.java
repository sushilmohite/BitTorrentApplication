package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class ClientListener extends Thread {
	
	DatagramSocket socket;
	ClientUI clientUI;
	
	public ClientListener(ClientUI clientUI) {
		try {
			this.socket = new DatagramSocket(Utility.CLIENT_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.clientUI = clientUI;
	}
	
	public void run() {
		
		// Create resources for incoming data
		byte[] dataBuffer = new byte[Utility.MAX_CHUNK_SIZE];
		DatagramPacket dataPacket = new DatagramPacket(dataBuffer, dataBuffer.length);
		
		while (true) {
			try {
				socket.receive(dataPacket);
				new HandlePacket(dataPacket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class HandlePacket extends Thread {
		
		DatagramPacket dataPacket;
		
		public HandlePacket(DatagramPacket dataPacket) {
			this.dataPacket = dataPacket;
		}
		
		public void run() {
			switch(this.dataPacket.getData()[0]) {
			
			case 1:
				try {
					sendData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case 2:
				receivedData();
				break;
				
			default:
				System.out.println("Invalid data received from " + dataPacket.getAddress());
				break;
			}
		}
		
		private void sendData() throws IOException {
			int chunkNumber = Integer.parseInt(new String(dataPacket.getData(), 1, 4));
			int chunkSize = Integer.parseInt(new String(dataPacket.getData(), 5, 4));
			int fileNameSize = Integer.parseInt(new String(dataPacket.getData(), 9, 4));
			String fileName = new String(dataPacket.getData(), 13, fileNameSize);
			int startPosition = chunkNumber * chunkSize;
			byte[] data = FileHandler.getChunk(fileName, startPosition, chunkSize);
			
			// Create upload packet
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byteStream.write(2);
			byteStream.write(dataPacket.getData(), 1, dataPacket.getData().length - 1);
			byteStream.write(data);
			byte[] uploadBuffer = byteStream.toByteArray(); 
			DatagramPacket uploadPacket = new DatagramPacket(uploadBuffer, uploadBuffer.length, dataPacket.getAddress(), dataPacket.getPort());
			
			// Send upload packet
			socket.send(uploadPacket);
		}
		
		private void receivedData() {
			int chunkNumber = Integer.parseInt(new String(dataPacket.getData(), 1, 4));
			int chunkSize = Integer.parseInt(new String(dataPacket.getData(), 5, 4));
			int fileNameSize = Integer.parseInt(new String(dataPacket.getData(), 9, 4));
			String fileName = new String(dataPacket.getData(), 13, fileNameSize);
			int startPosition = chunkNumber * chunkSize;
			int dataOffset = 13 + fileNameSize;
			byte[] data = Arrays.copyOfRange(dataPacket.getData(), dataOffset, dataPacket.getLength() - 1);
			
			FileHandler.writeToFile(fileName, startPosition, data);
			clientUI.updateUI(fileName, dataPacket.getAddress().getHostAddress());
		}
	}
}
