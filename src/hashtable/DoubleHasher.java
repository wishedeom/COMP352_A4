package hashtable;

import prime.Prime;

class DoubleHasher extends CollisionHandler
{
	private int q;	// Prime number less than size of table
	
	DoubleHasher(final HashTable implementingTable)
	{
		q = Prime.nextSmallestPrime(implementingTable.size());
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
	
	public CollisionHandlingType getType()
	{
		return CollisionHandlingType.DOUBLE;
	}
}