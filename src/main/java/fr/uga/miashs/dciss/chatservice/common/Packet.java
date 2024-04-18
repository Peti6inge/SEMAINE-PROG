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

package fr.uga.miashs.dciss.chatservice.common;

/*
 * Data structure to represent a packet
 */
public class Packet {
	
	public final static Packet POISON=new Packet(0,(byte)0,0, new byte[0], new byte[0]);

	public final int srcId;
	public final int destId;
	public final byte fichier;
	public final byte[] data;
	public final byte[] nomFichier;
	
	public Packet(int srcId, byte fichier, int destId,  byte[] data, byte[] nomFichier) {
		super();
		this.srcId = srcId;
		this.destId = destId;
		this.data = data;
		this.fichier = fichier;
		this.nomFichier = nomFichier;
	}
	
}
