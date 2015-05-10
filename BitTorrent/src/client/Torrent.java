/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;

/**
 *
 * @author Sushil Mohite
 */
public class Torrent implements Serializable {
    
    private String[] trackerIP;
    private final String fileName;
    private String fileHash;
    private long fileSize;
    private int numberOfChunks;
    private int chunkSize;
    private long lastChunkSize;
    
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
    
    public Torrent(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the trackerIP
     */
    public String[] getTrackerIP() {
        return trackerIP;
    }

    /**
     * @param trackerIP the trackerIP to set
     */
    public void setTrackerIP(String[] trackerIP) {
        this.trackerIP = trackerIP;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the fileHash
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * @param fileHash the fileHash to set
     */
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the numberOfChunks
     */
    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    /**
     * @param numberOfChunks the numberOfChunks to set
     */
    public void setNumberOfChunks(int numberOfChunks) {
        this.numberOfChunks = numberOfChunks;
    }

    /**
     * @return the chunkSize
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * @param chunkSize the chunkSize to set
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * @return the lastChunkSize
     */
    public long getLastChunkSize() {
        return lastChunkSize;
    }

    /**
     * @param lastChunkSize the lastChunkSize to set
     */
    public void setLastChunkSize(long lastChunkSize) {
        this.lastChunkSize = lastChunkSize;
    }
}