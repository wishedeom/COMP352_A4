package hashtable;

import java.util.Random;
import prime.Prime;

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