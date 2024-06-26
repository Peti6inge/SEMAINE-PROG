/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.uga.miashs.dciss.chatservice.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.sql.*;

/**
 * Manages the connection to a ServerMsg. Method startSession() is used to
 * establish the connection. Then messages can be send by a call to sendPacket.
 * The reception is done asynchronously (internally by the method receiveLoop())
 * and the reception of a message is notified to MessagesListeners. To register
 * a MessageListener, the method addMessageListener has to be called. Session
 * are closed thanks to the method closeSession().
 */
public class ClientMsg {

	private String serverAddress;
	private int serverPort;

	private Socket s;
	private DataOutputStream dos;
	private DataInputStream dis;

	private String username;

	private List<MessageListener> mListeners;
	private List<ConnectionListener> cListeners;
	private Connection cnx;

	/**
	 * Create a client with an existing id, that will connect to the server at the
	 * given address and port
	 * 
	 * @param id      The client id
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(String username, String address, int port) {
		serverAddress = address;
		serverPort = port;
		this.username = username;
		mListeners = new ArrayList<>();
		cListeners = new ArrayList<>();

	}

	/**
	 * Register a MessageListener to the client. It will be notified each time a
	 * message is received.
	 * 
	 * @param l
	 */
	public void addMessageListener(MessageListener l) {
		if (l != null)
			mListeners.add(l);
	}

	protected void notifyMessageListeners(Packet p) {
		mListeners.forEach(x -> x.messageReceived(p));
	}

	/**
	 * Register a ConnectionListener to the client. It will be notified if the
	 * connection start or ends.
	 * 
	 * @param l
	 */
	public void addConnectionListener(ConnectionListener l) {
		if (l != null)
			cListeners.add(l);
	}

	protected void notifyConnectionListeners(boolean active) {
		cListeners.forEach(x -> x.connectionEvent(active));
	}

	public String getusername() {
		return username;
	}

	private byte[] readFile(String filePath) throws IOException {
		File file = new File(filePath);
		byte[] fileContent = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(fileContent);
		fis.close();
		return fileContent;
	}

	/**
	 * Method to be called to establish the connection.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void startSession(String password, String username) throws UnknownHostException {
		if (s == null || s.isClosed()) {
			try {
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeUTF(username);
				dos.writeUTF(password);
				dos.flush();
				// start the receive loop
				new Thread(() -> receiveLoop()).start();
				notifyConnectionListeners(true);
			} catch (IOException e) {
				e.printStackTrace();
				// error, close session
				closeSession();
			}
		}
	}

	/**
	 * Send a packet to the specified destination (etiher a userId or groupId)
	 * 
	 * @param destId the destinatiion id
	 * @param data   the data to be sent
	 */
	public void sendPacket(String destUsername, byte[] data, byte fichier, byte[] nomFichier) {
		try {
			synchronized (dos) {
				dos.writeUTF(destUsername);
				dos.writeInt(data.length);
				dos.write(fichier);
				dos.writeInt(nomFichier.length);
				dos.write(nomFichier);
				dos.write(data);
				dos.flush();
			}
		} catch (IOException e) {
			// error, connection closed
			e.printStackTrace();
			closeSession();
		}
	}

	/**
	 * Start the receive loop. Has to be called only once.
	 */
	private void receiveLoop() {
		try {
			while (s != null && !s.isClosed()) {

				String sender = dis.readUTF();
				String dest = dis.readUTF();
				int length = dis.readInt();
				byte fichier = dis.readByte();
				int lengthnomFichier = dis.readInt();
				byte[] nomFichier = new byte[lengthnomFichier];
				dis.readFully(nomFichier);
				byte[] data = new byte[length];
				dis.readFully(data);
				notifyMessageListeners(new Packet(sender, fichier, dest, data, nomFichier));

			}
		} catch (IOException e) {
			// error, connection closed
			e.printStackTrace();
		}
		closeSession();
	}

	public void closeSession() {
		try {
			if (s != null)
				s.close();
		} catch (IOException e) {
		}
		s = null;
		notifyConnectionListeners(false);
	}

	private void stockageBDD(String sender, String msg, boolean reception) {
		try {

			PreparedStatement pstmt = cnx.prepareStatement("INSERT INTO MsgUser" + username + " VALUES (?,?,?)");

			pstmt.setString(1, sender);
			pstmt.setString(2, msg);
			pstmt.setBoolean(3, reception);

			boolean inserted = pstmt.executeUpdate() == 1;

			ResultSet res = cnx.createStatement().executeQuery("SELECT * FROM MsgUser" + username);
			System.out.println("---------------");
			while (res.next()) {
				System.out.println(res.getString(1) + " - " + res.getString(2) + " - " + res.getBoolean(3));
			}
			System.out.println("---------------");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

		// Authentification

		String password;
		String usernameSaisi = null;
		boolean creation = false;
		Scanner sc = new Scanner(System.in);

		if (args.length >= 2) {
			System.out.println("2 args");
			usernameSaisi = args[0];
			password = args[1];
		} else {
			System.out.println("Voulez-vous créer un compte? (o/n)");
			creation = sc.nextLine().equals("o") ? true : false;
			// Création d'un compte
			System.out.println("Saisissez votre nom d'utilisateur :");
			usernameSaisi = sc.nextLine();
			System.out.println("Saisissez votre mot de passe :");
			password = sc.nextLine();

		}
		ClientMsg c = new ClientMsg(usernameSaisi, "localhost", 1666);
		// add a dummy listener that print the content of message as a string

		c.addMessageListener(p -> {
			if (p.fichier == (byte) 0)
				c.stockageBDD(p.srcUsername, new String(p.data), true);
			if (p.fichier == (byte) 1) {
				c.stockageBDD(p.srcUsername, p.srcUsername + " vous a été envoyé le fichier suivant : " + new String(p.nomFichier),
						true);
				FileOutputStream fos = null;
				try {
					String name = new String(p.nomFichier);
					fos = new FileOutputStream("RECU/" + name);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.write(p.data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// add a connection listener that exit application when connection closed
		c.addConnectionListener(active -> {
			if (!active)
				System.exit(0);
		});

		c.startSession(password, c.username);
		if (creation) {
			try {
				c.cnx = DriverManager.getConnection("jdbc:derby:target/" + c.getusername() + ";create=true");
			} catch (SQLException e) {
				System.out.println("PAS DE CONNEXION!");
				c.closeSession();
			}
			try {
				c.cnx.createStatement().executeUpdate("DROP TABLE MsgUser" + c.getusername());
			} catch (SQLException e) {

			}
			try {
				c.cnx.createStatement().executeUpdate(
						"CREATE TABLE MsgUser" + c.getusername() + " (username VARCHAR(255), msg VARCHAR(255), reception BOOLEAN)");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				c.cnx = DriverManager.getConnection("jdbc:derby:target/" + c.getusername() + ";create=false");
			} catch (SQLException e) {
				System.out.println("PAS DE CONNEXION!");
				c.closeSession();
			}
		}

		System.out.println("Vous êtes : " + c.getusername());

		String lu = null;
		while (!"\\quit".equals(lu)) {
			try {
				System.out.println("A qui voulez vous écrire ? ");
				String dest = sc.nextLine();
				if (dest.equals("x")) {
					System.out.println("Deconnexion");
					c.closeSession();
				}

				if (dest.equals("0")) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(bos);
					// Création de groupe
					System.out.println(
							"Que voulez-vous faire? (1 : création de groupe, 2 : Suppression de groupe, 3 : Ajout d'un membre dans un groupe, 4 : Suppression d'un membre dans un groupe");
					int choix = Integer.parseInt(sc.nextLine());
					dos.writeByte(choix);
					switch (choix) {
					case 1: // création de groupe
						System.out.println("Nom du groupe ?");
						lu = sc.nextLine();
						dos.writeUTF(lu);
						System.out.println("Nom des membres ? (séparés par des virgules)");
						lu = sc.nextLine();
						String[] parts = lu.split(","); // Diviser la chaîne en sous-chaînes en utilisant la virgule
														// comme délimiteur
						dos.writeInt(parts.length);
						for (String part : parts) {
							dos.writeUTF(part);
						}
						dos.flush();
						c.sendPacket("0", bos.toByteArray(), (byte) 0, new byte[0]);
						break;
					case 2: // suppression de groupe
						System.out.println("Nom du groupe?");
						String groupe = sc.nextLine();
						dos.writeUTF(groupe);
						dos.flush();
						c.sendPacket("0", bos.toByteArray(), (byte) 0, new byte[0]);
						break;
					case 3: // Ajout d'un nouveau membre
						System.out.println("Nom du groupe?");
						String groupe2 = sc.nextLine();
						dos.writeUTF(groupe2);
						System.out.println("Nom du membre à ajouter?");
						String membre = sc.nextLine();
						dos.writeUTF(membre);
						dos.flush();
						c.sendPacket("0", bos.toByteArray(), (byte) 0, new byte[0]);
						break;
					case 4: // Suppression d'un membre
						System.out.println("Nom du groupe?");
						String groupe3 = sc.nextLine();
						dos.writeUTF(groupe3);
						System.out.println("Nom du membre à ajouter?");
						String membre2 = sc.nextLine();
						dos.writeUTF(membre2);
						dos.flush();
						c.sendPacket("0", bos.toByteArray(), (byte) 0, new byte[0]);
						break;
					}
				} else {
					System.out.println("(1) Message? (2) Fichier?");
					int cas = Integer.parseInt(sc.nextLine());
					switch (cas) {
					case 1:
						System.out.println("Votre message ? ");
						lu = sc.nextLine();
						c.sendPacket(dest, lu.getBytes(), (byte) 0, new byte[0]);
						c.stockageBDD(dest.substring(0, 1).equals("@")?dest.substring(1):dest, lu, false);
						break;
					case 2:
						System.out.println("Votre fichier ? ");
						String filePath = "ENVOI/" + sc.nextLine();
						File fichier = new File(filePath);
						byte[] nomFichier = fichier.getName().getBytes();
						byte[] fileContent = c.readFile(filePath);
						c.sendPacket(dest, fileContent, (byte) 1, nomFichier);
						break;
					}
				}
			} catch (InputMismatchException | NumberFormatException e) {
				System.out.println("Mauvais format");
			}

		}

		/*
		 * int id =1+(c.getIdentifier()-1) % 2; System.out.println("send to "+id);
		 * c.sendPacket(id, "bonjour".getBytes());
		 * 
		 * 
		 * Thread.sleep(10000);
		 */

		c.closeSession();

	}
}
