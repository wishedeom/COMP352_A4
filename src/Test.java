import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(3);
		h.display();
		h.put("Hello");
		h.resize(4);
		h.display();
		h.remove("Hello");
		h.display();
	}
}