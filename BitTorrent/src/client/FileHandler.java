package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import javax.swing.UIManager;

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
			
			fc.position(startPosition);
			ByteBuffer buf = ByteBuffer.allocate(size);
			
			int bytesRead = fc.read(buf);
			
			if(bytesRead > 0) {
				bytes = Arrays.copyOf(buf.array(), bytesRead);
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
			fc.write(buf, startPosition);
			
			raFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
