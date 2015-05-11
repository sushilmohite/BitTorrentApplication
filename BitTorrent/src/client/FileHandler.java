package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FileHandler {

	/**
	 * Extract a chunk from a file
	 * 
	 * @param fileName          name of the file to read from
	 * @param startPosition     start position for reading
	 * @param size              number of bytes to read
	 * @return                  byte[] containing bytes read. 
	 */
	public static byte[] getChunk(String fileName, int startPosition, int size) {
		byte[] bytes = null;
		try {
			RandomAccessFile raFile = new RandomAccessFile(fileName, "r");
			FileChannel fc = raFile.getChannel();
			
			fc.position(startPosition + 1);
			ByteBuffer buf = ByteBuffer.allocate(size);
			
			int bytesRead = fc.read(buf);
			
			if(bytesRead > 0) {
				bytes = Arrays.copyOf(buf.array(), bytesRead);
			} else {
				System.out.println(fileName + " " + startPosition + " " + size);
				System.out.println("No bytes read!");
			}
			
			raFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static void writeToFile(String fileName, int startPosition, byte[] bytes) {
		try {
			RandomAccessFile raFile = new RandomAccessFile(fileName, "rw");
			FileChannel fc = raFile.getChannel();
			
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			fc.write(buf, startPosition + 1);
			
			raFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getHash(String fileName) {
		String digest = null;
		try {
			Path path = Paths.get(fileName);
			byte[] fileBytes;
			fileBytes = Files.readAllBytes(path);
			MessageDigest cript = null;
			cript = MessageDigest.getInstance(Utility.HASH_ALGORITHM);
	        cript.reset();
	        cript.update(fileBytes);
	        digest = Utility.bytesToHex(cript.digest());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return digest;
	}
	
	public static String getHash(byte[] bytes) {
		String digest = null;
		try {
			MessageDigest cript = null;
			cript = MessageDigest.getInstance(Utility.HASH_ALGORITHM);
	        cript.reset();
	        cript.update(bytes);
	        digest = Utility.bytesToHex(cript.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return digest;
	}
	
	public static void main(String[] args) {
		String fileName = "hello.txt";
		byte[] bytes = FileHandler.getChunk(fileName, 5, 5);
		System.out.println(Utility.bytesToHex(bytes));
		bytes = Utility.hexToBytes("2e20492061");
		bytes = Utility.hexToBytes("1313131313");
		FileHandler.writeToFile(fileName, 200, bytes);
		bytes = FileHandler.getChunk(fileName, 0, 1000);
		System.out.println(Utility.bytesToHex(bytes));
	}

}
