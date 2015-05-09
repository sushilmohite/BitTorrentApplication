package client;

public class OngoingTorrent {

	private Torrent torrent;
	private boolean[] chunkDownloaded;
	private String location;
	private boolean completed;
	
	public OngoingTorrent(Torrent t, String location, boolean completed) {
		this.torrent = t;
		this.location = location;
		this.completed = completed;
		chunkDownloaded = new boolean[torrent.numberOfChunks];
	}
	
	@Override
	public String toString() {
		return String.format("%-100s", torrent.fileName) + "x %";
	}

	public String getFileName() {
		return torrent.fileName;
	}

	public String getProgress() {
		int count = 0;
		for(int i = 0; i < chunkDownloaded.length; i++) {
			if(chunkDownloaded[i]) {
				count++;
			}
		}
		return String.format("%.2f", count/(float)chunkDownloaded.length) + " %";
	}

	public String getFileSize() {
		return String.format("%.2f", torrent.fileSize / 1024.0) + " kb";
	}

	public String getNumOfConnectedPeers() {
		return "" + 0;
	}

}
