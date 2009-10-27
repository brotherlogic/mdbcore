package uk.co.brotherlogic.mdb.format;

/**
 * Class to represent a format
 * @author Simon Tucker
 */

import java.sql.SQLException;

/**
 * Represents a format
 * 
 * @author sat
 * 
 */
public class Format implements Comparable<Format>
{
	/** The name of the format */
	private final String name;

	/** The base name of the format */
	private final String baseFormat;

	/** The id number of this format */
	private int formatNumber;

	/** Flag indicating if this format has been changed */
	private final boolean formatUpdated = false;

	public Format()
	{
		name = "";
		baseFormat = "";
		formatNumber = -1;
	}

	public Format(int num, String sIn, String base)
	{
		name = sIn;
		formatNumber = num;
		baseFormat = base;
	}

	public Format(int num, String sIn, String base, Format categoryCopy)
	{
		name = sIn;
		formatNumber = num;
		baseFormat = base;
	}

	public int compareTo(Format o)
	{
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Format)
			return compareTo((Format) o) == 0;
		else
			return false;
	}

	public String fullString()
	{
		String out = formatNumber + ": " + name + "\n";
		return out;
	}

	public String getBaseFormat()
	{
		return baseFormat;
	}

	public String getName()
	{
		return name;
	}

	public int getNumber()
	{
		return formatNumber;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public int save() throws SQLException
	{
		System.out.println(formatNumber + " => " + formatUpdated);
		if (formatNumber == -1 || formatUpdated)
			formatNumber = GetFormats.create().save(this);

		return formatNumber;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
