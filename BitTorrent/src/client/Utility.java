package client;

public class Utility {
	
	public static final int CLIENT_PORT = 10412;
	public static final String[] COLUMNS = {"Filename", "Progress", "Size", "# of Peers"};

	public static final float KB = 1024;
	public static final float MB = 1024 * 1024;
	public static final float GB = 1024 * 1024 * 1024;
	
	private static final char[] hexArray = "0123456789abcdef".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void main(String[] args) {
		System.out.println(KB);
		System.out.println(MB);
		System.out.println(GB);
	}
}
