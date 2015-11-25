package hashtable;

import hashtable.CollisionHandler.CollisionHandlingType;

public class HashTable
{
	private enum EmptyMarkerScheme
	{
		AVAILABLE,
		NEGATIVE,
		REPLACE;
	}
	
	private static final int DEFAULT_SIZE = 100;
	private static final CollisionHandlingType DEFAULT_COLLISION_HANDLING_TYPE = CollisionHandler.CollisionHandlingType.DOUBLE;
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
	
	public String put(final String key, final String value)
	{
		// Change to table extension later
		// Att: Should allow value replacement even if table is full
		if (isFull())
		{
			throw new RuntimeException("Hash table is full.");
		}
		
		final KeyValuePair kvp = new KeyValuePair(key, value);
		collisionHandler.reset(kvp.hashCode());
		
		int index;
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
		}
		while (!positionIsEmpty(index) && !positions[index].get().getKey().toString().equals(key));
		
		String oldValue = null;
		if (positionIsEmpty(index))
		{
			positions[index] = new Position(kvp, index);
			numElements++;
		}
		else
		{			
			oldValue = positions[index].get().getValue();
			positions[index].get().setValue(value);
		}
		
		return oldValue;
	}
	
	public String put(final String keyValue)
	{
		return put(keyValue, keyValue);
	}
	
	public String get(final String key)
	{
		final Key target = new Key(key);
		collisionHandler.reset(target.hashCode());
		
		int index;
		int elementsSearched = 0;
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
			elementsSearched++;
		}
		while (elementsSearched <= numElements && !positionIsEmpty(index) && !positions[index].get().getKey().toString().equals(key));
		
		String foundValue = null;
		if (elementsSearched <= numElements && !positionIsEmpty(index))
		{
			foundValue = positions[index].get().getValue().toString();
		}
		
		return foundValue;
	}
	
	public void resize(final int newSize, final CollisionHandlingType newCollisionHandlingType, final EmptyMarkerScheme newEmptyMarkerScheme)
	{
		if (newSize < numElements)
		{
			throw new IllegalArgumentException("New size not large enough to hold all elements.");
		}
		
		numElements = 0;
		setCollisionHandlingType(newCollisionHandlingType);
		setEmptyMarkerScheme(newEmptyMarkerScheme);
		
		HashTable newHashTable = new HashTable(newSize, newCollisionHandlingType, newEmptyMarkerScheme);
		for (Position p : positions)
		{
			if (p != null)
			{
				final String key = p.get().getKey();
				final String value = p.get().getValue();
				newHashTable.put(key, value);
			}
		}
		
		positions = newHashTable.positions;
		numElements = newHashTable.numElements;
	}
	
	public void resize(final int newSize)
	{
		resize(newSize, collisionHandler.getType(), emptyMarkerScheme);
	}
	
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
	
	public boolean setEmptyMarkerScheme(final EmptyMarkerScheme emptyMarkerScheme)
	{
		final boolean changedScheme = this.emptyMarkerScheme != emptyMarkerScheme;
		
		if (changedScheme)
		{
			for (int i = 0; i < positions.length; i++)
			{
				if (positionIsFormerlyOccupied(i))
				{
					Position replacementPosition;
					switch (emptyMarkerScheme)
					{
						case AVAILABLE:
							replacementPosition = new AvailablePosition(i);
							break;
						case NEGATIVE:
							replacementPosition = new Position(new KeyValuePair("-", ""), i);
							break;
						case REPLACE:
							// Do thing
							break;
						default:
							break;
					}
				}
			}
		}
		
		return changedScheme;
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
		return (positions[index] == null) || positionIsFormerlyOccupied(index);
	}
	
	public boolean positionIsFormerlyOccupied(final int index)
	{		
		return (positions[index] != null)
			&& ((emptyMarkerScheme == EmptyMarkerScheme.AVAILABLE && positions[index].isAvailablePosition())
			|| (emptyMarkerScheme == EmptyMarkerScheme.NEGATIVE && positions[index].get() != null && positions[index].get().getKey().charAt(0) == '-'));
	}
	
	public int size()
	{
		return positions.length;
	}
}	