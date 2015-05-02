package tracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tracker {

	private final static int port = 1818;
	public static String backupTrackerIP;
	Scanner sc;
	public static Map<String, List<String>> clientInfo = new HashMap<String, List<String>>();

	public Tracker() {
		sc = new Scanner(System.in);
		System.out.println("Enter backup tracker IP");
		this.backupTrackerIP = sc.nextLine();
	}

	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(port);
			Tracker t = new Tracker();
			while (true) {
				Socket s = ss.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				String param = (br.readLine());
				t.new ManageRequest(s, param).start();
				Thread.sleep(100);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class ManageRequest extends Thread {

		private Socket socket;
		BufferedWriter bufferedWriter;

		// the function that is to be called
		int function;

		// list of parameters for that function
		String[] param;

		public ManageRequest(Socket socket, String param) {
			this.socket = socket;
			this.param = param.split(" ");
			this.function = Integer.parseInt(this.param[0]);
		}

		public boolean addClient(String hashValue, String clientIP,
				int addToBackUp) throws UnknownHostException, IOException {
			boolean success = false;

			if ((hashValue != null || hashValue != "")
					&& (clientIP != null || clientIP != "")) {
				if (Tracker.clientInfo.containsKey(hashValue)) {
					Tracker.clientInfo.get(hashValue).add(clientIP);
					success = true;
				} else {
					List<String> clients = new ArrayList<String>();
					clients.add(clientIP);
					Tracker.clientInfo.put(hashValue, clients);
					success = true;
				}
			}

			if (addToBackUp == 1) {
				Socket backupSocket = new Socket(Tracker.backupTrackerIP,
						Tracker.port);
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(backupSocket.getOutputStream()));
				String paramBackUp = "2" + " " + hashValue + " " + clientIP
						+ " " + "0";
				bufferedWriter.write(paramBackUp);
				bufferedWriter.flush();
			}

			return success;

		}

		public List<String> getClients(String hashValue) {
			List<String> clientList = new ArrayList<String>();
			if (Tracker.clientInfo.containsKey(hashValue)) {
				clientList = Tracker.clientInfo.get(hashValue);
			}
			return clientList;
		}

		public boolean addFile(String hashValue, String clientIP,
				int addToBackUp) throws UnknownHostException, IOException {
			boolean success = false;
			if (!Tracker.clientInfo.containsKey(hashValue)) {
				List<String> clients = new ArrayList<String>();
				clients.add(clientIP);
				Tracker.clientInfo.put(hashValue, clients);
				success = true;
			}

			if (addToBackUp == 1) {
				Socket backupSocket = new Socket(Tracker.backupTrackerIP,
						Tracker.port);
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(backupSocket.getOutputStream()));
				String paramBackUp = "1" + " " + hashValue + " " + clientIP
						+ " 0";
				bufferedWriter.write(paramBackUp);
				bufferedWriter.flush();
			}
			return success;
		}

		public void run() {
			// String param[] = null;

			switch (function) {
			case 1:
				try {
					addFile(param[1], param[2], Integer.parseInt(param[3]));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case 2:
				try {
					addClient(param[1], param[2], Integer.parseInt(param[3]));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				getClients(param[1]);
				break;
			}

		}
	}
}
