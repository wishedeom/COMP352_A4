import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(0);
		for (int i = 1; i <= 10; i++)
		{
			h.printHashTableStatistics();
			h.display();
			h.put(String.valueOf(i));
			System.out.println("Putting " + i);
		}
	}
}