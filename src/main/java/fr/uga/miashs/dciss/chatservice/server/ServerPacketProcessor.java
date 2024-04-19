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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import fr.uga.miashs.dciss.chatservice.common.Packet;

public class ServerPacketProcessor implements PacketProcessor {
	private final static Logger LOG = Logger.getLogger(ServerPacketProcessor.class.getName());
	private ServerMsg server;

	public ServerPacketProcessor(ServerMsg s) {
		this.server = s;
	}

	@Override
	public void process(Packet p) {
		// ByteBufferVersion. On aurait pu utiliser un ByteArrayInputStream + DataInputStream à la place
		ByteBuffer buf = ByteBuffer.wrap(p.data);
		byte type = buf.get();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf.array());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		switch (type) {
		case 1 :  // Création d'un groupe
			createGroup(p.srcUsername,buf);
			break;
		case 2 : // Suppression d'un groupe
			try {
				server.removeGroup(p.srcUsername, dataInputStream.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 3 : // Ajout d'un membre
			
			try {
				server.AddUsertoGroup(p.srcUsername, dataInputStream.readUTF(), dataInputStream.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 4 : // Suppression d'un membre
			try {
				server.RemoveUsertoGroup(p.srcUsername, dataInputStream.readUTF(), dataInputStream.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default : 
			LOG.warning("Server message of type=" + type + " not handled by procesor");

		}
		try {
			dataInputStream.close();
	        byteArrayInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void createGroup(String ownerName, ByteBuffer data) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        int nb;
		try {
            String groupeName = dataInputStream.readUTF();
            nb = dataInputStream.readInt();
			GroupMsg g = server.createGroup(ownerName, groupeName);
			for (int i = 0; i < nb; i++) {
	            String str = dataInputStream.readUTF();
	            g.addMember(server.getUser(str));
	        }
			dataInputStream.close();
            byteArrayInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
