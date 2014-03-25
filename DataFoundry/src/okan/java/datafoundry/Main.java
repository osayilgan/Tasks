package okan.java.datafoundry;

/**
 * Data Foundry Inc, Test Main Class.
 * 
 * @author Okan SAYILGAN
 */
public class Main {
	
	private static final char[] taskOneArray = new char[]{'K', 'A', 'b', 'c', 'e', 'O', 'm', 'u'};
	
	/** MAIN */
	public static void main(String[] args) throws InterruptedException {

		/** FIRST TASK */
		TaskOne taskOne = new TaskOne(taskOneArray);
		taskOne.print();

		/** SECOND TASK */
		TaskTwo taskTwo = new TaskTwo();
		taskTwo.startProcess();
	}

	/**
	 * Interface to print Tasks
	 * 
	 * @author Okan SAYILGAN
	 */
	public interface Printer {

		/** Prints the Object */
		void print();
	}
}
