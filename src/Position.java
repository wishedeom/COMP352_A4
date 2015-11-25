public class Position
{
	private KeyValuePair keyValuePair;
	private int index;
	
	public Position(final KeyValuePair keyValuePair, final int index)
	{
		this.keyValuePair = keyValuePair;
		this.index = index;
	}
	
	public KeyValuePair get()
	{
		return keyValuePair;
	}
	
	public int getIndex()
	{
		return index;
	}
}