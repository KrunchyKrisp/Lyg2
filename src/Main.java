/*
 *
 */
public class Main {
    public static int sharedResource = 0;

    public static void main(String[] args) throws InterruptedException {
        MySemaphore ms = new MySemaphore(1);

        int count = 10;
        MyThread[] threads = new MyThread[count];

        for (int i = 0; i < count; ++i) {
            threads[i] = new MyThread(ms, i % 2 == 0 ? 1 : -1);
            threads[i].start();
        }

        for (int i = 0; i < count; ++i) {
            threads[i].join();
        }

        System.out.println("All Threads Done");
        System.out.println("Semaphore Counter = " + ms.numberAvailable());
        System.out.println("Shared Resource = " + sharedResource);
    }
}

class MyThread extends Thread {
    int changeAmount;
    MySemaphore ms;

    public MyThread(MySemaphore ms, int changeAmount) {
        this.ms = ms;
        this.changeAmount = changeAmount;
    }

    public void run() {
        for (int i = 0; i < 1_000_000; ++i) {
            try {
                ms.request();

                int temp = Main.sharedResource;
                temp += changeAmount;
                Main.sharedResource = temp;

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                ms.release();
            }
        }
    }
}

class MySemaphore {
    private int count;

    public MySemaphore(int count) {
        this.count = count;
    }

    public int numberAvailable() {
        return count;
    }

    public synchronized void request() throws InterruptedException {
        while (count <= 0) {
            this.wait();
        }
        --count;
    }

    public synchronized void release() {
        ++count;
        if (count > 0) {
            this.notify();
        }
    }
}