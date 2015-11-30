package hashtable;

import prime.Prime;
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
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//	DEFAULT VALUES
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private static final int DEFAULT_INITIAL_SIZE = 100;																// Default initial size of hash table
	private static final double DEFAULT_REHASH_THRESHOLD = 0.75;														// Default initial size of hash table
	private static final boolean DEFAULT_EXPAND_BY_FACTOR = true;														// Default to expanding by a factor
	private static final double DEFAULT_REHASH_FACTOR = 2;																// Default expansion factor
	private static final int DEFAULT_REHASH_NUMBER = 100;																// Default expansion number
	
	private static final CollisionHandlingScheme DEFAULT_COLLISION_HANDLING_SCHEME = CollisionHandlingScheme.DOUBLE;	// Default initial collision handling scheme
	private static final EmptyMarkerScheme DEFAULT_EMPTY_MARKER_SCHEME = EmptyMarkerScheme.AVAILABLE;					// Default initial empty marker scheme
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// INSTANCE VARIABLES
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private Position[] positions;					// Holds the positions which point to the key-value pairs
	private Compressor compressor;					// Maps hash codes to array indices
	private CollisionHandler collisionHandler;		// Iterates through array indices to find an empty spot, when hash collisions occur
	private EmptyMarkerScheme emptyMarkerScheme;	// The current empty marker scheme being used
	private int numElements;						// The number of elements held by the hash table; starts at 0
	private double loadFactor;						// The ratio of held elements to array size
	private double rehashThreshold;					// Maximum load factor before rehashing; between 0 and 1 inclusive
	
	// Expansion variables
	private boolean expandByFactor;					// True if expanding table size by a given factor, false if expanding by a given addition  
	private double rehashFactor;					// The factor to expand by
	private int rehashNumber;						// The number of addition cells to expand by 
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTORS
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * No-argument constructor. Constructs an empty hash table with with initial size 100, double hashing, and "available" empty markers.
	 */
	public HashTable()
	{
		this(DEFAULT_INITIAL_SIZE);
	}
	
	
	/**
	 * Constructor. Constructs a hash table with the given initial size.
	 * @param initialSize The hash table's initial size, a non-negative integer.
	 */
	public HashTable(final int initialSize)
	{		
		this(initialSize, DEFAULT_REHASH_THRESHOLD, DEFAULT_COLLISION_HANDLING_SCHEME, DEFAULT_EMPTY_MARKER_SCHEME);
	}
	
	
	/**
	 * Constructor. Constructs an empty hash table with the given initial size, rehash threshold, collision handling scheme, and empty marker scheme.
	 * @param initialSize The hash table's initial size, a non-negative integer.
	 * @param initialRehashThreshold The hash table's initial rehash threshold, a floating-point number between 0 and 1.
	 * @param collisionHandlingScheme The hash table's initial collision handling scheme.
	 * @param emptyMarkerScheme The hash table's initial empty marker scheme.
	 */
	public HashTable(final int initialSize, final double initialRehashThreshold, final CollisionHandlingScheme collisionHandlingScheme, final EmptyMarkerScheme emptyMarkerScheme)
	{
		// Check for illegal initial size
		if (initialSize < 0)
		{
			throw new IllegalArgumentException("Initial size must be a non-negative integer.");
		}
		
		final int nextPrimeSize = Prime.nextLargestPrime(initialSize);	// Enforces the fact that size should be a prime number
		this.positions = new Position[nextPrimeSize];
		this.compressor = new Compressor(this);
		this.emptyMarkerScheme = emptyMarkerScheme;
		this.numElements = 0;
		this.rehashThreshold = initialRehashThreshold;
		
		this.expandByFactor = DEFAULT_EXPAND_BY_FACTOR;
		if (expandByFactor)
		{
			setRehashFactor(DEFAULT_REHASH_FACTOR);
		}
		else
		{
			setRehashFactor(DEFAULT_REHASH_NUMBER);
		}
		
		setCollisionHandlingScheme(collisionHandlingScheme);
		updateLoadFactor();		// Compute initial load factor 
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// MAP METHODS
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
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
			
			if (!positionIsEmpty(index))
			{
				positions[index].get().incrementCollisions();
			}
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
	 * Searches for an entry with the given key, and removes and returns the associated value if one is found. Returns null otherwise.
	 * @param key The key of the entry to remove.
	 * @return The value of the removed entry if one is found, null otherwise.
	 */
	public String remove(final String key)
	{
		final Key target = new Key(key);
		collisionHandler.reset(target.hashCode());
		
		int index;
		int elementsSearched = 0;
		
		// Iterate through table contents until an empty location is found, all elements are searched, or an entry with a matching key is found
		do
		{
			index = compressor.compress(collisionHandler.nextHash());
			elementsSearched++;
		}
		while (elementsSearched <= numElements && !positionIsEmpty(index) && !positions[index].get().getKey().toString().equals(key));
		
		String foundValue = null;	// If no matching entry is found, return null
		if (elementsSearched <= numElements && !positionIsEmpty(index))	// Otherwise, return the matching value and delete the entry
		{
			foundValue = positions[index].get().getValue().toString();
			makePositionAvailable(index);	// Marks the position as formerly occupied, but now available
			addElements(-1);
		}
		
		return foundValue;
	}
	
	
	/**
	 * Marks the position as available according to the empty marker scheme of the hash table
	 * @param index
	 */
	private void makePositionAvailable(final int index)
	{
		switch (emptyMarkerScheme)
		{
			case AVAILABLE:
				positions[index] = new AvailablePosition(index);	// If using the AVAILABLE scheme, replace the positon with an AvailablePosition marker
				break;
			case NEGATIVE:
				final KeyValuePair original = positions[index].get();	// If using the NEGATIVE scheme, place a '-' character at the head of the entry's key
				final KeyValuePair negated = new KeyValuePair("-" + original.getKey().toString(), original.getValue().toString());
				positions[index] = new Position(negated, index);
				break;
			case REPLACE:	// If using the REPLACE scheme, pull back another entry with the same hash to the removed location
				rollBack(index);
				break;
			default:	// This should not occur; all enum values are accounted for
				throw new RuntimeException(emptyMarkerScheme + " is an unsupported EmptyMarkerScheme.");
		}
	}
	
	//
	//INCOMPLETE
	//
	/**
	 * Replaces the entry at a given index with another entry that would have been hashed there if it was empty.
	 * @param index The index of the entry to roll back to.
	 */
	private void rollBack(final int index)
	{
		if (isEmpty())	// Should not be called if the table is empty anyway
		{
			throw new RuntimeException("Cannot roll back: Hash table is empty.");
		}
		
		collisionHandler.reset(positions[index].get().hashCode());
		positions[index] = null;
		int nextIndex;
		
		// Iterate until a non-empty 
		do
		{
			nextIndex = compressor.compress(collisionHandler.nextHash());
		}
		while (!positionIsEmpty(nextIndex));
	}
	
	
	/**
	 * Resizes the table to a desired size. The chosen size will be rounded up to the next largest prime number.
	 * @param newSize The new desired size. Will be rounded up to a prime number.
	 * @param newCollisionHandlingScheme The table's new collision handling scheme.
	 * @param newEmptyMarkerScheme The table's new empty marker scheme.
	 */
	public void resize(final int newSize, final CollisionHandlingScheme newCollisionHandlingScheme, final EmptyMarkerScheme newEmptyMarkerScheme)
	{
		// Cannot make a table smaller than the number of elements it contains
		if (newSize < numElements)
		{
			throw new IllegalArgumentException("New size not large enough to hold all elements.");
		}
		
		numElements = 0;	// To allow the collision and empty marker schemes to be changed; will be updated after
		setCollisionHandlingScheme(newCollisionHandlingScheme);	// Update the schemes
		setEmptyMarkerScheme(newEmptyMarkerScheme);
		
		final int nextPrimeSize = Prime.nextLargestPrime(newSize);	//Size should always be prime, so round up to the next prime

		// Make a new hash table with the desired size; properties will be copied over
		HashTable newHashTable = new HashTable(nextPrimeSize, rehashThreshold, newCollisionHandlingScheme, newEmptyMarkerScheme);
		
		// Put each old entry into the new table; the proper hashing and compression algorithms will be automatically used
		for (Position p : positions)
		{
			if (p != null)
			{
				final String key = p.get().getKey();
				final String value = p.get().getValue();
				newHashTable.put(key, value);
			}
		}
		
		// Copy over relevant properties
		positions = newHashTable.positions;
		compressor = newHashTable.compressor;
		numElements = newHashTable.numElements;
	}
	
	/**
	 * Changes the table's rehash threshold. When the load factor equals or exceeds this number, the table will be resized. 
	 * @param rehashThreshold The table's new rehash threshold.
	 */
	public void setRehashThreshold(final double rehashThreshold)
	{
		if (!(0.0 <= rehashThreshold && rehashThreshold <= 1.0))
		{
			throw new IllegalArgumentException("Rehash threshold must be between 0 and 1, inclusive.");
		}
		
		this.rehashThreshold = rehashThreshold;
		updateLoadFactor();
	}
	
	/**
	 * Sets the factor by which the table will expand when the rehash threshold is met.
	 * @param rehashFactor The table's new rehash factor. Must be greater than unity.
	 */
	public void setRehashFactor(final double rehashFactor)
	{
		if (rehashFactor <= 1.0)
		{
			throw new IllegalArgumentException("Rehash factor must be greater than unity.");
		}
		
		expandByFactor = true;
		this.rehashFactor = rehashFactor;
	}
	
	/**
	 * Sets the number of cells by which the table will expand when the rehash threshold is met.
	 * @param rehashFactor The table's new rehash factor. Must be greater than unity.
	 */
	public void setRehashFactor(final int rehashNumber)
	{
		if (rehashNumber <= 1)
		{
			throw new IllegalArgumentException("Rehash number must be greater than unity.");
		}
		
		expandByFactor = false;
		this.rehashNumber = rehashNumber;
	}
	
	
	/**
	 * Resizes the table to a desired size. The chosen size will be rounded up to the next largest prime number.
	 * @param newSize The new desired size. Will be rounded up to a prime number.
	 */
	public void resize(final int newSize)
	{
		resize(newSize, collisionHandler.getType(), emptyMarkerScheme);
	}
	
	public void resize(final int newSize, final char collisionHandlingScheme, final char emptyMarkerScheme)
	{
		resize(newSize, CollisionHandlingScheme.fromChar(collisionHandlingScheme), EmptyMarkerScheme.fromChar(emptyMarkerScheme));
	}
	
	public CollisionHandlingScheme getCollisionHandlingScheme()
	{
		return collisionHandler.getType();
	}
	
	public EmptyMarkerScheme getEmptyMarkerScheme()
	{
		return emptyMarkerScheme;
	}
	
	/**
	 * Prints the contents of the table to the standard output. Will indicate the key and value of each entry if it exists, and whether the position has been never occupied or formerly occupied.
	 */
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
	
	/**
	 * Changes the table's collision handling scheme. Can only be called explicitly if the table is empty. Otherwise, use the resize method.
	 * @param collisionHandlingType The table's new collision handling scheme.
	 */
	public void setCollisionHandlingScheme(final CollisionHandlingScheme collisionHandlingType)
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
	
	public void setCollisionHandlingScheme(final char collisionHandlingScheme)
	{
		setCollisionHandlingScheme(CollisionHandlingScheme.fromChar(collisionHandlingScheme));
	}
	
	
	/**
	 * Changes the table's empty marker scheme.
	 * @param emptyMarkerScheme The new empty marker scheme of the table. Must be the character 'A', 'N', or 'R'.
	 * @return True if and only if the old scheme and the new scheme are different.
	 */
	public boolean setEmptyMarkerScheme(final char emptyMarkerScheme)
	{
		return setEmptyMarkerScheme(EmptyMarkerScheme.fromChar(emptyMarkerScheme));
	}
	
	
	/**
	 * Changes the table's empty marker scheme.
	 * @param emptyMarkerScheme The new empty marker scheme of the table.
	 * @return True if and only if the old scheme and the new scheme are different.
	 */
	public boolean setEmptyMarkerScheme(final EmptyMarkerScheme emptyMarkerScheme)
	{
		final boolean changedScheme = this.emptyMarkerScheme != emptyMarkerScheme;
		
		if (changedScheme)
		{
			for (int i = 0; i < positions.length; i++)	// Iterate through the positions in the table, replacing with new empty markers as appropriate
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
							rollBack(i);
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
	
	
	/**
	 * Increments the number of elements counter by one
	 */
	private void addElement()
	{
		addElements(1);
	}
	
	
	/**
	 * Increments or decrements the number of elements counter by a specified amount. Updates the load factor afterwards.
	 * @param change The number to add to the number of elements counter.
	 */
	private void addElements(final int change)
	{
		numElements += change;
		updateLoadFactor();
	}
	
	
	/**
	 * Re-computes the load factor (ratio of number of elements to total size). Checks that this is not more than the total laod factor afterwards. 
	 * @return The updated load factor.
	 */
	private void updateLoadFactor()
	{
		loadFactor = ((double) numElements) / size();
		checkLoadFactor();
	}
	
	/**
	 * Checks if the load factor is at least the rehash threshold. If so, expands the hash table.
	 */
	private void checkLoadFactor()
	{
		if (loadFactor >= rehashThreshold)
		{
			expandTable();
		}
	}
	
	
	/**
	 * Expands the table by a the rehash factor or number, whichever was last set.
	 */
	private void expandTable()
	{
		int newSize;
		if (expandByFactor)
		{
			newSize = (int) (size() * rehashFactor);
		}
		else
		{
			newSize = size() + rehashNumber;
		}
		resize(newSize);
	}
	
	
	/**
	 * Checks if the hash table is empty.
	 * @return True if and only if the table has no elements stored.
	 */
	public boolean isEmpty()
	{
		return numElements == 0;
	}
	
	
	/**
	 * Checks if a position at a given index is empty, whether or not it has ever been occupied.
	 * @param index The index of the position to check.
	 * @return True if and only if the position is not filled with an entry.
	 */
	public boolean positionIsEmpty(final int index)
	{		
		return (positions[index] == null) || positionIsFormerlyOccupied(index);
	}
	
	
	/**
	 * Checks if a position at a given index is but has been formerly occupied.
	 * @param index The index of the position to check.
	 * @return True if and only if the position is available but has been previously occupied.
	 */
	public boolean positionIsFormerlyOccupied(final int index)
	{		
		return (positions[index] != null)
			&& ((emptyMarkerScheme == EmptyMarkerScheme.AVAILABLE && positions[index].isAvailablePosition())
			|| (emptyMarkerScheme == EmptyMarkerScheme.NEGATIVE && positions[index].get() != null && positions[index].get().getKey().charAt(0) == '-'));
	}
	
	
	/**
	 * Returns the total size of the hash table.
	 * @return The size of the hash table.
	 */
	public int size()
	{
		return positions.length;
	}
	
	/**
	 * Computes the total number of collisions each entry has endured.
	 * @return The total number of collisions over all entries in the hash table.
	 */
	public int getTotalCollisions()
	{
		int totalCollisions = 0;
		
		for (int i = 0; i < positions.length; i++)
		{
			if (!positionIsEmpty(i))
			{
				totalCollisions += positions[i].get().getCollisions();
			}
		}
		
		return totalCollisions;
	}
	
	/**
	 * Computes the total number of entries that have been collided with.
	 * @return The total number of entries that have been collided with.
	 */
	public int getNumberOfCollidedEntries()
	{
		int totalCollided = 0;
		
		for (int i = 0; i < positions.length; i++)
		{
			if (!positionIsEmpty(i) && positions[i].get().getCollisions() > 0)
			{
				totalCollided++;
			}
		}
		
		return totalCollided;
	}
	
	/**
	 * Computes the average number of collisions over all entries that have endured a collision.
	 * @return The average number of collisions.
	 */
	public double getAverageCollisions()
	{
		final double numberOfCollidedEntries = getNumberOfCollidedEntries();
		double averageCollisions;
		
		if (numberOfCollidedEntries == 0)
		{
			averageCollisions = 0;
		}
		else
		{
			averageCollisions = ((double) getTotalCollisions()) / getNumberOfCollidedEntries();
		}
		
		return averageCollisions;
	}
	
	/**
	 * Computes the greatest number of collisions any entry has endured.
	 * @return The maximum number of collisions.
	 */
	public int getMaxCollisions()
	{
		int maxCollisions = 0;
		
		for (int i = 0; i < positions.length; i++)
		{
			if (!positionIsEmpty(i) && positions[i].get().getCollisions() > maxCollisions)
			{
				maxCollisions = positions[i].get().getCollisions();
			}
		}
		
		return maxCollisions;
	}
	
	/**
	 * Prints a summary of the hash table's statistics.
	 * Includes the current rehash threshold, expansion factor/number, collision handling scheme, empty marker scheme, table size, number of elements,
	 * load factor, total number of collisions, maximum number of collisions, and average number of collisions over all entries.
	 */
	public void printHashTableStatistics()
	{
		System.out.println("--------HASH TABLE STATISTICS--------");
		System.out.println("Rehash threshold: " + rehashThreshold);
		if (expandByFactor)
		{
			System.out.println("Expanding by factor");
			System.out.println("Rehash factor: " + rehashFactor);
		}
		else
		{
			System.out.println("Expanding by number of cells");
			System.out.println("Rehash number: " + rehashNumber);
		}
		System.out.println("Collision handling scheme: " + collisionHandler.getType());
		System.out.println("Empty marker scheme: " + emptyMarkerScheme);
		System.out.println();
		System.out.println("Size: " + size());
		System.out.println("Number of elements: " + numElements);
		System.out.println("Load factor: " + loadFactor);
		System.out.println();
		System.out.println("Total collisions: " + getTotalCollisions());
		System.out.println("Maximum collisions for single cell: " + getMaxCollisions());
		System.out.println("Average collisions over all collided cells: " + getAverageCollisions());
		System.out.println("Total collision rate: " + ((double) getTotalCollisions()) / numElements);
		System.out.println();
	}
	
	/**
	 * Resets the tracked number of collisions over all entries in the table.
	 */
	public void resetHashTableStatistics()
	{
		for (int i = 0; i < positions.length; i++)
		{
			if (!positionIsEmpty(i))
			{
				positions[i].get().resetCollisions();
			}
		}
	}
}