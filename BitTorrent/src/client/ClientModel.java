package client;

import java.io.File;

public class ClientModel {
	
	
	public ClientModel(OngoingTorrent ot) {
		
	}
	
	
	
	public static void main(String[] args) {
		OngoingTorrent ot = null;
		if(args[0] == "up") {
			File f = new File("/home/kedarnath/Desktop/WinDownloads/ubuntu-15.04-desktop-amd64.iso");
			Torrent t = new Torrent(f.getName(), f.length(), 10240);
			ot = new OngoingTorrent(t, f.getParent(), true);
			System.out.println("Uploading " + f.getName());
		} else {
			File f = new File("/home/kedarnath/Desktop/ubuntu-15.04-desktop-amd64.iso");
			Torrent t = new Torrent(f.getName(), f.length(), 10240);
			ot = new OngoingTorrent(t, f.getParent(), false);
			System.out.println("Downloading " + f.getName());
		}
		new ClientModel(ot);
		
	}
	
}
