package hashtable;

public abstract class CollisionHandler
{
	enum CollisionHandlingScheme
	{
		DOUBLE		('D'),
		QUADRATIC	('Q');
		
		private char representation;	// Character representation of the enum value
		
		
		/**
		 * Constructor. Associates each value with a character representation.
		 * @param representation The character representation of the enum value.
		 */
		private CollisionHandlingScheme(final char representation)
		{
			this.representation = representation;
		}
		
		
		/**
		 * Converts from a character representation to an empty marker scheme.
		 * @param representation The character representation of the empty marker scheme. Must be 'A', 'N', or 'R'.
		 * @return The empty marker scheme enumerated value.
		 */
		static CollisionHandlingScheme fromChar(final char representation)
		{
			CollisionHandlingScheme collisionHandlingScheme = null;
			
			for (CollisionHandlingScheme e : CollisionHandlingScheme.values())	// Iterate through the enum values until a matching one is found.
			{
				if (representation == e.representation)
				{
					collisionHandlingScheme = e;
					break;
				}
			}
			
			if (collisionHandlingScheme == null)	// Throw an exception if an unsupported character is passed.
			{
				throw new IllegalArgumentException("This character does not have an associated empty marker scheme.");
			}
			
			return collisionHandlingScheme;
		}
	}
	
	private int rawHash;	// Unmodified hash code
	private int counter;	// Counter
	
	public CollisionHandler()
	{
		reset(0);
	}
	
	abstract public int nextHash();
	abstract public CollisionHandlingScheme getType();
	
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