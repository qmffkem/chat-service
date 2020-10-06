package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

public class Chat {
	private JTextArea incoming;
	private JTextArea userList;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	private String username;
//	private FileOutputStream filestream;
//	private ObjectOutputStream os;

	public static void main(String[] args) {
		new Chat().run();
	}

	private void run() {
		if (setupNetworking()) {
			gui();
			Thread t = new Thread(new ServerReceiver());
			t.start();
		} else {
			System.err.println("Check server status");
		}
	}

	private boolean setupNetworking() {
		try {
			Socket socket = new Socket("146.148.98.43", 5000);

			// for connection to the server for input messages
			InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(streamReader);

			// for connection to the server for output messages
			writer = new PrintWriter(socket.getOutputStream());
			username = "User" + socket.getLocalPort();

			return true;

		} catch (IOException e) {
			return false;
		}
	}

	private void gui() {
		JFrame frame = new JFrame("Chat client");
		JPanel mainPanel = new JPanel();

		// sets incoming textbox properties, wrap the incoming with scroller
		incoming = new JTextArea(15, 30);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane TextBox = new JScrollPane(incoming);
		TextBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		TextBox.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// sets outgoing textbox properties
		outgoing = new JTextField(10);
		JButton sendButton = new JButton("send");
		sendButton.addActionListener(new SendButtonListener());

		// sets userlist textbox properties.
		userList = new JTextArea(15, 10);
		userList.setLineWrap(true);
		userList.setWrapStyleWord(true);
		userList.setEditable(false);
		JScrollPane userBox = new JScrollPane(userList);
		userBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		userBox.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel textArea = new JPanel();
		textArea.setLayout(new BoxLayout(textArea, BoxLayout.X_AXIS));
		textArea.add(TextBox);
		textArea.add(userBox);

		JPanel userSendArea = new JPanel();
		userSendArea.setLayout(new BoxLayout(userSendArea, BoxLayout.X_AXIS));
		userSendArea.add(outgoing);
		userSendArea.add(sendButton);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(textArea);
		mainPanel.add(userSendArea);

		frame.add(BorderLayout.CENTER, mainPanel);
		frame.setSize(400, 320);
//		frame.pack();
		frame.setVisible(true);
	}

	private class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			writer.println(username + ": " + outgoing.getText());
			writer.flush();

			outgoing.setText("");
			outgoing.requestFocus();
		}
	}

	private class ServerReceiver implements Runnable {
		private ArrayList<String> messages;
		private String filename;

		private ServerReceiver() {
			messages = new ArrayList<String>();
//			filename = "_savedMessage.txt";
			filename = username + "_message.txt";
			restoreMessages();
		}

		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					if (message.split(" ")[0].equals("+")) {
						System.out.println(message.split(" ")[1] + " added");
						userList.append(message.split(" ")[1] + "\n");
					} else if (message.split(" ")[0].equals("-")) {
						System.out.println(message.split(" ")[1] + " left");
						userList.setText(userList.getText().replace(message.split(" ")[1] + "\n", ""));
					} else {
						messages.add(message);
						saveMessages(message);
//					os.writeObject(message);
//					os.close();

						incoming.append(message + "\n");
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * saves given message to the file with the associated filename.
		 * 
		 * @param message message is what saved on the file.
		 */
		private void saveMessages(String message) {
			try {
				FileWriter fw = new FileWriter(filename, true);
				fw.write(message + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * reads saved messages from the filename and prints on the message area
		 */
		private void restoreMessages() {
			try {
				FileReader fr = new FileReader(filename);
				BufferedReader savedMessageReader = new BufferedReader(fr);
				String savedMessage;
				while ((savedMessage = savedMessageReader.readLine()) != null) {
					incoming.append(savedMessage + "\n");
				}
				savedMessageReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
