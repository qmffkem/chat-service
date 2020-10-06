package project1;

import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is a client side program for maze game. it gets user input, sends the
 * data to server program.
 * 
 * @author jaeungj
 * @version 1.0.0
 */
public class MazeGame {
	private PrintWriter writer;

	/**
	 * main function that starts the program
	 * 
	 * @param args it is what passed to the method
	 */
	public static void main(String[] args) {
		new MazeGame().run();
	}

	/**
	 * this starts the program
	 */
	private void run() {
		if (connectServer()) {
			System.out.println("success");
			handleInput();

		} else {
			System.out.println("Check if server is on.");
		}
	}

	/**
	 * this connects to the game server and makes connection
	 * 
	 * @return returns true if connection is successful, false otherwise.
	 */
	private boolean connectServer() {
		int connectionCount = 0;
		while (connectionCount < 5) {
			try {
				Socket socket = new Socket("127.0.0.1", 5000);
				writer = new PrintWriter(socket.getOutputStream());
				return true;
			} catch (IOException e) {
				connectionCount += 1;
				System.err.printf(e + ", %d times...\n", connectionCount);
			}
		}
		return false;
	}

	/**
	 * handles key pressed and returns relevant input; legal values includes: left,
	 * right, up, down.
	 */
	private void handleInput() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String key = scanner.nextLine();
			switch (key) {
			case "w":
			case "s":
			case "a":
			case "d":
				sendKeyInput(key);
				break;
			default:
				System.out.println("Valid options: A, S, D, W");
			}
		}
	}

	/**
	 * sends key input to the server
	 * 
	 */
	private void sendKeyInput(String key) {
		writer.println(key);
		writer.flush();
	}
}
