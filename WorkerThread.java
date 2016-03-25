import java.util.Queue;

/**
 * 
 * This class behaves like a coordinator to maintain a queue for a specific task,
 * for example task for processing packets.
 *
 */
public class WorkerThread extends {
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
