import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(3);
		
		h.put("A");
		h.displayContents();
		System.out.println("Replacing " + h.put("A", "B"));
		h.displayContents();
		h.put("B");
		h.displayContents();
		h.put("C");
		h.displayContents();
		h.put("D");
		h.displayContents();
		h.put("A", "5");
		h.displayContents();
	}
}