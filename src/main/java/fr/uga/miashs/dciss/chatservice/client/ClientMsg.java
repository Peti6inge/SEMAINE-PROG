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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.uga.miashs.dciss.chatservice.common.Packet;

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

	private int identifier;

	private List<MessageListener> mListeners;
	private List<ConnectionListener> cListeners;

	/**
	 * Create a client with an existing id, that will connect to the server at the
	 * given address and port
	 * 
	 * @param id      The client id
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(int id, String address, int port) {
		if (id < 0)
			throw new IllegalArgumentException("id must not be less than 0");
		if (port <= 0)
			throw new IllegalArgumentException("Server port must be greater than 0");
		serverAddress = address;
		serverPort = port;
		identifier = id;
		mListeners = new ArrayList<>();
		cListeners = new ArrayList<>();
	}

	/**
	 * Create a client without id, the server will provide an id during the session
	 * start
	 * 
	 * @param address The server address or hostname
	 * @param port    The port number
	 */
	public ClientMsg(String address, int port) {
		this(0, address, port);
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

	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Method to be called to establish the connection.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void startSession(String password) throws UnknownHostException {
		if (s == null || s.isClosed()) {
			try {
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeInt(identifier);
	            dos.writeUTF(password);
				dos.flush();
				if (identifier == 0) {
					identifier = dis.readInt();
				}
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
	public void sendPacket(int destId, byte[] data) {
		try {
			synchronized (dos) {
				dos.writeInt(destId);
				dos.writeInt(data.length);
				dos.write(data);
				dos.flush();
			}
		} catch (IOException e) {
			// error, connection closed
			closeSession();
		}

	}

	/**
	 * Start the receive loop. Has to be called only once.
	 */
	private void receiveLoop() {
		try {
			while (s != null && !s.isClosed()) {

				int sender = dis.readInt();
				int dest = dis.readInt();
				int length = dis.readInt();
				byte[] data = new byte[length];
				dis.readFully(data);
				notifyMessageListeners(new Packet(sender, dest, data));

			}
		} catch (IOException e) {
			// error, connection closed
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

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

		// Authentification

		int id = 0;
		String password;
		boolean creation = false;
		Scanner sc = new Scanner(System.in);

		if (args.length >= 2) {
			System.out.println("2 args");
			id = Integer.parseInt(args[0]);
			password = args[1];
		} else {
			System.out.println("Voulez-vous créer un compte? (o/n)");
			creation = sc.nextLine().equals("o") ? true : false;
			if (creation) {
				// Création d'un compte
				System.out.println("Saisissez votre mot de passe :");
				password = sc.nextLine();
			} else {
				// Connexion
				System.out.println("Saisissez votre identifiant :");
				id = Integer.parseInt(sc.nextLine());
				System.out.println("Saisissez votre mot de passe :");
				password = sc.nextLine();
			}
		}
		ClientMsg c;
		if (creation) {
			c = new ClientMsg("localhost", 1666);
		} else {
			c = new ClientMsg(id, "localhost", 1666);
		}

		// add a dummy listener that print the content of message as a string
		c.addMessageListener(p -> System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data)));

		// add a connection listener that exit application when connection closed
		c.addConnectionListener(active -> {
			if (!active)
				System.exit(0);
		});

		c.startSession(password);
		System.out.println("Vous êtes : " + c.getIdentifier());

		String lu = null;
		while (!"\\quit".equals(lu)) {
			try {
				System.out.println("A qui voulez vous écrire ? ");
				String dest = sc.nextLine();
				if (dest.equals("x")) {
					System.out.println("Deconnexion");
					c.closeSession();	
				}
				int destToInt = Integer.parseInt(dest);
				
				if (destToInt == 0) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(bos);
					// Création de groupe
					System.out.println("Que voulez-vous faire? (1 : création de groupe, 2 : Suppression de groupe, 3 : Ajout d'un membre dans un groupe, 4 : Suppression d'un membre dans un groupe");
					int choix = Integer.parseInt(sc.nextLine());
					dos.writeByte(choix);
					switch (choix) {
					case 1 : //création de groupe
						System.out.println("Id des membres ? (séparé par des virgules)");
						lu = sc.nextLine();
						String[] parts = lu.split(","); // Diviser la chaîne en sous-chaînes en utilisant la virgule
														// comme délimiteur
	
						int[] numbers = new int[parts.length]; // Créer un tableau pour stocker les entiers
	
						for (int i = 0; i < parts.length || i == 19; i++) {
							numbers[i] = Integer.parseInt(parts[i].trim()); // Convertir chaque sous-chaîne en entier //
																			// stocker dans le tableau
						}
						dos.writeInt(numbers.length);
						for (int num : numbers) {
							dos.writeInt(num);
						}
						dos.flush();
						c.sendPacket(0, bos.toByteArray());
						break;
					case 2 : //suppression de groupe
						System.out.println("Id du groupe?");
						int idGroupe = Integer.parseInt(sc.nextLine());
						dos.writeInt(idGroupe);
						dos.flush();
						c.sendPacket(0, bos.toByteArray());
						break;
					case 3 : //Ajout d'un nouveau membre
						System.out.println("Id du groupe?");
						int idGroupe2 = Integer.parseInt(sc.nextLine());
						dos.writeInt(idGroupe2);
						System.out.println("Id du membre à ajouter?");
						int idMembre = Integer.parseInt(sc.nextLine());
						dos.writeInt(idMembre);
						dos.flush();
						c.sendPacket(0, bos.toByteArray());
						break;
					case 4 : //Suppression d'un membre
						System.out.println("Id du groupe?");
						int idGroupe3 = Integer.parseInt(sc.nextLine());
						dos.writeInt(idGroupe3);
						System.out.println("Id du membre à supprimer?");
						int idMembre2 = Integer.parseInt(sc.nextLine());
						dos.writeInt(idMembre2);
						dos.flush();
						c.sendPacket(0, bos.toByteArray());
						break;
					}
				} else {
					System.out.println("Votre message ? ");
					lu = sc.nextLine();
					c.sendPacket(destToInt, lu.getBytes());
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
