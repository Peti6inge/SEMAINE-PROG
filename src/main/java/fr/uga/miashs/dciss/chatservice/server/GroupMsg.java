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

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import fr.uga.miashs.dciss.chatservice.common.Packet;

public class GroupMsg implements PacketProcessor {

	private String groupname;
	private UserMsg owner;
	private Set<UserMsg> members;
	
	public GroupMsg(String groupname, UserMsg owner) {
		if (owner==null) throw new IllegalArgumentException("owner cannot be null");
		this.groupname=groupname;
		this.owner=owner;
		members=Collections.synchronizedSet(new HashSet<>());
		addMember(owner);
	}
	
	public String getGroupname() {
		return groupname;
	}
	
	public String getOwnerName() {
		return owner.getName();
	}
		
	/**
	 * This method has to be used to add a member to the group.
	 * It update the bidirectional relationship, i.e. the user is added to the group and the the group is added to the user.
	 * @param s
	 * @return
	 */
	public boolean addMember(UserMsg s) {
		return s!=null && members.add(s) && s.getGroups().add(this);
	}
	
	/**
	 * This method has to be used to remove a member from the group.
	 * It update the bidirectional relationship, i.e. the user is removed from the group and the the group is removed from the user.
	 * @param s
	 * @return
	 */
	public boolean removeMember(UserMsg s) {
		if (s.equals(owner)) return false;
		if (members.remove(s)) {
			s.removeGroup(this);
			return true;
		}
		return false;
	}
	
	@Override
	public void process(Packet p) {
		// send packet to members except the sender.
		members.stream().filter(m->m.getName()!=p.srcUsername).forEach( m -> m.process(p));
	}
	
	// to be used carrefully, because it does not update birectional relationship in case of addition or removal.
	public Set<UserMsg> getMembers() {
		return members;
	}
	
	/*
	 * This method has to be called when removing a group in order to clean bidirectional membership.
	 */
	public void beforeDelete() {
		members.forEach(m->m.getGroups().remove(this));
	}

}
