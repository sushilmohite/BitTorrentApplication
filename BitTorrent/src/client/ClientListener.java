package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class ClientListener extends Thread {
	
	ClientUI clientUI;
	
	public ClientListener(ClientUI clientUI) {
		this.clientUI = clientUI;
	}
	
	@Override
	public void run() {
		
		// Create resources for incoming data
		byte[] dataBuffer = new byte[Utility.MAX_CHUNK_SIZE];
		DatagramPacket dataPacket = new DatagramPacket(dataBuffer, dataBuffer.length);

		while (true) {
			try {
				DatagramSocket socket = new DatagramSocket(Utility.CLIENT_PORT);	
				socket.receive(dataPacket);
				socket.close();
				new HandlePacket(dataPacket).start();
				
				sleep(100);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class HandlePacket extends Thread {
		
		DatagramPacket dataPacket;
		
		public HandlePacket(DatagramPacket dataPacket) {
			this.dataPacket = dataPacket;
		}
		
		@Override
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
			int chunkNumber = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 1, 5));
			int chunkSize = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 5, 9));
			int fileNameSize = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 9, 13));
			System.out.println(chunkNumber);
			String fileName = new String(dataPacket.getData(), 13, fileNameSize);
			int startPosition = chunkNumber * chunkSize;
			byte[] data = FileHandler.getChunk(fileName, startPosition, chunkSize);
			
			if(data == null) {
				return;
			}
			// Create upload packet
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byteStream.write(2);
			byteStream.write(Utility.intToByteArray(chunkNumber));
			byteStream.write(Utility.intToByteArray(chunkSize));
			byteStream.write(Utility.intToByteArray(fileNameSize));
			byteStream.write(fileName.getBytes());
			byteStream.write(data);
			
			byte[] uploadBuffer = byteStream.toByteArray(); 
			System.out.println("ClientListener: " + Arrays.toString(uploadBuffer));
			DatagramPacket uploadPacket = new DatagramPacket(uploadBuffer, uploadBuffer.length, /*dataPacket.getAddress()*/InetAddress.getByName("192.168.100.113"), Utility.CLIENT_PORT);
			
			DatagramSocket socket = new DatagramSocket(); 
			
			// Send upload packet
			socket.send(uploadPacket);
			socket.close();
		}
		
		private void receivedData() {
			int chunkNumber = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 1, 5));
			int chunkSize = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 5, 9));
			int fileNameSize = Utility.byteArrayToInt(Arrays.copyOfRange(dataPacket.getData(), 9, 13));
			String fileName = new String(dataPacket.getData(), 13, fileNameSize);
			int startPosition = chunkNumber * chunkSize;
			int dataOffset = 13 + fileNameSize;
			byte[] data = Arrays.copyOfRange(dataPacket.getData(), dataOffset, dataPacket.getLength());
			
			System.out.println("Writing to file..");
			FileHandler.writeToFile(fileName, startPosition, data);
			clientUI.updateUI(fileName, dataPacket.getAddress().getHostAddress(), chunkNumber);
		}
	}
}
