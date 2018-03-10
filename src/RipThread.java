package rip;

public class RipThread implements Runnable {

	Network network;
	public RipThread(Network network)
	{
		this.network=network;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;
		while(true)
		{
			//System.out.print(Thread.currentThread().getName());
			if(Thread.currentThread().getName().equals("T2")) {
				//System.out.println("a");
				network.doTimerLinkDeletion();
				network.doNotifyNeighbors();
				network.deliverMessages();
				network.doDistanceVectorUpdates();
				network.getNode("C").printDistanceVector();
				network.getNode("A").printDistanceVector();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if(Thread.currentThread().getName().equals("T1"))
			{
				if(i==0)
					{
					try{
					
					network.linkDeletion("A","B");
					i=1;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			}

			
		}
	}
	

}
