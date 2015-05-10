package client;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import BitTorrentWebService.Torrent;

public class ClientModel {
	
	private OngoingTorrent ot;
	
	public ClientModel(OngoingTorrent ot) {
		this.ot = ot;
		this.acceptConnections();
	}
	
	private void acceptConnections() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(Utility.CLIENT_PORT);
			byte[] receiveData = new byte[ot.getChunkSize()];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				new RequestHandler(receivePacket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		OngoingTorrent ot = null;
		if(args[0].equals("up")) {
			File f = new File("~/Desktop/test.img");
			Torrent t = new Torrent(f.getName(), f.length(), 10240);
			ot = new OngoingTorrent(t, f.getParent(), true);
			System.out.println("Uploading " + f.getName());
		} else {
			File f = new File("~/Desktop/testdown.img");
			Torrent t = new Torrent(f.getName(), f.length(), 10240);
			ot = new OngoingTorrent(t, f.getParent(), false);
			System.out.println("Downloading " + f.getName());
		}
		new ClientModel(ot);
		
	}
	
	private class RequestHandler extends Thread {

		byte[] receivedBytes;
		String senderIp;
		
		public RequestHandler(DatagramPacket packet) {
			receivedBytes = packet.getData();
			senderIp = packet.getAddress().getHostAddress();
			System.out.println(senderIp);
		}
		
		@Override
		public void run() {
			byte opcode = receivedBytes[0];
			
			switch(opcode) {
				case Utility.REQUEST: {
					
				}
			}
		}

	}
	
}
