package prime;
public final class Prime
{
	private static final int INITIAL_SIZE = 10;
	private static final int EXPANSION_FACTOR = 2;
	private static final int EMPTY_INDEX = -1;
	private static final int FIRST_PRIME = 2;
	
	private static int[] primes;
	private static int lastIndex;
	
	static
	{
		primes = new int[INITIAL_SIZE];
		lastIndex = EMPTY_INDEX;
	}
	
	private Prime() {}
	
	public static int nextLargestPrime(final int n)
	{
		generatePrimes(n);		
		return findNextLargestPrime(n);
	}
	
	public static int nextSmallestPrime(final int n)
	{
		generatePrimes(n);		
		return findNextSmallestPrime(n);
	}
	
	private static void generatePrimes(final int n)
	{
		while (isEmpty() || lastPrime() <= n)
		{
			addNextPrime();
		}
	}
	
	private static void addNextPrime()
	{
		if (isEmpty())
		{
			add(FIRST_PRIME);
		}
		else
		{
			int candidate = lastPrime() + 1;
			while (!isPrime(candidate))
			{
				candidate++;
			}
			add(candidate);			
		}
	}
	
	private static boolean isEmpty()
	{
		return lastIndex == EMPTY_INDEX;
	}
	
	private static int lastPrime()
	{
		return primes[lastIndex];
	}
	
	private static boolean isPrime(final int n)
	{
		boolean indivisible = true;
		
		int i = 0;
		while(indivisible && i <= lastIndex && primes[i] * primes[i] <= n)
		{
			indivisible = n % primes[i] != 0;
			i++;
		}
		
		return indivisible;
	}
	
	private static void add(final int newPrime)
	{
		lastIndex++;
		
		if (lastIndex >= primes.length)
		{
			expandPrimeList();
		}
		
		primes[lastIndex] = newPrime;
	}
	
	private static void expandPrimeList()
	{
		int[] newPrimes = new int[primes.length * EXPANSION_FACTOR];
		
		for (int i = 0; i < lastIndex; i++)
		{
			newPrimes[i] = primes[i];
		}
		
		primes = newPrimes;
	}
	
	// Improve into binary search; too lazy right now
	private static int findNextLargestPrime(final int n)
	{
		int i = 0;
		while (primes[i] <= n)
		{
			i++;
		}
		return primes[i];
	}
	
	private static int findNextSmallestPrime(final int n)
	{
		int i = primes.length - 1;
		while (primes[i] == 0 || primes[i] >= n)
		{
			i--;
		}
		return primes[i];
	}
}