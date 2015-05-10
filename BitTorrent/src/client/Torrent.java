/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;
import java.util.Scanner;

/**
 *
 * @author Sushil Mohite
 */
public class Torrent implements Serializable {
    
    private final String fileName;
    private String fileHash;
    private long fileSize;
    private String[] trackerIP;
    private int numberOfChunks;
    private int chunkSize;
    private int lastChunkSize;
    
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
    
    public Torrent(String fileName, String fileHash, long fileSize, String[] trackerIP,  int numberOfChunks, int chunkSize, int lastChunkSize) {
		this.fileName = fileName;
		this.fileHash = fileHash;
		this.fileSize = fileSize;
		this.trackerIP = trackerIP;
		this.numberOfChunks = numberOfChunks;
		this.chunkSize = chunkSize;
		this.lastChunkSize = lastChunkSize;
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
    public int getLastChunkSize() {
        return lastChunkSize;
    }

    /**
     * @param lastChunkSize the lastChunkSize to set
     */
    public void setLastChunkSize(int lastChunkSize) {
        this.lastChunkSize = lastChunkSize;
    }
    
    @Override
    public String toString() {
    	StringBuffer buf = new StringBuffer(fileName);
	    buf.append(",");
	    buf.append(fileHash);
	    buf.append(",");
	    buf.append(fileSize);
	    buf.append(",");
    	for(int i = 0; i < trackerIP.length; i++) {
    	    buf.append(trackerIP[i]);
    	    if(i != trackerIP.length - 1) {
    		    buf.append(";");
    	    }
    	}
	    buf.append(",");
	    buf.append(numberOfChunks);
	    buf.append(",");
	    buf.append(chunkSize);
	    buf.append(",");
	    buf.append(lastChunkSize);
	    
	    return buf.toString();
	}
    
    public static Torrent decode(String str) {
    	Scanner sc = new Scanner(str);
    	sc.useDelimiter(",");
        String fileName = sc.next();
        String fileHash = sc.next();
        long fileSize = sc.nextLong();
        String[] trackerIP = sc.next().split(";");
        int numberOfChunks = sc.nextInt();
        int chunkSize = sc.nextInt();
        int lastChunkSize = sc.nextInt();
        sc.close();
    	return new Torrent(fileName, fileHash, fileSize, trackerIP, numberOfChunks, chunkSize, lastChunkSize);
    }
    
    public static void main(String[] args) {
		String str = "file1.img,1231782,1232,192.168.100.1;192.168.100.2,12,100,132";
		Torrent t = Torrent.decode(str);
		System.out.println(t);
	}
}