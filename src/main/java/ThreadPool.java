import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool implements Executor {
    private Integer maxThreadCount = 0;
    private Integer currentThreadCount = 0;
    private Queue<Runnable> tasks;

    public ThreadPool(Integer maxThreadsCount) {
        this.maxThreadCount = maxThreadsCount;
        tasks = new LinkedBlockingQueue<>();
    }

    @Override
    public void execute(Runnable executor) {
        synchronized (this) {
            if (!tasks.offer(executor)) {
                System.out.println("Problem with tasks.offer(executor)");
            }
            if (currentThreadCount < maxThreadCount) {
                new Thread(new Worker()).start();
                currentThreadCount++;
            }
        }
    }

    private final class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                final Runnable task;
                synchronized (this) {
                    task = tasks.poll();
                }

                if (task != null) {
                    task.run();
                } else {
                    synchronized (this) {
                        currentThreadCount--;
                    }
                    return;
                }
            }
        }
    }
}
