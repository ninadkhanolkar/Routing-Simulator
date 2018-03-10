package rip;



import java.io.*;
import java.util.*;


public class Network {
	// Data structure to store the nodes in this network, indexed by name
	protected HashMap<String, Node> nameToNodeMap;
	int cnt;

	
	public Network(File file) throws Exception {
		// Initialize the nodeToNameMap
		nameToNodeMap = new HashMap<String, Node>();
		
		loadNetworkFromFile(file);
		cnt=0;
	}
	
	protected void linkDeletion(String linkDel1, String linkDel2) throws Exception {
		float cost;
		Node nodeDel1= getNode(linkDel1);
		Node nodeDel2= getNode(linkDel2);
		cost=nodeDel1.getCostToNeighbor(nodeDel2);
		nodeDel1.changeCostToNeighbor(nodeDel2,cost+1);
		cost=nodeDel2.getCostToNeighbor(nodeDel1);
		nodeDel2.changeCostToNeighbor(nodeDel1,cost+1);
		
	}

	protected void loadNetworkFromFile(File file) throws Exception {
		// Declare and initialize IO variables used read the network from file
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String nextLine = null;
		StringTokenizer tokenizer;

		String nodeName1;
		String nodeName2;
                String ipSource;
                String ipDestination;
                String subnet;
		float cost;
		Node node1;
		Node node2;
		int edgeCount = 0;

		// Loop over lines reading
		for (int lineNumber = 1; (nextLine = reader.readLine()) != null; lineNumber++) {
			// Skip empty lines and lines that start with #
			if (nextLine.equals("") || nextLine.startsWith("#")) {
				continue;
			}

			// Tokenize the string based on spaces
			tokenizer = new StringTokenizer(nextLine);

			// Make sure there are three tokens (StartNode, EndNode, Cost)
			if (tokenizer.countTokens() != 5) {
				throw new Exception("Improperly formatted input at line "
						+ lineNumber);
			}

			nodeName1 = tokenizer.nextToken();
			nodeName2 = tokenizer.nextToken();
			//cost = Float.parseFloat(tokenizer.nextToken());
                        ipSource=tokenizer.nextToken();
                        ipDestination=tokenizer.nextToken();
                        subnet=tokenizer.nextToken();

			// Add nodeName1 to the Network if necessary
			if (!nameToNodeMap.containsKey(nodeName1)) {
				node1 = new Node(nodeName1);
				nameToNodeMap.put(nodeName1, node1);
			} else {
				node1 = nameToNodeMap.get(nodeName1);
			}

			// Add nodeName2 to the Network if necessary
			if (!nameToNodeMap.containsKey(nodeName2)) {
				node2 = new Node(nodeName2);
				nameToNodeMap.put(nodeName2, node2);
			} else {
				node2 = nameToNodeMap.get(nodeName2);
			}

			node1.addNeighbor(node2, ipSource, ipDestination, subnet);
			node2.addNeighbor(node1, ipDestination, ipSource, subnet);
			edgeCount++;
		}
		reader.close();
		// Make sure each node has the full destination set
		/*for (Node n : getNodes()) {
			n.updateDestinations(getNodes());
		}*/

		System.out.println("Successfully loaded network from " + file);
		System.out.println(nameToNodeMap.size() + " nodes, and " + edgeCount
				+ " edges");
		System.out.println("");
	}
	
	/**
	 * @return a Collection of the Nodes in this network
	 */
	public Collection<Node> getNodes() {
		Collection<Node> nodes = new TreeSet<Node>(nameToNodeMap.values());
		return nodes;
	}
	
	/**
	 * Get a node by name
	 * @param name is the name of the node we're asking for
	 * @return the node with specified name 
	 */
	public Node getNode(String name) {
		return nameToNodeMap.get(name);
	}
	
	/**
	 * Print the distance vector and latest messages received by each node
	 */
	public void printRoutingInfo() {
		for (Node n : getNodes()) {
			System.out.println("-----------------------------------------------------------");
			System.out.println("");
			
			n.printLatestMessages();
			n.printDistanceVector();
		}
	}

	/**
	 * Iterate over all nodes and check if any has received a new message from a
	 * neighbor
	 * 
	 * @return true if any node has a new message, false otherwise
	 */
	public boolean hasNewMessages() {
		for (Node node : getNodes()) {
			if (node.hasNewMessages()) {
				return true;
			}
		}
		return false;
	}
	
	
	public void deliverMessages() {
		for (Node node : getNodes()) {
			node.deliverMessageQueue();
		}
	}
	
	public void doNotifyNeighbors() {
		System.out.println("iter");
		for (Node node: getNodes()) {
			node.notifyNeighbors();
		}
	}
	
	public void doTimerLinkDeletion() {
		for (Node node:getNodes()) {
			node.timerLinkDeletion();
		}
	}

	public int messageCnt() {
		for (Node n:getNodes()) {
			cnt=cnt+n.counter;
		}
		return cnt;
	}
	
	public void doDistanceVectorUpdates() {
		for (Node node : getNodes()) {
			//node.printLatestMessages();
			System.out.println("-----------------------------------------------------------");
			if (node.hasNewMessages()) {
				System.out.println("Updating node " + node);
				node.doDistanceVectorUpdate();
				node.clearNewMessagesFlag();
			} else {
				System.out.println("Information for node " + node + "  (no update)");
			}
			System.out.println("");
			
			
			//node.printDistanceVector();
		}
	}
}
