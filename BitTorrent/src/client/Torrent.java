package client;

public class Torrent {
	public String fileName;
	public long fileSize;
	
	public int numberOfChunks;
	public int chunkSize;
	
	public String hashOfFile;
	public String[] hashOfChunk;
	
	public Torrent(String fileName, long fileSize, int chunkSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.chunkSize = chunkSize;
		if(fileSize % chunkSize == 0) {
			this.numberOfChunks = (int) (fileSize / chunkSize);
		} else {
			this.numberOfChunks = (int) (fileSize / chunkSize) + 1;
		}
	}
}
