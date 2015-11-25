package hashtable;


public class HashTable
{	
	private static final int DEFAULT_SIZE = 100;
	private static final CollisionHandlingType DEFAULT_COLLISION_HANDLING_TYPE = CollisionHandlingType.DOUBLE;
	private static final EmptyMarkerScheme DEFAULT_EMPTY_MARKER_SCHEME = EmptyMarkerScheme.AVAILABLE;
	
	private Position[] positions;
	private Compressor compressor;
	private CollisionHandler collisionHandler;
	private EmptyMarkerScheme emptyMarkerScheme;
	private int numElements;
	
	public HashTable()
	{
		this(DEFAULT_SIZE, DEFAULT_COLLISION_HANDLING_TYPE, DEFAULT_EMPTY_MARKER_SCHEME);
	}
	
	public HashTable(final int initialSize)
	{
		this(initialSize, DEFAULT_COLLISION_HANDLING_TYPE, DEFAULT_EMPTY_MARKER_SCHEME);
	}
	
	public HashTable(final int initialSize, final CollisionHandlingType collisionHandlingType, final EmptyMarkerScheme emptyMarkerScheme)
	{
		this.positions = new Position[initialSize];
		this.compressor = new Compressor(this);
		this.emptyMarkerScheme = emptyMarkerScheme;
		this.numElements = 0;
		
		setCollisionHandlingType(collisionHandlingType);
	}
	
	public void put(final String key, final String value)
	{
		if (isFull())
		{
			throw new RuntimeException("Hash table is ass3ass.");
		}
		
		final KeyValuePair kvp = new KeyValuePair(key, value);
		collisionHandler.reset(kvp.hashCode());
		
		int index;
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
		}
		while(!positionIsEmpty(index));
		
		positions[index] = new Position(kvp, index);
	}
	
	/*public void get(final String key)
	{
		final KeyValuePair
	}*/
	
	public void displayContents()
	{
		for (Position p : positions)
		{
			if (p == null)
			{
				System.out.println("Never filled");
			}
			else
			{
				System.out.println(p.get());
			}
		}
		System.out.println();
	}
	
	public void setCollisionHandlingType(final CollisionHandlingType collisionHandlingType)
	{
		if (!isEmpty())
		{
			throw new RuntimeException ("Hash table must be empty to change the collision handling type.");
		}
		
		switch (collisionHandlingType)
		{
			case DOUBLE:
				collisionHandler = new DoubleHasher(this);
				break;
			case QUADRATIC:
				collisionHandler = new QuadraticProbe();
				break;
			default:
				break;
		}
	}
	
	public boolean isEmpty()
	{
		return numElements == 0;
	}
	
	public boolean isFull()
	{
		return numElements >= size();
	}
	
	public boolean positionIsEmpty(final int index)
	{		
		return (positions[index] == null)
			|| (emptyMarkerScheme == EmptyMarkerScheme.AVAILABLE && positions[index] instanceof AvailablePosition)
			|| (emptyMarkerScheme == EmptyMarkerScheme.NEGATIVE && positions[index].get() != null && positions[index].get().getKey().charAt(0) == '-');
	}
	
	public int size()
	{
		return positions.length;
	}
}	