package hashtable;

abstract class CollisionHandler
{
	enum CollisionHandlingType
	{
		DOUBLE,
		QUADRATIC;
	}
	
	private int rawHash;	// Unmodified hash code
	private int counter;	// Counter
	
	public CollisionHandler()
	{
		reset(0);
	}
	
	abstract public int nextHash();
	abstract public CollisionHandlingType getType();
	
	public final void reset(final int rawHash)
	{
		this.rawHash = rawHash;
		counter = 0;
	}
	
	public int getRawHash()
	{
		return rawHash;
	}
	
	public final int getCounter()
	{
		return counter;
	}
	
	public final void incrementCounter()
	{
		counter++;
	}
	
	public int nextHash(final int repetitions)
	{
		if (repetitions < 1)
		{
			throw new IllegalArgumentException("Number of repetitions must be a positive integer.");
		}
		
		int finalHash = Integer.MIN_VALUE;
		
		for (int i = 1; i <= repetitions; i++)
		{
			finalHash = nextHash();
		}
		
		return finalHash;
	}
}