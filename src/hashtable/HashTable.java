package hashtable;

import hashtable.CollisionHandler.CollisionHandlingScheme;

/**
 * The HashTable class represents a hash table for storing String values with String keys. The default initial size of the table is 100 entries, and the table will automatically resize when
 * the load factor (ratio of stored elements to table size) exceeds 0.75. By default, each resizing will double the table size, but any factor or constant term can be selected. The table can
 * also be resized manually at any time. In any resizing, the stored entries will be re-hashed.
 * 
 * Entries can be added to the table, searched for by key, or removed by key. When adding an entry, if no entry with that key already exists in the table, a new entry is added. If an entry
 * with the same key already exists, the old value is replaced with the new value.
 * 
 * There are two methods for resolving hash collisions: By default, collisions are resolved by double hashing - multiples of a secondary hash function are added to the key's raw hash until an empty
 * array index is found. The alternative is quadratic hashing, where the images of a quadratic integer function are added to the raw hash until a suitable location is found.
 * 
 * To accelerate searching, removed entries are dealt with in one of three ways: By default, a special object (of type AvailablePosition) is placed in place of each entry. Alternatively, any key
 * string which begins with the symbol '-' can be considered a removed position. Finally, removed entries can simply be replaced with other entries whose keys would have had them out into the same
 * array element.
 * 
 * @author Michael Deom, Tarik Abbou-Saddik
 *
 */
public class HashTable
{
	
	/**
	 * Enumerates the three possible schemes for dealing with removed elements.
	 * 
	 * AVAILABLE: Removed elements are replaced with AvailablePosition objects.
	 * NEGATIVE: Removed elements have a '-' character placed at the head of their keys.
	 * REPLACE: Removed elements are replaced with other elements that would have been placed in the same spot.
	 *
	 */
	private enum EmptyMarkerScheme
	{
		AVAILABLE	('A'),
		NEGATIVE	('N'),
		REPLACE		('R');
		
		private char representation;	// Character representation of the enum value
		
		
		/**
		 * Constructor. Associates each value with a character representation.
		 * @param representation The character representation of the enum value.
		 */
		private EmptyMarkerScheme(final char representation)
		{
			this.representation = representation;
		}
		
		
		/**
		 * Converts from a character representation to an empty marker scheme.
		 * @param representation The character representation of the empty marker scheme. Must be 'A', 'N', or 'R'.
		 * @return The empty marker scheme enumerated value.
		 */
		private static EmptyMarkerScheme fromChar(final char representation)
		{
			EmptyMarkerScheme emptyMarkerScheme = null;
			
			for (EmptyMarkerScheme e : EmptyMarkerScheme.values())	// Iterate through the enum values until a matching one is found.
			{
				if (representation == e.representation)
				{
					emptyMarkerScheme = e;
				}
			}
			
			if (emptyMarkerScheme == null)	// Throw an exception if an unsupported character is passed.
			{
				throw new IllegalArgumentException("This character does not have an associated empty marker scheme.");
			}
			
			return emptyMarkerScheme;
		}
	}
	
	private static final int DEFAULT_INITIAL_SIZE = 100;																// Default initial size of hash table
	private static final CollisionHandlingScheme DEFAULT_COLLISION_HANDLING_SCHEME = CollisionHandlingScheme.DOUBLE;	// Default initial collision handling scheme
	private static final EmptyMarkerScheme DEFAULT_EMPTY_MARKER_SCHEME = EmptyMarkerScheme.AVAILABLE;					// Default initial empty marker scheme
	
	private Position[] positions;					// Holds the positions which point to the key-value pairs
	private Compressor compressor;					// Maps hash codes to array indices
	private CollisionHandler collisionHandler;		// Iterates through array indices to find an empty spot, when hash collisions occur
	private EmptyMarkerScheme emptyMarkerScheme;	// The current empty marker scheme being used
	private int numElements;						// The number of elements held by the hash table; starts at 0
	private double loadFactor;						// The ratio of held elements to array size
	
	
	/**
	 * No-argument constructor. Constructs an empty hash table with with initial size 100, double hashing, and "available" empty markers.
	 */
	public HashTable()
	{
		this(DEFAULT_INITIAL_SIZE, DEFAULT_COLLISION_HANDLING_SCHEME, DEFAULT_EMPTY_MARKER_SCHEME);
	}
	
	
	/**
	 * Constructor. Constructs a hash table with the given initial size.
	 * @param initialSize The hash table's initial size, a non-negative integer.
	 */
	public HashTable(final int initialSize)
	{		
		this(initialSize, DEFAULT_COLLISION_HANDLING_SCHEME, DEFAULT_EMPTY_MARKER_SCHEME);
	}
	
	
	/**
	 * Constructor. Constructs an empty hash table with the given initial size, collision handling scheme, and empty marker scheme.
	 * @param initialSize The hash table's initial size, a non-negative integer.
	 * @param collisionHandlingScheme The hash table's initial collision handling scheme.
	 * @param emptyMarkerScheme The hash table's initial empty marker scheme.
	 */
	public HashTable(final int initialSize, final CollisionHandlingScheme collisionHandlingScheme, final EmptyMarkerScheme emptyMarkerScheme)
	{
		// Check for illegal initial size
		if (initialSize < 0)
		{
			throw new IllegalArgumentException("Initial size must be a non-negative integer.");
		}
		
		this.positions = new Position[initialSize];
		this.compressor = new Compressor(this);
		this.emptyMarkerScheme = emptyMarkerScheme;
		this.numElements = 0;
		
		setCollisionHandlingType(collisionHandlingScheme);
		updateLoadFactor();		// Compute initial load factor 
	}
	
	
	/**
	 * Adds a new entry to the hash table, with a given string key and string value. If an entry with the same key already exists, the old value is replaced with the new value, and the old value
	 * is returned.
	 * @param key The key of the entry to add.
	 * @param value The value of the entry to add.
	 * @return Null if a new entry was added, the old value if it was replaced.
	 */
	public String put(final String key, final String value)
	{		
		final KeyValuePair kvp = new KeyValuePair(key, value);	// Create a new key-value pair with the given strings
		collisionHandler.reset(kvp.hashCode());					// Prepare the collisionHandler with the new pair; reset the counter
		
		// Iterate through the indices until an empty index or one holding an entry with the desired key is found
		int index;
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
		}
		while (!positionIsEmpty(index) && !positions[index].get().getKey().toString().equals(key));
		
		String oldValue = null;			// If no old value is found, will return null
		if (positionIsEmpty(index))		// If the position is empty, a new entry is created
		{
			positions[index] = new Position(kvp, index);
			addElement();				// Increment the number of elements; update load factor.
		}
		else
		{			
			oldValue = positions[index].get().getValue();	// If the position is not empty, it must have the same key as the put entry
			positions[index].get().setValue(value);			// So, replace and return the old value 
		}
		
		return oldValue;
	}
	
	
	/**
	 * Convenience method. Puts an entry with the same key and value.
	 * @param keyValue The key and the value of the entry to add.
	 * @return Null if a new entry was added, the old value if it was replaced.
	 */
	public String put(final String keyValue)
	{
		return put(keyValue, keyValue);
	}
	
	
	/**
	 * Searches the table for an entry with the given key and returns the associated value. Returns null if the entry is not found.
	 * @param key The key of the entry to search for.
	 * @return The associated value if it is found, null otherwise
	 */
	public String get(final String key)
	{
		final Key target = new Key(key);			// Create a new key-value pair with the given strings
		collisionHandler.reset(target.hashCode());	// Prepare the collision handler
		
		// Iterate until the entry is found, an empty location is found, or all elements have been searched.
		int index;
		int elementsSearched = 0;
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
			elementsSearched++;
		}
		while (elementsSearched <= numElements && !positionIsEmpty(index) && !positions[index].get().getKey().toString().equals(key));
		
		String foundValue = null;											// If the entry is not found, return null
		if (elementsSearched <= numElements && !positionIsEmpty(index))
		{
			foundValue = positions[index].get().getValue().toString();		// If the entry is found, return the value
		}
		
		return foundValue;
	}
	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String remove(final String key)
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
			makePositionAvailable(index);
			addElements(-1);
		}
		
		return foundValue;
	}
	
	private void makePositionAvailable(final int index)
	{
		switch (emptyMarkerScheme)
		{
			case AVAILABLE:
				positions[index] = new AvailablePosition(index);
				break;
			case NEGATIVE:
				final KeyValuePair original = positions[index].get();
				final KeyValuePair negated = new KeyValuePair("-" + original.getKey().toString(), original.getValue().toString());
				positions[index] = new Position(negated, index);
				break;
			case REPLACE:
				rollBack(index);
				break;
			default:
				break;
		}
	}
	
	private void rollBack(final int index)
	{
		if (isEmpty())
		{
			throw new RuntimeException("Cannot roll back: Hash table is empty.");
		}
		
		collisionHandler.reset(positions[index].get().hashCode());
		positions[index] = null;
		int nextIndex;
		do
		{
			nextIndex = compressor.compress(collisionHandler.nextHash());
		}
		while (!positionIsEmpty(nextIndex));
	}
	
	public void resize(final int newSize, final CollisionHandlingScheme newCollisionHandlingType, final EmptyMarkerScheme newEmptyMarkerScheme)
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
		compressor = newHashTable.compressor;
		numElements = newHashTable.numElements;
	}
	
	public void resize(final int newSize)
	{
		resize(newSize, collisionHandler.getType(), emptyMarkerScheme);
	}
	
	public void display()
	{
		for (Position p : positions)
		{
			if (p == null)
			{
				System.out.println("Never filled");
			}
			else if (p.isAvailablePosition())
			{
				System.out.println("Formerly occupied");
			}
			else
			{
				System.out.println(p.get());
			}
		}
		System.out.println();
	}
	
	public void setCollisionHandlingType(final CollisionHandlingScheme collisionHandlingType)
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
	
	public boolean setEmptyMarkerScheme(final char emptyMarkerScheme)
	{
		return setEmptyMarkerScheme(EmptyMarkerScheme.fromChar(emptyMarkerScheme));
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
					Position replacementPosition = null;
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
					positions[i] = replacementPosition;
				}
			}
		}
		
		return changedScheme;
	}
	
	private void addElement()
	{
		addElements(1);
	}
	
	private void addElements(final int change)
	{
		numElements += change;
		updateLoadFactor();
	}
	
	private double updateLoadFactor()
	{
		return loadFactor = ((double) numElements) / size();
		
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