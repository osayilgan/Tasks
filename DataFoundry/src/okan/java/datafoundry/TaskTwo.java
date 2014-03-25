package okan.java.datafoundry;

import java.util.Arrays;

import okan.java.datafoundry.Main.Printer;

/**
 * Runs two different Thread which can reach to a shared Array.
 * Each thread increases the Array's values by 1.
 * 
 * @author Okan SAYILGAN
 */
public class TaskTwo implements Printer {

	private Integer[] mIntegerArray = new Integer[1000];

	public TaskTwo() {

		/* When the new Object is created, generate and insert 1000 times "0" to the mIntegerArray */
		for (int i = 0; i < 1000; i++) {
			mIntegerArray[i] = 0;
		}
	}

	/**
	 * Starts two different threads to work on the same Array.
	 * Prints the result of Array When the tasks are finished.
	 * 
	 * @throws InterruptedException
	 */
	public void startProcess() throws InterruptedException {

		/* Create an Instance of Processor Class */
		Processor processor = new Processor();

		/* Create Concurrent Threads */
		Thread firstThread = new Thread(processor);
		Thread secondThread = new Thread(processor);

		/* Start Threads */
		firstThread.start();
		secondThread.start();

		/* Join Threads here, wait for them to be finished */
		firstThread.join();
		secondThread.join();

		/* Print the Result */
		print();
	}

	@Override
	public void print() {
		System.out.println("\nTask 2 - Printed ...");
		System.out.println(Arrays.asList(mIntegerArray));
	}

	/**
	 * Class implementing the Runnable Interface and Increasing the Numbers in the Array.
	 * 
	 * @author Okan SAYILGAN
	 */
	class Processor implements Runnable {

		@Override
		public void run() {

			/* Do the task here */
			increaseNumbersInArray();
		}

		/**
		 * Increases the number in the Integer Array
		 */
		private void increaseNumbersInArray() {

			for (int i = 0; i < mIntegerArray.length; i++) {
				mIntegerArray[i] = increaseInteger(mIntegerArray[i]);
			}
		}

		/** Increases the Given Integer value by 1 */
		private int increaseInteger(int i) {
			synchronized (this) {
				return ++i;
			}
		}
	}
}
