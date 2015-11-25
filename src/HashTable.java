import java.util.Random;

public class HashTable
{	
	private enum CollisionHandlingType
	{
		DOUBLE,
		QUADRATIC;
	}
	
	private enum EmptyMarkerScheme
	{
		AVAILABLE,
		NEGATIVE,
		REPLACE;
	}
	
	abstract private class CollisionHandler
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
	
	private class DoubleHasher extends CollisionHandler
	{
		private int q;	// Prime number less than size of table
		
		private DoubleHasher(final HashTable implementingTable)
		{
			q = Prime.nextSmallestPrime(implementingTable.size());
			System.out.println("size = " + implementingTable.size());
			System.out.println("q = " + q);
		}
		
		private int secondaryHash()
		{
			return q - getRawHash() % q;
		}
		
		public int nextHash()
		{
			final int hashCode = getRawHash() + getCounter() * secondaryHash();
			incrementCounter();
			return hashCode;
		}
	}
	
	private class QuadraticProbe extends CollisionHandler
	{
		private static final int DEFAULT_LINEAR_COEFFICIENT = 0;
		private static final int DEFAULT_QUADRATIC_COEFFICIENT = 1;
		
		private int c1;			// Linear coefficient
		private int c2;			// Quadratic coefficient
		
		private QuadraticProbe()
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
	
	public class Compressor
	{
		private int N;	// Size of associated HashTable
		private int p;	// Prime number larger than N
		private int a;	// Pre-modulus multiplier
		private int b;	// Pre-modulus adder
		
		public Compressor(final HashTable hashTable)
		{
			N = hashTable.size();
			p = Prime.nextLargestPrime(N);
			
			Random rand = new Random();
			a = rand.nextInt(p - 1) + 1;	// Random integer in [1, p-1]
			b = rand.nextInt(p);			// Random integer in [0, p-1]
		}
		
		public Compressor(final int N, final int a, final int b)
		{
			if (N <= 0)
			{
				throw new IllegalArgumentException("Size of associated hash table must be a positive integer.");
			}
			
			this.N = N;
			this.p = Prime.nextLargestPrime(N);
			
			if (!(0 < a && a <= p - 1))
			{
				throw new IllegalArgumentException("Compression multiplier must be a positive integer, at most " + p + " (smallest prime larger than hash table size " + N + ").");
			}
			
			if (!(0 <= b && b <= p - 1))
			{
				throw new IllegalArgumentException("Compression adder must be a non-negative integer, at most " + p + " (smallest prime larger than hash table size " + N + ").");
			}
			
			this.a = a;
			this.b = b;
		}
		
		public int compress(final int hashCode)
		{
			return ((a * hashCode + b) % p % N);
		}
	}
	
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
			throw new RuntimeException("Hash table is ass2ass.");
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
	
	public void get(final String key)
	{
		final KeyValuePair
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