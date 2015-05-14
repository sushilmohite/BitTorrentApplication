package tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tracker {

	final private static int PORT = 1818;
	private String backupTrackerIP;
	private Map<String, List<String>> clientInfo;
	
	public Tracker() {
		clientInfo = new HashMap<String, List<String>>();
	}

	public static void main(String[] args) {
		
		Tracker tracker = new Tracker();
		try {
			System.out.println("Tracker running on " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("Port: " + PORT);
		} catch (UnknownHostException ex) {
			System.out.println(ex.getMessage());
		}
		/*Scanner sc = new Scanner(System.in);
		System.out.println("Enter backup tracker IP");
		tracker.backupTrackerIP = sc.nextLine();
		sc.close();*/
		
		tracker.run();
	}
	
	private void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				new ManageRequest(serverSocket.accept()).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ManageRequest extends Thread {

		private Socket socket;

		public ManageRequest(Socket socket) {
			this.socket = socket;
		}

		private boolean addClient(String hashValue, String clientIP, boolean addToBackUp) throws Exception {
			boolean success = false;

			if (hashValue != null && !hashValue.equals("") && clientIP != null && !clientIP.equals("")) {
				if (clientInfo.containsKey(hashValue)) {
					if(!clientInfo.get(hashValue).contains(clientIP)) {
						clientInfo.get(hashValue).add(clientIP);
					}
				} else {
					List<String> clients = new ArrayList<String>();
					clients.add(clientIP);
					clientInfo.put(hashValue, clients);
				}
				success = true;
			}

			/*try {
				if (addToBackUp) {
					Socket backupSocket = new Socket(backupTrackerIP, PORT);
					PrintWriter out = new PrintWriter(backupSocket.getOutputStream(), true);
					ObjectInputStream br = new ObjectInputStream(socket.getInputStream());
					String paramBackUp = "1" + " " + hashValue + " " + clientIP	+ " " + "false";
					out.println(paramBackUp);
					System.out.println(br.readObject());
					br.close();
					out.close();
					backupSocket.close();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}*/

			return success;
		}

		private List<String> getClients(String hashValue) {
			if (clientInfo.containsKey(hashValue)) {
				return clientInfo.get(hashValue);
			}
			return null;
		}

		public void run() {

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				String input = br.readLine();
				System.out.println("Incoming Request");
				String[] params = input.split(" ");
			
				switch (Integer.parseInt(params[0])) {
				
				case 1:
					out.writeObject(addClient(params[1], params[2], Boolean.parseBoolean(params[3])));
					break;
				
				case 2:
					out.writeObject(getClients(params[1]));
					break;
			
				default:
					System.out.println("Invalid input: " + input + " from " + socket.getInetAddress().getHostAddress());
					break;
				}
			
				br.close();
				out.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
