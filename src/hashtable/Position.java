package hashtable;

class Position
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
	
	public boolean isAvailablePosition()
	{
		return this.getClass() == AvailablePosition.class;
	}
}

class AvailablePosition extends Position
{
	public AvailablePosition(final int index)
	{
		super(null, index);
	}
}