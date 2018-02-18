/**
 * ECSE 414 - Homework Assignment 4, Problem 4
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgillca
 * 24 October 2009
 */

import java.util.*;

/**
 * A simple class to encapsulate the information transmitted from one node to
 * one of its neighbors in a distance vector routing algorithm implementation.
 * 
 * @author michaelrabbat
 * 
 */
public class Message {
	private Node from;
	private HashMap<String,Float> costMap;

	/**
	 * Create a new distance vector Message
	 * 
	 * @param from
	 *            is the node sending the message
	 * @param costs
	 *            is a Map with the costs to all destination nodes to be
	 *            included in this message
	 */
	public Message(Node from, Map<String,Float> costs) {
		this.from = from;
		this.costMap = new HashMap<String,Float>(costs);
	}
	
	/**
	 * @return the Node that sent this message
	 */
	public Node getFrom() {
		return from;
	}

        public Collection<String> getTable() {
		return new TreeSet<String>(costMap.keySet());
	}
	
	/**
	 * @param destination is the Node we're interested in knowing the cost to
	 * @return the advertised cost from the sender of this message to the specified destination
	 */
	public float getCostTo(String destination) {
		if (costMap.containsKey(destination)) {
			return costMap.get(destination);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}
}
