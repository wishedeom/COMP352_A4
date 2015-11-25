package hashtable;
/**
 * Stores strings as keys and values for use in a hash map.
 * @author Michael Deom
 *
 */

public class KeyValuePair
{
	public static final int HASH_BASE = 33;	// Try 33, 37, 39, 41; products of few primes
	
	private String key;
	private String value;
	
	public KeyValuePair(final String key, final String value)
	{
		if (key == null || value == null)
		{
			throw new IllegalArgumentException ("Key and value must not be null.");
		}
		
		this.key = key;
		this.value = value;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(final String value)
	{
		if (null == value)
		{
			throw new IllegalArgumentException ("Value must not be null.");
		}
		
		this.value = value;
	}
	
	public int hashCode()
	{
		int code = 0;
		
		for (int i = key.length() - 1; i >= 0; i--)
		{
			code = key.charAt(i) + code * HASH_BASE;	// Horner's rule for evaluating polynomials in O(n) time
		}
		
		return code;
	}
	
	public boolean equals(final Object o)
	{
		boolean isEqual = false;
		
		if (o.getClass() == this.getClass())
		{
			final KeyValuePair k = (KeyValuePair) o;
			isEqual = k.key.equals(this.key) && k.value.equals(this.value);
		}
		
		return isEqual;
	}
	
	public String toString()
	{
		return "(" + key + ", " + value + ")";
	}
}