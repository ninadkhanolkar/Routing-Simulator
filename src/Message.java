package routing_simulator;

import java.util.*;


public class Message implements Comparable<Message> {
	private Node from;
	private String  name;
	private HashMap<String,Float> costMap;

	
	public Message(Node from, Map<String,Float> costs) {
		this.from = from;
		this.costMap = new HashMap<String,Float>(costs);
		name=from.toString();
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(Message o) {
		return name.compareTo(o.toString());
	}
	
	
	public Node getFrom() {
		return from;
	}
	
	public Float getCostMap(String s) {
		return costMap.get(s);
	}

        public Collection<String> getTable() {
		return new TreeSet<String>(costMap.keySet());
	}
	
	
	public float getCostTo(String destination) {
		if (costMap.containsKey(destination)) {
			return costMap.get(destination);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}
}
