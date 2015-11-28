import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(3);
		for (int i = 1; i <= 10; i++)
		{
			h.display();
			h.put(String.valueOf(i));
		}
		
		System.out.println(h.remove(String.valueOf(7)));
		h.display();
	}
}