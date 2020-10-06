package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private boolean quit = false;
	private ArrayList<PrintWriter> users;
	private ArrayList<String> usernames;

	public static void main(String[] args) {
		new Server().run();
	}

	public void run() {
		handleConnection();
	}

	private void handleConnection() {
		users = new ArrayList<PrintWriter>();
		usernames = new ArrayList<String>();
		try {
			ServerSocket serversoc = new ServerSocket(5000);

			while (!quit) {
				Socket socket = serversoc.accept();
				String username = "User" + socket.getRemoteSocketAddress().toString().split(":")[1];
				usernames.add(username);
				System.out.println(username + " entered.");
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				users.add(writer);

				Thread t = new Thread(new ClientHandler(socket, writer, username));
				t.start();
			}
			serversoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processMessage(String message) {
		users.forEach((user) -> {
			user.println(message);
			user.flush();
		});
	}

	private void processUsers(String username, String command) {
		users.forEach((user) -> {
			user.println(command + username);
			user.flush();
		});
	}

	private void sendUserList(PrintWriter currentUser, String currentUserName) {
		usernames.forEach((each) -> {
			if (!each.equals(currentUserName)) {
				currentUser.println("+ " + each);
				currentUser.flush();
			}
		});
	}

	private class ClientHandler implements Runnable {
		private BufferedReader reader;
		private Socket socket;
		private String username;

		public ClientHandler(Socket socket, PrintWriter writer, String username) {
			this.socket = socket;
			this.username = username;
			try {
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
				sendUserList(writer, username);
				processUsers(username, "+ ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					processMessage(message);
					System.out.println(message);
				}
				// case for disconnection
				usernames.remove(username);
				processUsers(username, "- ");
				System.out.println(username + " left.");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
