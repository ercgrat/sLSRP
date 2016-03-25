import java.util.Queue;


public class WorkerThread extends Thread {
    private final Queue<Runnable> taskQueue;
    public WorkerThread(Queue queue) {
        this.taskQueue = queue;
    }
     
    @Override
    public void run() {
    	while (true) {
            try {
                Runnable task = null;
                synchronized (taskQueue) {
                    while ( taskQueue.isEmpty() )
                    	taskQueue.wait();
                     
                    // Get the next task
                    task = taskQueue.remove();
                }
                //Call the task to start
                task.run();
            }
            catch (InterruptedException ie) {
            	ie.printStackTrace();
                break;
            }
        }
    }
}
