import hashtable.HashTable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class HashTableDriver
{
	private static final String FILE_1 = "hash_test_file1.txt";
	private static final String FILE_2 = "hash_test_file2.txt";
	
	public static void main(String[] args)
	{
		final HashTable h = new HashTable();
		putFromFiles(h);
	}
	
	public static void putFromFiles(final HashTable h)
	{
		Scanner input1 = null;
		Scanner input2 = null;
		
		try
		{
			input1 = new Scanner(new FileReader(FILE_1));
			input2 = new Scanner(new FileReader(FILE_2));
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
	}
}