WiDiSi is a single-threaded simulator; this means that all its internal components synchronize around Peersim cycles. Android applications, on the other hand, can be multi-threaded. To solve this discrepancy one can decide to use PeerSim in a hybrid mode, i.e., one in which both its cycle-driven and event-driven engines are used.
In the following with some example, we explain how to solve the multi-threaded problem. 
Our aim here is to do all the processes inside one thread that is synched with the PeerSim cycle. It is trivial that this technique cannot be applied to all multi-threaded applications. In the end, one may find out that converting his/ her multi-threaded application to a single-threaded application is not feasible at all. 
Assume we have a runnable in Android application like below:

delayHandler.postDelayed(new Runnable() {
	public void run() {
		if(condition){
		//the code
		}
	}
}, 2000;

In this code, a new thread would be created in parallel with the main thread and after 2000 ms "the code" will be executed. In order to remove this parallel thread in Peersim we use flags and counter at nextCycle() method in the main activity. For this reason we define a condition at the nextCycle() method like this:

long counter = 0;
boolean flag = false;
public void nextCycle(Node node, int pid)
{
........
	if(flag && counter >= (2000/CycleLength){
		//the code
		counter = 0;
	}else if(flag && counter < (2000/CycleLength)){
		counter++;
	}
........
cycle++;
}
Now whenever we are interested in starting the runnable, we only need to make flag=true. This will make the counter counts for (2000/CycleLength) cycles that would be equal to 2000 ms and then execute the code.

