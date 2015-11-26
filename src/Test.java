import hashtable.HashTable;

public class Test
{
	public static void main(String[] args)
	{
		HashTable h = new HashTable(3);
		h.setEmptyMarkerScheme('R');
		h.display();
		h.put("Hello");
	}
}