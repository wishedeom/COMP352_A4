import hashtable.HashTable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class HashTableDriver
{
	public static void main(String[] args)
	{
		final HashTable h = new HashTable();
		
		Scanner input1 = null;
		Scanner input2 = null;
		
		try
		{
			input1 = new Scanner(new FileReader("hash_test_file1.txt"));
			input2 = new Scanner(new FileReader("hash_test_file2.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		while (input1.hasNext())
		{
			h.put(input1.next());
		}
		
		while (input2.hasNext())
		{
			h.put(input2.next());
		}
		
		h.printHashtableStatistics();
	}
}