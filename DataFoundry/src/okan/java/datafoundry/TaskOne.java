package okan.java.datafoundry;

import java.util.HashSet;
import java.util.Set;
import okan.java.datafoundry.Main.Printer;

/**
 * Takes char array in the constructor of this class.
 * Reverses the Array and checks the chars in this array if they have Vowels or Consonants inside.
 * Changes the Vowels to Upper case and Consonants to Lower case.
 * 
 * @author Okan SAYILGAN
 */
public class TaskOne implements Printer {

	/** Set of Characters holding the Vowels in the Alphabet */
	private static final Set<Character> vowels = getVowels();

	/** Array to be taken in the constructor. */
	private char[] taskOneArray;

	public TaskOne(char[] taskOneArray) {
		this.taskOneArray = taskOneArray;
	}

	@Override
	public void print() {

		print("\nTask 1 - Printed ...");

		char[] result = reverseAndReplace(taskOneArray);
		for (int i = 0; i < result.length; i++) {
			print(result[i]);
		}
	}

	/**
	 * Prints through the console.
	 * 
	 * @param object	char to be printed.
	 */
	private void print(char object) {
		System.out.println(object);
	}

	/**
	 * Prints through the console.
	 * 
	 * @param object	String to be printed.
	 */
	private void print(String object) {
		System.out.println(object);
	}

	/**
	 * Returns an array of the same characters in reversed order 
	 * with every consonant lower cased and every vowel upper cased.
	 * 
	 * @param arrayOfChars
	 * @return					Returns null if the array is null or empty, task one array otherwise.
	 */
	private char[] reverseAndReplace(char[] arrayOfChars) {

		if (arrayOfChars.length <= 0 || arrayOfChars == null) {
			return null;
		}

		/* Create new String Array with the same size of parameter to be returned */
		char[] returnArray = new char[arrayOfChars.length];

		/* variable to hold the index of Return Array */
		int j = 0;

		for (int i = arrayOfChars.length-1; i >= 0; i--) {

			char temp;

			/* Upper case for Vowel, Lower case for Consonant, Directly pass otherwise */
			if (isVowel(arrayOfChars[i]))
			    temp = Character.toUpperCase(arrayOfChars[i]);
			else
				temp = Character.toLowerCase(arrayOfChars[i]);

			returnArray[j] = temp;
			j++;
		}

		return returnArray;
	}

	/**
	 * Creates Set of Characters with Vowels.
	 * 
	 * @return	Newly created Set.
	 */
	private static Set<Character> getVowels() {

		Set<Character> vowels = new HashSet<Character>();

        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        vowels.add('A');
        vowels.add('E');
        vowels.add('I');
        vowels.add('O');
        vowels.add('U');
        return vowels;
	}

	/**
	 * Checks of the Vowels contain given char.
	 * 
	 * @param c	Char to be check if it is in the set.
	 * @return	True if there is given char in the set, false otherwise.
	 */
	private boolean isVowel(char c) {
	    return vowels.contains(c);
	}
}
