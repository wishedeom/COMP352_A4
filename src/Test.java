
import hashtable.HashTable;
import hashtable.CollisionHandler;

import java.util.InputMismatchException;
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
		HashTable h = null;
		
		System.out.println("HASH TABLE DRIVER");
		System.out.println("=================\n");
		
		int initialSize = 0;
		{
			boolean inputIsGood;
			do
			{
				inputIsGood = true;
				System.out.println("Enter the size of your new hash table: (The result will be rounded to the next prime number.)");
				try
				{
					initialSize = input.nextInt();
				}
				catch(InputMismatchException e)
				{
					System.out.println("Not an integer.");
					inputIsGood = false;
				}
			}
			while(!inputIsGood);
		}
		
		/*double initialRehashThreshold = 0;
		{
			boolean inputIsGood = true;
			do
			{
				System.out.println("Enter the initial rehash threshold: (Between 0 and 1 inclusive.)");
				try
				{
					initialRehashThreshold = input.nextDouble();
					if (!(0 <= initialRehashThreshold && initialRehashThreshold <= 1))
					{
						inputIsGood = false;
					}
					
				}
				catch(InputMismatchException e)
				{
					System.out.println("Not an double.");
					inputIsGood = false;
				}
			}
			while(!inputIsGood);
		}*/
		
		char initialCollisionHandlingScheme = 0;
		{
			boolean inputIsGood;
			do
			{
				inputIsGood = true;
				System.out.println("Enter the initial collision handling scheme: ('D'ouble or 'Q'uadratic.)");
				initialCollisionHandlingScheme = Character.toUpperCase(input.next().charAt(0));
				if (!(initialCollisionHandlingScheme == 'D' || initialCollisionHandlingScheme == 'Q'))
				{
					inputIsGood = false;
				}
			}
			while(!inputIsGood);
		}
			
		char initialEmptyMarkerScheme = 0;
		{
			boolean inputIsGood;
			do
			{
				inputIsGood = true;
				System.out.println("Enter the initial empty marker scheme: ('A'vailable, 'N'egative, or 'R'eplacement.)");
				initialEmptyMarkerScheme = Character.toUpperCase(input.next().charAt(0));
				if (!(initialEmptyMarkerScheme == 'A' || initialEmptyMarkerScheme == 'N' || initialEmptyMarkerScheme == 'R'))
				{
					inputIsGood = false;
				}
			}
			while(!inputIsGood);
		}
		
		h = new HashTable(initialSize);
		h.resize(h.size(), initialCollisionHandlingScheme, initialEmptyMarkerScheme);
		System.out.println();
		h.printHashTableStatistics();
	
		System.out.println("Enter the name of the file you would like to read from:");
		String fileName; //String object that holds name of test data set. 
		fileName = input.next();
		
		Scanner reader = null;						//Scanner object created to read test data sets (i.e. text file).
		
		try
		{
			reader = new Scanner(new FileReader(fileName));
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error reading from files. Program will terminate");
			System.exit(0);
		}
			
		String answer;								//Stores client's answer to (Y/N) question.
		long beforeTime, afterTime;					//Record time before and after execution of method called (e.g. put). 
		boolean resume = true;						//Boolean variable for use in while-loop (as seen below).
		
		while(resume) 
		{
			try
			{				
				//User is able to add a specific number of elements at a time
				System.out.println("How many entries would you liked to add?"); 
				
				int ceiling = input.nextInt();
				int counter = 1;
				
				boolean remove = ceiling < 0;
				if (remove)
				{
					ceiling = -ceiling;
				}
				
				beforeTime = System.currentTimeMillis();
				
				while(reader.hasNext() && (counter <= ceiling)) 
				{
					++counter;
					if (!remove)
					{
						h.put(reader.next());
					}
					else
					{
						h.remove(reader.next());
					}
				}
				
				afterTime = System.currentTimeMillis();
				System.out.println("\nExecution Time: " + (afterTime - beforeTime) + " milliseconds.\n");
				h.printHashTableStatistics();
				
				System.out.println("\nDo you wish to continue (Y/N/Reset reader/Hash table options)?");
				answer = input.next();
				if(Character.toUpperCase(answer.charAt(0)) == 'H')
				{
					initialCollisionHandlingScheme = 0;
					{
						boolean inputIsGood;
						do
						{
							inputIsGood = true;
							System.out.println("Enter the initial collision handling scheme: ('D'ouble or 'Q'uadratic.)");
							initialCollisionHandlingScheme = Character.toUpperCase(input.next().charAt(0));
							if (!(initialCollisionHandlingScheme == 'D' || initialCollisionHandlingScheme == 'Q'))
							{
								inputIsGood = false;
							}
						}
						while(!inputIsGood);
					}
						
					initialEmptyMarkerScheme = 0;
					{
						boolean inputIsGood;
						do
						{
							inputIsGood = true;
							System.out.println("Enter the initial empty marker scheme: ('A'vailable, 'N'egative, or 'R'eplacement.)");
							initialEmptyMarkerScheme = Character.toUpperCase(input.next().charAt(0));
							if (!(initialEmptyMarkerScheme == 'A' || initialEmptyMarkerScheme == 'N' || initialEmptyMarkerScheme == 'R'))
							{
								inputIsGood = false;
							}
						}
						while(!inputIsGood);
					}
					
					h.resize(h.size(), initialCollisionHandlingScheme, initialEmptyMarkerScheme);
				}
				if (Character.toUpperCase(answer.charAt(0)) == 'R')
				{
					System.out.println("Enter the name of the file you would like to read from:");
					fileName = input.next();
					
					reader.close();
					
					try
					{
						reader = new Scanner(new FileReader(fileName));
					}
					catch(FileNotFoundException e)
					{
						System.out.println("Error reading from files. Program will terminate");
						System.exit(0);
					}
				}
				if (answer.charAt(0) == 'N' || answer.charAt(0) == 'n' )
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