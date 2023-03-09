public class Main {

    public static MySemaphore ms = new MySemaphore(1);
    public static int shared = 0;
    public static int count = 4;
    public static void main(String[] args) throws InterruptedException {
        MyThread[] threads = new MyThread[count];

        for (int i = 0; i < count; ++i) {
            threads[i] = new MyThread(i % 2 == 0 ? 1 : -1);
            threads[i].start();
        }

        for (int i = 0; i < count; ++i) {
            threads[i].join();
        }

        System.out.println("All Threads Done. " + ms.numberAvailable() + " left.");
        System.out.println(shared);
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
        if (count <= 0) {
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

class MyThread extends Thread {
    int increment;
    public MyThread(int increment) {
        this.increment = increment;
    }

    public void run() {
            for (int i = 0; i < 1_000_000; ++i) {
                try {
                    Main.ms.request();

                    int temp = Main.shared;
                    temp += increment;
                    Main.shared = temp;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    Main.ms.release();
                }
            }
    }
}