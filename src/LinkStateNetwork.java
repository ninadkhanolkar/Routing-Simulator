package routing_simulator;

import java.io.*;
import java.util.*;


public class LinkStateNetwork {
	protected HashMap<String, LinkStateNode> nameToNodeMap;
	int cnt;
	int numOfNodes;

	
	public LinkStateNetwork(File file, int n) throws Exception {
		// Initialize the nodeToNameMap
		nameToNodeMap = new HashMap<String, LinkStateNode>();
		numOfNodes=n;

		loadNetworkFromFile(file);
		cnt=0;
	}

	// loading netwrok for linkstate
		protected void loadNetworkFromFile(File file) throws Exception {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			LinkStateNode.initialisation(numOfNodes);
			String nextLine = null;
			StringTokenizer tokenizer;
			String nodeName1;
			String nodeName2;
	                String ipSource;
	                String ipDestination;
	                String subnet;
			float cost;
			LinkStateNode node1;
			LinkStateNode node2;
			int edgeCount = 0;
			int indexVal=0;

			// Loop over lines reading
			for (int lineNumber = 1; (nextLine = reader.readLine()) != null; lineNumber++) {
				// Skip empty lines and lines that start with #
				if (nextLine.equals("") || nextLine.startsWith("#")) {
					continue;
				}

				// Tokenize the string based on spaces
				tokenizer = new StringTokenizer(nextLine);

				// Make sure there are three tokens (StartNode, EndNode, Cost)
				if (tokenizer.countTokens() != 6) {
					throw new Exception("Improperly formatted input at line "
							+ lineNumber);
				}

				nodeName1 = tokenizer.nextToken();
				nodeName2 = tokenizer.nextToken();
	            ipSource=tokenizer.nextToken();
	            ipDestination=tokenizer.nextToken();
	            subnet=tokenizer.nextToken();
	        	cost = Float.parseFloat(tokenizer.nextToken());

	                        

				// Add nodeName1 to the Network if necessary
				if (!nameToNodeMap.containsKey(nodeName1)) {
					node1 = new LinkStateNode(nodeName1);
					nameToNodeMap.put(nodeName1, node1);
				} else {
					node1 = nameToNodeMap.get(nodeName1);
				}

				// Add nodeName2 to the Network if necessary
				if (!nameToNodeMap.containsKey(nodeName2)) {
					node2 = new LinkStateNode(nodeName2);
					nameToNodeMap.put(nodeName2, node2);
				} else {
					node2 = nameToNodeMap.get(nodeName2);
				}
				
				if(!LinkStateNode.routerToIndex.containsKey(node1)) {
					LinkStateNode.routerToIndex.put(node1,indexVal);
					LinkStateNode.indexToRouter.put(indexVal,node1);
					indexVal++;
				}
				
				if(!LinkStateNode.routerToIndex.containsKey(node2)) {
					LinkStateNode.routerToIndex.put(node2,indexVal);
					LinkStateNode.indexToRouter.put(indexVal,node2);
					indexVal++;
				}
				int indexNode1=LinkStateNode.routerToIndex.get(node1);
				int indexNode2=LinkStateNode.routerToIndex.get(node2);

				LinkStateNode.topology[indexNode1][indexNode2]=cost;
				LinkStateNode.topology[indexNode2][indexNode1]=cost;


					
					
				

				node1.addNeighbor(node2, ipSource, ipDestination, subnet, cost);
				node2.addNeighbor(node1, ipDestination, ipSource, subnet, cost);
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
		
		public Collection<LinkStateNode> getNodes() {
			Collection<LinkStateNode> nodes = new TreeSet<LinkStateNode>(nameToNodeMap.values());
			return nodes;
		}
		

		public void doDistanceVectorUpdates() {
			int i,j;
			for(i=0;i<numOfNodes;i++) {
				for(j=0;j<numOfNodes;j++) {
					System.out.print(LinkStateNode.topology[i][j]+"  ");
				}
				System.out.println();
		}
			for (LinkStateNode n : getNodes()) {
				n.initialisationNonStatic(numOfNodes);
				n.doRoutingTableUpdate();
			}
			
		}

		
}

