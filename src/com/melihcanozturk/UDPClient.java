package com.melihcanozturk;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;

public class UDPClient implements Runnable {

	public final static int SERVERPORT = 8080;
	static DatagramSocket udpClientSocket = null;
	static InetAddress serverIPAddress;
	private JFrame frame;
	private JTextField txtMesaj;
	private JTextArea mArea;
	JLabel lblNewLabel;
	JLabel lblMesaj;
	JScrollPane scrollPane;

	JButton btnSend;
	static {
		try {
			serverIPAddress = InetAddress.getByName("localhost");
		} catch (IOException er) {
			System.out.println(er);
		}
	}

	static {
		try {
			udpClientSocket = new DatagramSocket();
		} catch (IOException er) {
			System.out.println(er);
		}
	}

	UDPClient() {
		frame = new JFrame();

		frame.setBounds(100, 100, 483, 414);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 30, 437, 292);
		frame.getContentPane().add(scrollPane);

		mArea = new JTextArea();
		scrollPane.setViewportView(mArea);

		lblNewLabel = new JLabel("Mesaj ekranı");
		lblNewLabel.setBounds(20, 11, 128, 14);
		frame.getContentPane().add(lblNewLabel);
		lblMesaj = new JLabel("Message");
		lblMesaj.setBounds(20, 336, 46, 14);
		frame.getContentPane().add(lblMesaj);

		txtMesaj = new JTextField();
		txtMesaj.setBounds(76, 333, 288, 20);
		frame.getContentPane().add(txtMesaj);
		txtMesaj.setColumns(10);

		byte[] sendData = new byte[1024];

		String clientRequest = "first;" + txtMesaj;

		sendData = clientRequest.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, SERVERPORT);

		try {

			udpClientSocket.send(sendPacket);
		} catch (IOException er) {
			System.out.println(er);
		}

		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
				byte[] data = new byte[1024];
				String info = time + ": " + txtMesaj.getText(); // specifies format to be sent
				mArea.setText(mArea.getText() + txtMesaj.getText() + "\n");
				data = info.getBytes();
				DatagramPacket output = new DatagramPacket(data, data.length, serverIPAddress, SERVERPORT);
				try {
					udpClientSocket.send(output);
				} catch (IOException er) {
					System.out.println(er);
				}
				data = new byte[1024];
				txtMesaj.setText("");
			}
		});
		btnSend.setBounds(368, 332, 89, 23);
		frame.getContentPane().add(btnSend);
		frame.setVisible(true);
	}

	public void run() {

		byte[] info = new byte[1024];
		String s = "";
		while (true) {
			DatagramPacket dp = new DatagramPacket(info, info.length);
			try {
				udpClientSocket.receive(dp);
				s = new String(dp.getData());
				mArea.append(s + "\n");
			} catch (IOException er) {
				System.out.println(er);
			}
		}
	}

	public static void main(String[] args) {

		Thread t = new Thread(new UDPClient());
		t.start();

	}

}
