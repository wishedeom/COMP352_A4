package hashtable;

abstract class CollisionHandler
{
	private int rawHash;	// Unmodified hash code
	private int counter;	// Counter
	
	public CollisionHandler()
	{
		reset(0);
	}
	
	public void reset(final int rawHash)
	{
		this.rawHash = rawHash;
		counter = 0;
	}
	
	public int getRawHash()
	{
		return rawHash;
	}
	
	public int getCounter()
	{
		return counter;
	}
	
	public void incrementCounter()
	{
		counter++;
	}
	
	abstract public int nextHash();
}