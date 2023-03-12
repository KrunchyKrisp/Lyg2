/*
 * Main.java
 * Matas Damidavičius INF 3k2g1p 1910621
 * ND2 variantas 1 - Semafora
 *
 * N gijų naudos bendrą MySemaphore objektą užtikrinti sinchroniškumą keičiant bendrą sharedResource skaitiklio
 * reikšmę ++/--;
 */
public class Main {
	public static int sharedResource = 0; //bendras skaitiklis

	public static void main(String[] args) throws InterruptedException {
		MySemaphore ms = new MySemaphore(1); //semafora su skaitikliu N = 1

		int count = 10;
		MyThread[] threads = new MyThread[count]; //N gijų

		for (int i = 0; i < count; ++i) {
			threads[i] = new MyThread(ms, i % 2 == 0 ? 1 : -1); //gija sukuriama su bendra semafora ir 1/-1
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
	int changeAmount; //+1/-1
	MySemaphore ms; //bendra semafora

	public MyThread(MySemaphore ms, int changeAmount) {
		this.ms = ms;
		this.changeAmount = changeAmount;
	}

	public void run() { //1_000_000 kartų, pasinaudojant semafora, ++/-- bendrą skaitiklį
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
	private int n; //semaforos skaitiklis

	public MySemaphore(int n) {
		this.n = n;
	}

	public int numberAvailable() {
		return n;
	}

	public synchronized void request() throws InterruptedException {
		while (n <= 0) { //kol nėra available, laukiame
			this.wait();
		}
		--n;
	}

	public synchronized void release() {
		++n;
		if (n > 0) { //jei yra available, pranešame visiems laukiantiems
			this.notifyAll();
		}
	}
}