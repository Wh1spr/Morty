package wh1spr.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Team {

	/**
	 * Returns a Team instance with the given name and leader. The members list will contain only the leader.
	 * @param name The name of the team. If no name is given, it will be called "Team ‹leadername›"
	 * @param leader The Person instance of the leader. If no leader was given, Person.NOONE will take its place.
	 * @throws IllegalArgumentException When both name and leader are null, a team cannot be instantiated.
	 */
	public Team(String name, Person leader) {
		if (name == null && leader == null) throw new IllegalArgumentException("A Team cannot have no name and no leader.");
		if (leader == null) leader = Person.NOONE;
		this.name = name;
		this.leader = leader;
		this.members = new ArrayList<Person>();
		this.roster = new ArrayList<Person>();
		this.members.add(leader);
	}
	
	private String name;
	public String getName() {
		return this.name;
	}
	public Team setName(String newName) {
		if (newName == null && this.getLeader() == Person.NOONE) throw new IllegalArgumentException("A Team cannot have no name and no leader.");
		this.name = newName;
		return this;
	}
	
	private Person leader;
	public Person getLeader() {
		if (this.leader == null) return Person.NOONE;
		return this.leader;
	}
	public Team setLeader(Person newLeader) {
		if ((newLeader == null || newLeader == Person.NOONE) && this.getName() == null) throw new IllegalArgumentException("A Team cannot have no name and no leader.");
		this.leader = newLeader;
		return this;
	}
	
	private Person coach = null;
	public Person getCoach() {
		if (coach == null) return Person.NOONE;
		return this.coach;
	}
	public Team setCoach(Person newCoach) {
		this.coach = newCoach;
		return this;
	}
	
	private List<Person> members = null;
	public List<Person> getMembers() {
		return Collections.unmodifiableList(members);
	}
	public Team addMember(Person p) {
		members.add(p);
		return this;
	}
	public Team addMembers(Person... p) {
		members.addAll(Arrays.asList(p));
		return this;
	}
	public Team removeMember(Person p) {
		if (p == Person.NOONE) return this;
		members.remove(p);
		return this;
	}
	public int getSize() {
		return members.size();
	}
	
	private int gamesize = -1;
	/**
	 * @return GameSize, the number of members active in one game.
	 */
	public int getGameSize() {
		return this.gamesize;
	}
	/**
	 * @param size The new gamesize. -1 stands for unlimited users.
	 */
	public Team setGameSize(int size) {
		if (size < 0) throw new IllegalArgumentException("Gamesize has to greater or equal to -1.");
		this.gamesize = size;
		return this;
	}
	
	private List<Person> roster = null;
	public List<Person> getRoster() {
		return Collections.unmodifiableList(roster);
	}
	public Team addToRoster(Person p) {
		if (roster.size() >= getGameSize() && getGameSize() != -1) throw new IllegalArgumentException("Reached roster limit due to GameSize. Remove someone first.");
		roster.add(p);
//		if () //check if in member list and shizzle
		return this;
	}
	
	
}
