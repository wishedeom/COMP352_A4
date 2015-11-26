package hashtable;

/**
 * Stores strings as keys and values for use in a hash map.
 * @author Michael Deom
 *
 */

class KeyValuePair
{	
	private Key key;
	private String value;
	
	public KeyValuePair(final String key, final String value)
	{
		setKey(key);
		setValue(value);
	}
	
	public String getKey()
	{
		return key.toString();
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setKey(final String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException ("Key must not be null.");
		}
		
		this.key = new Key(key);
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
		return key.hashCode();
	}
	
	public String toString()
	{
		return "(" + key + ", " + value + ")";
	}
}

class Key
{
	public static final int HASH_BASE = 33;	// Try 33, 37, 39, 41; products of few primes
	
	private String key;
	
	public Key(final String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key cannot be null.");
		}
		
		this.key = key;
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
	
	public String toString()
	{
		return key;
	}
}