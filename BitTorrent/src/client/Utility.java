package client;

public class Utility {
	
	public static final int CLIENT_PORT = 10412;
	
	public static final int TRACKER_PORT = 1818;
	
	public static final String WEB_SERVICE_IP = "129.21.159.17";
	
	public static final String[] COLUMNS = {"Filename", "Progress", "Size", "# of Peers"};

	public static final String ONGOING_TORRENTS_FILE = "OngoingTorrents.info";
	
	// OP codes
	public static final byte REQUEST = 0x01;
	
	public static final float KB = 1024;
	public static final float MB = 1024 * 1024;
	public static final float GB = 1024 * 1024 * 1024;
	
	public static final int MAX_CHUNK_SIZE = (int) (50 * KB);
	
	public static final String HASH_ALGORITHM = "SHA-1";
	
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
	
	public static byte[] hexToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	public static byte[] intToByteArray(int a) {
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
	
	public static void main(String[] args) {
		System.out.println(KB);
		System.out.println(MB);
		System.out.println(GB);
		System.out.println(REQUEST);
		System.out.println(FileHandler.getHash("Certificate_Orig.pdf"));
	}
}
