<<<<<<< HEAD
package tests.prime_tests;

import static org.junit.Assert.*;

import org.junit.Test;

import prime.Prime;

public class PrimeTest
{

	@Test
	public void TestNextLargestPrime()
	{
		int p = Prime.nextLargestPrime(2);
		assertEquals(2, p);
		
		p = Prime.nextLargestPrime(3);
		assertEquals(3, p);
		
		p = Prime.nextLargestPrime(4);
		assertEquals(5, p);
		
		p = Prime.nextLargestPrime(6);
		assertEquals(7, p);
		
		p = Prime.nextLargestPrime(123434);
		assertEquals(123439, p);
	}
	
	@Test
	public void TextNextSmallestPrime()
	{
		int p = Prime.nextSmallestPrime(2);
		assertEquals(2, p);
		
		p = Prime.nextSmallestPrime(3);
		assertEquals(3, p);
		
		p = Prime.nextSmallestPrime(4);
		assertEquals(3, p);
		
		p = Prime.nextSmallestPrime(6);
		assertEquals(5, p);
		
		p = Prime.nextSmallestPrime(123434);
		assertEquals(123433, p);
	}

}
=======
package tests.prime_tests;

import static org.junit.Assert.*;

import org.junit.Test;

import prime.Prime;

public class PrimeTest
{

	@Test
	public void TestNextLargestPrime()
	{
		int p = Prime.nextLargestPrime(2);
		assertEquals(2, p);
		
		p = Prime.nextLargestPrime(3);
		assertEquals(3, p);
		
		p = Prime.nextLargestPrime(4);
		assertEquals(5, p);
		
		p = Prime.nextLargestPrime(6);
		assertEquals(7, p);
		
		p = Prime.nextLargestPrime(123434);
		assertEquals(123439, p);
	}
	
	@Test
	public void TextNextSmallestPrime()
	{
		int p = Prime.nextSmallestPrime(2);
		assertEquals(2, p);
		
		p = Prime.nextSmallestPrime(3);
		assertEquals(3, p);
		
		p = Prime.nextSmallestPrime(4);
		assertEquals(3, p);
		
		p = Prime.nextSmallestPrime(6);
		assertEquals(5, p);
		
		p = Prime.nextSmallestPrime(123434);
		assertEquals(123433, p);
	}

}
>>>>>>> 8b0c43fb4a8475e4e2a171057958252400ad2fc9
