
import hashtable.HashTable;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Test
{
	public static void main(String[] args)
	{
		
		Scanner input = new Scanner(System.in);		//Scanner object used for client input. 
		String collisionScheme = null;				//Stores client's selected collision-handling scheme. 
		String emptyScheme = null;					//Stores client's selected empty marker scheme. 
		
		
		int initialSize = 0;						//Initial size of Hash Table.
		HashTable h = new HashTable(initialSize);
		
		System.out.println("Enter the name of the file you would like to read from:");
		String fileName; //String object that holds name of test data set. 
		fileName = input.next();
		
		Scanner reader = null;						//Scanner object created to read test data sets (i.e. text file).
		
		try {
			reader = new Scanner(new FileReader(fileName));
		}
		catch(FileNotFoundException e) {
			System.out.println("Error reading from files. Program will terminate");
			System.exit(0);
		}
		
			System.out.println("Please enter an initial size for your Hash Table: ");
			initialSize = input.nextInt();
			h.resize(initialSize);
			System.out.println("This test consists of a Hash Table of initial size " + h.size());
			
		String answer;								//Stores client's answer to (Y/N) question.
		long beforeTime, afterTime;					//Record time before and after execution of method called (e.g. put). 
		boolean resume = true;						//Boolean variable for use in while-loop (as seen below).
		
		while(resume) 
		{
			try
			{
				System.out.println("\nEnter Collision Handling Scheme: ");
				collisionScheme = input.next();
				System.out.println("Enter EmptyMarkerScheme (in captial letters): ");
				emptyScheme = input.next();
				
				h.resize(h.size(), Character.toUpperCase(collisionScheme.charAt(0)), Character.toUpperCase(emptyScheme.charAt(0)));
				
				/** User is able to add a specific number of elements at a time **/
				System.out.println("How many entries would you liked to add?"); 
				
				int ceiling = input.nextInt();
				int counter = 1;
					
				beforeTime = System.currentTimeMillis();
				
				while(reader.hasNext() && (counter <= ceiling)) 
				{		++counter;
						h.put(reader.next());
				}
				
				afterTime = System.currentTimeMillis();
				System.out.println("\nExecution Time: " + (afterTime - beforeTime) + " milliseconds.\n");
				h.printHashTableStatistics();
				
				System.out.println("\nDo you wish to continue (Y/N)?");
				answer = input.next();
				if(answer.charAt(0) == 'N' || answer.charAt(0) == 'n' )
				{
					resume = false;
				}
			}
			catch(IllegalArgumentException e)
			{
				System.out.println("You have failed to enter the appropriate response. The program will now terminate.");
			}	
		}
		
		input.close();
	}
}