package routing_simulator;


import java.io.File;


public class Test {

	
	public static void main(String[] args) {
		// Check for the right number of arguments
		if (args.length != 1) {
			System.out.println("Invalid File");
			System.exit(1);
		}
		
		try {
			// Load the network from file
			File networkFile = new File(args[0]);
			Network network = new Network(networkFile);
			
			// Print initial distance vectors and forwarding tables
			System.out.println("===========================================================");
			System.out.println("T=0 (Before any messages are exchanged)");
			network.printRoutingInfo();
			
			// Repeat doing distance vector updates until no more messages are passed
			for (int t=1; network.hasNewMessages(); t++) {
				System.out.println("===========================================================");
				System.out.println("T=" + t);
				network.deliverMessages();
				network.doDistanceVectorUpdates();
				
			}
			System.out.println("The number of messages exchanged is "+network.messageCnt());
			RipThread t1=new RipThread(network);
			//Thread thread=new Thread(t1,"T2");
			//thread.start();
			//Thread thread1=new Thread(t1,"T1");
			//thread1.start();
			Thread thread2=new Thread(t1,"T3");
			thread2.start();
		} catch (Exception e) {
			System.out.println("Error loading the network from file: " + args[0]);
			e.printStackTrace();
		}
		
		
	}

}
