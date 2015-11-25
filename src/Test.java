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
		
		System.out.println("\n" + h.get("A"));
	}
}