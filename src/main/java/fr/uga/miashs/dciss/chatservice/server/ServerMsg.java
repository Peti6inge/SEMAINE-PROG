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

package fr.uga.miashs.dciss.chatservice.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.util.*;

public class ServerMsg {

	private final static Logger LOG = Logger.getLogger(ServerMsg.class.getName());
	public final static int SERVER_CLIENTID = 0;

	private transient ServerSocket serverSock;
	private transient boolean started;
	private transient ExecutorService executor;
	private transient ServerPacketProcessor sp;

	// maps pour associer les id aux users, passwords et groupes
	private Map<String, UserMsg> users;
	private Map<String, String> passwords;
	private Map<String, GroupMsg> groups;


	public ServerMsg(int port) throws IOException {
		serverSock = new ServerSocket(port);
		started = false;
		users = new ConcurrentHashMap<>();
		groups = new ConcurrentHashMap<>();
		sp = new ServerPacketProcessor(this);
		executor = Executors.newCachedThreadPool();
		passwords = new HashMap<>();
	}

	public GroupMsg createGroup(String ownerName, String groupeName) {
		UserMsg owner = users.get(ownerName);
		if (owner == null)
			throw new ServerException("User with id=" + ownerName + " unknown. Group creation failed.");
		GroupMsg res = new GroupMsg(groupeName, owner);
		groups.put(ownerName, res);
		LOG.info("Group " + res.getGroupname() + " created");
		return res;
	}

	public boolean removeGroup(String userName, String groupName) {
		GroupMsg g = groups.get(groupName);
		if (g == null || userName != g.getOwnerName()) {
			LOG.info("Deleting Group " + groupName + " is forbiden for " + userName);
			return false;
		}
		g = groups.remove(groupName);
		g.beforeDelete();
		LOG.info("Group " + groupName + " deleted");
		return true;
	}

	public boolean AddUsertoGroup(String userName, String groupName, String userToAddName) {
		GroupMsg g = groups.get(groupName);
		if (g == null || userName != g.getOwnerName() || g.getMembers().contains(users.get(userToAddName))) {
			LOG.info("Adding user " + userToAddName + " is impossible");
			return false;
		}
		g.addMember(users.get(userToAddName));
		return true;
	}

	public boolean RemoveUsertoGroup(String userName, String groupName, String userToRemoveName) {
		GroupMsg g = groups.get(groupName);
		if (g == null || userName != g.getOwnerName() || !g.getMembers().contains(users.get(userToRemoveName))) {
			LOG.info("Removing user " + userToRemoveName + " is impossible");
			return false;
		}
		g.removeMember(users.get(userToRemoveName));
		return true;
	}

	public boolean removeUser(String userName) {
		UserMsg u = users.remove(userName);
		if (u == null)
			return false;
		u.beforeDelete();
		return true;
	}

	public UserMsg getUser(String userName) {
		return users.get(userName);
	}

	// Methode utilisée pour savoir quoi faire d'un paquet
	// reçu par le serveur
	public void processPacket(Packet p) {
		PacketProcessor pp = null;
		if (p.destUsername.substring(0,1).equals("@")) { // message de groupe
			// can be send only if sender is member
			UserMsg sender = users.get(p.srcUsername);
			System.out.println(p.srcUsername + p.destUsername);
			GroupMsg g = groups.get(p.destUsername.substring(1));
			if (g.getMembers().contains(sender))
				pp = g;
			
				
		} else if (!(p.destUsername.equals("0"))) { // message entre utilisateurs
			pp = users.get(p.destUsername);
		} else { // message de gestion pour le serveur
			pp = sp;
		}

		if (pp != null) {
			pp.process(p);
		}
	}

	public void start() {
		started = true;
		while (started) {
			try {
				// le serveur attend une connexion d'un client
				Socket s = serverSock.accept();

				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				// lit l'identifiant et le mot de passe du client
				String userName = dis.readUTF();
				String password = dis.readUTF();
				if (!users.containsKey(userName)) {
					// si inexistant alors il faut créer un nouvel utilisateur
					//Prévenir avec une notif + flush?
					users.put(userName, new UserMsg(userName, this));
					passwords.put(userName, password);
				}

				if (!(passwords.get(userName).equals(password))) {
					// Si l'Id est inconnu ou s'il ne correspond pas au mot de passe saisi
					LOG.info("Bad password");
					s.close();
					continue;
				}

				// si l'identifiant existe ou est nouveau alors
				// deux "taches"/boucles sont lancées en parralèle
				// une pour recevoir les messages du client,
				// une pour envoyer des messages au client
				// les deux boucles sont gérées au niveau de la classe UserMsg
				UserMsg x = users.get(userName);
				if (x != null && x.open(s)) {
					LOG.info(userName + " connected");
					// lancement boucle de reception
					executor.submit(() -> x.receiveLoop());
					// lancement boucle d'envoi
					executor.submit(() -> x.sendLoop());
				} else { // si l'idenfiant est inconnu, on ferme la connexion
					s.close();
				}

			} catch (IOException e) {
				LOG.info("Close server");
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		started = false;
		try {
			serverSock.close();
			users.values().forEach(s -> s.close());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		ServerMsg s = new ServerMsg(1666);
		s.start();
	}

}
