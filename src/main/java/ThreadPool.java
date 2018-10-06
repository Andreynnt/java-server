import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final Queue<Runnable> tasks;
    private final Worker[] threads;

    public ThreadPool(Integer maxThreadsCount) {
        tasks = new LinkedBlockingQueue<>();
        threads = new Worker[maxThreadsCount];

        for (int i = 0; i < maxThreadsCount; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }

    }

    public void execute(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    private class Worker extends Thread {
        public void run() {
            Runnable task;
            while (true) {
                synchronized(tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    task.run();
                } catch (Exception e) {
                    System.out.println(e.toString());

                }
            }
        }
    }
}
