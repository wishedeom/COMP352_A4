import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(3);
		h.displayContents();
		h.put("Hello");
		h.displayContents();
		h.resize(4);
		h.displayContents();
	}
}