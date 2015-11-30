
import hashtable.HashTable;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Test
{
	public static void main(String[] args)
	{
		final int INITIAL_SIZE;
		long beforeTime, afterTime;
		Scanner input = new Scanner(System.in);
		boolean resume = true;
		String collisionScheme;
		String emptyScheme;
		String answer;
		
		
		System.out.println("Please enter an initial size for your Hash Table: ");
		INITIAL_SIZE = input.nextInt();
		System.out.println("This test consists of a Hash Table of initial size " + INITIAL_SIZE);
		HashTable h = new HashTable(INITIAL_SIZE);
		
		while(resume) 
		{
			System.out.println("Enter Collision Handling Scheme: ");
			collisionScheme = input.next();
			System.out.println("Enter EmptyMarkerScheme (in captial letters): ");
			emptyScheme = input.next();
			h.resize(h.size(),collisionScheme.charAt(0),emptyScheme.charAt(0));
			h.setEmptyMarkerScheme(emptyScheme.charAt(0));
				
			beforeTime = System.currentTimeMillis();
			HashTableDriver.putFromFiles(h);
			afterTime = System.currentTimeMillis();
			System.out.println("\nExecution Time: " + (afterTime - beforeTime) + " milliseconds.\n");
			h.printHashTableStatistics();
			
			System.out.println("\nDo you wish to continue (Y/N)?");
			answer = input.next();
			if(answer.charAt(0) == 'N' || answer.charAt(0) == 'n' )
				resume = false;
			
		}
		
		input.close();
	}
}