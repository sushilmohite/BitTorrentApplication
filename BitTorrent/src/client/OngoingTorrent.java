package client;

import java.util.HashSet;

public class OngoingTorrent {

	private Torrent torrent;
//	private boolean[] chunkDownloaded;
	private HashSet<Integer> chunksRemaining;
	private String location;
	private boolean completed;
	
	public OngoingTorrent(Torrent t, String location, boolean completed) {
		this.torrent = t;
		this.location = location;
		this.completed = completed;
		this.chunksRemaining = new HashSet<Integer>();
		if(!completed) {
			for(int i = 0; i < torrent.numberOfChunks; i++) {
				chunksRemaining.add(i);
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("%-100s", torrent.fileName) + "x %";
	}

	public String getFileName() {
		return torrent.fileName;
	}

	public String getProgress() {
		float total = torrent.numberOfChunks;
		float done = total - chunksRemaining.size();
		return String.format("%.2f", done/total) + " %";
	}

	public String getFileSize() {
		if(torrent.fileSize < Utility.KB) {
			return String.format("%.2f", torrent.fileSize) + " b";
		} else if(torrent.fileSize < Utility.MB) {
			return String.format("%.2f", torrent.fileSize / Utility.KB) + " kb";
		} else if(torrent.fileSize < Utility.GB) {
			return String.format("%.2f", torrent.fileSize / Utility.MB) + " mb";
		} else {
			return String.format("%.2f", torrent.fileSize / Utility.GB) + " gb";
		}
	}

	public String getNumOfConnectedPeers() {
		return "" + 0;
	}

}
