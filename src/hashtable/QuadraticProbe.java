package hashtable;

class QuadraticProbe extends CollisionHandler
{
	private static final int DEFAULT_LINEAR_COEFFICIENT = 0;
	private static final int DEFAULT_QUADRATIC_COEFFICIENT = 1;
	
	private int c1;			// Linear coefficient
	private int c2;			// Quadratic coefficient
	
	QuadraticProbe()
	{
		this(DEFAULT_LINEAR_COEFFICIENT, DEFAULT_QUADRATIC_COEFFICIENT);
	}
	
	private QuadraticProbe(final int c1, final int c2)
	{
		super();
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public int nextHash()
	{
		final int hashCode = getRawHash() + c1 * getCounter() + c2 * getCounter() * getCounter();
		incrementCounter();
		return hashCode;
	}
}