package client;

import java.io.File;
import java.io.Serializable;

public class OngoingTorrent implements Serializable {

	private static final long serialVersionUID = 1L;
	Torrent torrent;
	private String location;
	private boolean completed;
	private String[] otherClients;
	private byte[] chunkStatus;
	
	public OngoingTorrent(Torrent t, String location, boolean completed) {
		this.torrent = t;
		this.location = location;
		this.completed = completed;
		this.chunkStatus = new byte[torrent.getNumberOfChunks()];
		if(!completed) {
			for(int i = 0; i < torrent.getNumberOfChunks(); i++) {
				chunkStatus[i] = -1;
			}
		}
	}
	
	public int getNumOfChunks() {
		return torrent.getNumberOfChunks();
	}
	
	public String getClient(int clientId) {
		return otherClients[clientId];
	}
	
	public int getChunkStatus(int chunk) {
		return chunkStatus[chunk];
	}
	
	public String[] getTrackers() {
		return torrent.getTrackerIP();
	}
	
	public void setOtherClients(String[] otherClients) {
		this.otherClients = otherClients;
	}
	
	public String[] getConnectedClients() {
		return otherClients;
	}

	@Override
	public String toString() {
		return String.format("%-100s", torrent.getFileName()) + "x %";
	}

	public String getFileName() {
		return torrent.getFileName();
	}
	
	public String getAbsoluteFileName() {
		return location + File.separatorChar + getFileName();
	}

	public String getProgress() {
		float total = torrent.getNumberOfChunks();
		float done = 0;
		for(int i = 0; i < chunkStatus.length; i++) {
			if(chunkStatus[i] != -1) {
				done++;
			}
		}
		return String.format("%.2f", 100*done/total) + " %";
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
		if(otherClients != null) {
			return "" + otherClients.length;
		} else {
			return "" + 0;
		}
	}

	public int getChunkSize() {
		return torrent.getChunkSize();
	}
	
	public int getLastChunkSize() {
		return torrent.getLastChunkSize();
	}
	
	public String getFileHash() {
		return torrent.getFileHash();
	}

	public boolean isCompletelyDownloaded() {
		return completed;
	}
	
	public void setDownloaded(int chunk, String clientIp) {
		int index = -1;
		for(int i = 0; i < otherClients.length; i++) {
			if(clientIp.equals(otherClients[i])) {
				index = i;
			}
		}
		
		if(index != -1) {
			chunkStatus[index] = (byte) index;
		}
		
		completed = checkCompletion();
	}

	private boolean checkCompletion() {
		for(int i = 0; i < getNumOfChunks(); i++) {
			if(chunkStatus[i] == -1) {
				return false;
			}
		}
		return true;
	}

}
