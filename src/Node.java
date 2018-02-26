package rip;
import java.util.*;

public class Node implements Comparable<Node> {
	
	private String name;
	 int counter;

	private HashMap<Node, Float> costToNeighborMap;

	
	private HashMap<Node, Message> messages;
	private boolean newMessages = false;
	private Vector<Message> messageQueue;

	
	private HashMap<String, Float> distanceVector;
	private HashMap<String, String> forwardingTable;
    private HashMap<Node,String>nodeStringNeighborIp;
    private HashMap<Node,String>nodeStringOwnIp;
    private HashMap<String,Node>stringNodeNeighborIp;
    private HashMap<String,Node>stringNodeOwnIp;
    private HashMap<Node,String>neighborSubnet;
    
	public Node(String name) {
		
		this.name = name;
		costToNeighborMap = new HashMap<Node, Float>();
		nodeStringNeighborIp=new HashMap<Node,String>();
		nodeStringOwnIp=new HashMap<Node,String>();
		stringNodeNeighborIp=new HashMap<String,Node>();
		stringNodeOwnIp=new HashMap<String,Node>();
		neighborSubnet=new HashMap<Node,String>();
		messages = new HashMap<Node, Message>();
		newMessages = false;
		messageQueue = new Vector<Message>();
		distanceVector = new HashMap<String, Float>();
		
		forwardingTable = new HashMap<String, String>();
		counter=0;
		
	}

	
	public String toString() {
		return name;
	}

	
	public int compareTo(Node o) {
		return name.compareTo(o.toString());
	}

	
	public void addNeighbor(Node neighbor, String ipSource, String ipDestination, String subnet) throws Exception {
		if ((costToNeighborMap.containsKey(neighbor))) {
			String message = "Error adding neighbor to node" + this + "("
					+ neighbor + ", "+")"
					+ "\nCan't have duplicate links or negative costs";
			throw new Exception(message);
		}

		// Add an entry for the new neighbor in the local data structures
		costToNeighborMap.put(neighbor,(float)1);
                nodeStringNeighborIp.put(neighbor,ipDestination);
                nodeStringOwnIp.put(neighbor,ipSource);
                stringNodeNeighborIp.put(ipDestination,neighbor);
                stringNodeOwnIp.put(ipSource,neighbor);
                neighborSubnet.put(neighbor,subnet); 
		messages.put(neighbor, null);
		if (!distanceVector.containsKey(subnet)) {
			distanceVector.put(subnet, (float)0);
			forwardingTable.put(subnet, ipSource);
		}

		// Send a message to all neighbors with this new cost info
		notifyNeighbors();
	}

	public void changeCostToNeighbor(Node neighbor, float cost)
			throws Exception {
		if (!costToNeighborMap.containsKey(neighbor)) {
			throw new Exception(
					"Trying to change cost to a node that isn't already a neighbor");
		}

		// Change the cost to this neighbor
		costToNeighborMap.put(neighbor, cost);

		// Update the local routing info (distance vector and forwarding table)
		// with the new cost
		doDistanceVectorUpdate();
		clearNewMessagesFlag();

		// Notify neighbors of the change
		notifyNeighbors();
	}

	
	public void sendMessage(Message m) {
		messageQueue.add(m);
		newMessages = true;
	}

	public void deliverMessageQueue() {
		for (int i = 0; i < messageQueue.size(); i++) {
			Message m = messageQueue.get(i);
			messages.put(m.getFrom(), m);
			counter++;
		}
		messageQueue.clear();
		newMessages = true;
	}

	
	public boolean hasNewMessages() {
		return newMessages;
	}

	
	public void clearNewMessagesFlag() {
		newMessages = false;
	}

	
	protected Collection<Node> getNeighbors() {
		return new TreeSet<Node>(costToNeighborMap.keySet());
	}

	
	protected Collection<String> getDestinations() {
		return new TreeSet<String>(distanceVector.keySet());
	}

	
	private float getCostToNeighbor(Node neighbor) {
		if (costToNeighborMap.containsKey(neighbor)) {
			return costToNeighborMap.get(neighbor);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}

	
	private float getCostFromNeighborTo(Node neighbor, String destination) {
		Message m = messages.get(neighbor);
		if (m != null) {
			return m.getCostTo(destination);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}

	
	protected String getNextHopTo(String destination) {
		return forwardingTable.get(destination);
	}

	
	protected float getCostToDestination(String destination) {
		return distanceVector.get(destination);
	}

	private void updateDistanceVector(String destination, float cost)
			throws Exception {
		if (cost > 0) {
			distanceVector.put(destination, cost);
		} else {
			throw new Exception("Costs can't be negative");
		}
	}

	private void updateForwardingTable(String destination, String nextHop)
			throws Exception {
		if ((!costToNeighborMap.containsKey(stringNodeNeighborIp.get(nextHop))) && (stringNodeNeighborIp.get(nextHop) != this)) {
			throw new Exception(
					"Trying to add a forwarding table entry to a node that isn't a neighbor");
		}
		forwardingTable.put(destination, nextHop);
	}

	public void printLatestMessages() {
		System.out.println("Latest messages received by node " + this + ":");
		System.out.println("      \t| Neighbors");
		System.out.print("Dest. \t|");
		Collection<Node> neighbors = getNeighbors();
		for (Node n : neighbors) {
			System.out.print("\t" + n);
		}
		System.out.print("\n");
		System.out.print("---------------");
		for (int i = 0; i < neighbors.size(); i++) {
			System.out.print("--------");
		}
		System.out.print("\n");
		for (String dest : getDestinations()) {
			System.out.print(dest + "|");
			for (Node n : neighbors) {
				String costFromNeighborTo = "";
				if (getCostFromNeighborTo(n, dest) == Float.POSITIVE_INFINITY) {
					costFromNeighborTo = "Inf";
				} else {
					
					costFromNeighborTo = Integer
							.toString((int) getCostFromNeighborTo(n, dest));
				}
				System.out.print("\t" + costFromNeighborTo);
			}
			System.out.print("\n");
		}
		System.out.println(" ");
	}

	public void printDistanceVector() {
		System.out.println("Distance vector and forwarding table for node "
				+ this + ":");
		System.out.println("Dest.\tCost (Next Hop)");
		System.out.println("-------------------------");
		for (String dest : getDestinations()) {
			String costToDestination = "";
			if (getCostToDestination(dest) == Float.POSITIVE_INFINITY) {
				costToDestination = "Inf";
			} else {
				costToDestination = Integer
						.toString((int) getCostToDestination(dest));
			}
			System.out.println(dest + "  " + costToDestination + " ("
					+ getNextHopTo(dest) + ")");
		}
		System.out.println("");
	}

        protected Collection<Message> getMessages() {
		return new TreeSet<Message>(messages.values());
	}

	/**
	 * Implements the Bellman-Ford equation to update the distanceVector costs
	 * and forwardingTable of this node
	 */
	public void doDistanceVectorUpdate() {
		
		boolean somethingChanged = false;
                for (Message message:getMessages()) {
                        for (String subnet:message.getTable()) {
                                if(!distanceVector.containsKey(subnet)) {
                                        distanceVector.put(subnet,message.getCostMap(subnet)+1);
                                        forwardingTable.put(subnet,nodeStringNeighborIp.get(message.getFrom()));
                                        somethingChanged=true;
                                }  else {
                                        if(distanceVector.get(subnet)>(message.getCostMap(subnet)+1)) {
                                                 distanceVector.put(subnet,message.getCostMap(subnet)+1);
                                                 forwardingTable.put(subnet,nodeStringNeighborIp.get(message.getFrom()));
                                                 somethingChanged=true;
                                        }
                                }
                         }
                }
   
                		// If something changed, notifies this node's neighbors.
		if (somethingChanged) {

			this.notifyNeighbors();
			somethingChanged = false;
		}
		messages = new HashMap<Node, Message>();
		
	}

	
	protected void notifyNeighbors() {
		

		HashMap<String, Float> vector = new HashMap<String, Float>();

		// Gets the node's distance vector.
		for (String destination : getDestinations())
			vector.put(destination, getCostToDestination(destination));

		// (Not doing poisoned reverse in this implementation)

		// Compiles the node's distance vector.
		Message message = new Message(this, vector);

		// Send the message to every neighbor.
		for (Node neighbor : getNeighbors())
			neighbor.sendMessage(message);
	}

	
	public boolean equals(Node o) {
		return name.compareTo(o.toString()) == 0;
	}
}
