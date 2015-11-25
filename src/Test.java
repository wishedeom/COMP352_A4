public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(10);
		
		for (int i = 1; i <= 11; i++)
		{
			h.put("A", "A");
			h.displayContents();
		}
	}
}