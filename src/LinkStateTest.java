package routing_simulator;

import java.io.File;

public class LinkStateTest {
	

	public static void main(String[] args) {
		// Check for the right number of arguments
		if (args.length != 1) {
			System.out.println("Invalid File");
			System.exit(1);
		}
		
		try {
			// Load the network from file
			File networkFile = new File(args[0]);
			LinkStateNetwork network = new LinkStateNetwork(networkFile,10);
			network.doDistanceVectorUpdates();
			
		}
		 catch (Exception e) {
				System.out.println("Error loading the network from file: " + args[0]);
				e.printStackTrace();
			}

}
	}
