package client;

import java.util.HashSet;

public class OngoingTorrent {

	Torrent torrent;
//	private boolean[] chunkDownloaded;
	private HashSet<Integer> chunksRemaining;
	private String location;
	private boolean completed;
	private String[] otherClients;
	private int[] chunkStatus;
	
	public OngoingTorrent(Torrent t, String location, boolean completed) {
		this.torrent = t;
		this.location = location;
		this.completed = completed;
		this.chunksRemaining = new HashSet<Integer>();
		this.chunkStatus = new int[torrent.getNumberOfChunks()];
		if(!completed) {
			for(int i = 0; i < torrent.getNumberOfChunks(); i++) {
				chunksRemaining.add(i);
				chunkStatus[i] = -1;
			}
		}
		otherClients = getConnectedClients();
	}
	
	public boolean isChunkDownloaded(int chunk) {
		return chunkStatus[chunk] != -1;
	}
	
	public int getChunkStatus(int chunk) {
		return chunkStatus[chunk];
	}
	
	public String[] getConnectedClients() {
		// Get clients from tracker
		otherClients = new String[] {"127.0.0.1"};
		return otherClients;
	}

	@Override
	public String toString() {
		return String.format("%-100s", torrent.getFileName()) + "x %";
	}

	public String getFileName() {
		return torrent.getFileName();
	}

	public String getProgress() {
		float total = torrent.getNumberOfChunks();
		float done = total - chunksRemaining.size();
		return String.format("%.2f", done/total) + " %";
	}

	public String getFileSize() {
		if(torrent.getFileSize() < Utility.KB) {
			return String.format("%.2f", torrent.getFileSize()) + " b";
		} else if(torrent.getFileSize() < Utility.MB) {
			return String.format("%.2f", torrent.getFileSize() / Utility.KB) + " kb";
		} else if(torrent.getFileSize() < Utility.GB) {
			return String.format("%.2f", torrent.getFileSize() / Utility.MB) + " mb";
		} else {
			return String.format("%.2f", torrent.getFileSize() / Utility.GB) + " gb";
		}
	}

	public String getNumOfConnectedPeers() {
		return "" + 0;
	}

	public int getChunkSize() {
		return torrent.getChunkSize();
	}

}
