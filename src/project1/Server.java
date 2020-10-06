package project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

	/**
	 * main function that starts the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Server().run();
	}

	/**
	 * runs server
	 */
	private void run() {
		openServer();
	}

	/**
	 * opens server
	 */
	private void openServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(5000);

			while (true) {
				Socket socket = serverSocket.accept();

				Thread thread = new Thread(new ClientHandler(socket));
				thread.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * class for users which handles input message
	 * 
	 * @author jaeungj
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;
		private BufferedReader reader;
		private String key;
		private int KEY_LENGTH = 10;

		/**
		 * sets global variables
		 * 
		 * @param clientSocket unique socket for each players
		 */
		private ClientHandler(Socket clientSocket) {
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
				key = generateKey(KEY_LENGTH);
				System.out.println(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * what is run by thread start
		 */
		@Override
		public void run() {
			String receivedKey = "";
			String message;
			int stringLength = 0;
			try {
				while ((message = reader.readLine()) != null) {
					receivedKey += message;
					stringLength += 1;
					if (stringLength == 10) {
						if (isEqualKey(receivedKey)) {
							System.out.println("Horray, match");
						} else {
							stringLength = 0;
							receivedKey = "";
							System.out.println("try again.");
						}
					} else {
						// not enough strings. pass
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String generateKey(int keyLength) {
			String options = "aswd";
			String key = "";
			for (int i = 0; i < keyLength; i++) {
				Random random = new Random();
				key += options.charAt(random.nextInt(4));
			}
			return key;
		}

		private boolean isEqualKey(String userKey) {
			return key.equals(userKey);
		}

	}

}
