package uk.co.brotherlogic.mdb.categories;

import java.sql.SQLException;

/**
 * Class to represent a category
 * 
 * @author Simon Tucker
 */
public class Category implements Comparable<Category>
{
	/** The name of the category */
	private final String catName;

	/** The id number of the category */
	private int catNumber;

	/** The associated MP3 category */
	private final int mp3Number;

	/**
	 * Constructor
	 */
	public Category()
	{
		catName = "";
		catNumber = -1;
		mp3Number = -1;
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the category
	 * @param number
	 *            The ID number of the category
	 * @param mp3
	 *            The MP3 number of the category
	 */
	public Category(final String name, final int number, final int mp3)
	{
		catName = name;
		catNumber = number;
		mp3Number = mp3;
	}

	@Override
	public final int compareTo(final Category o)
	{
		return this.toString().compareTo(o.toString());
	}

	@Override
	public final boolean equals(final Object o)
	{
		if (o instanceof Category)
			return this.compareTo((Category) o) == 0;
		else
			return false;
	}

	/**
	 * Get method for the Category
	 * 
	 * @return {@link String} of the Category name
	 */
	public final String getCatName()
	{
		return catName;
	}

	/**
	 * Get method for the MP3 number
	 * 
	 * @return {@link int} of the category number
	 */
	public final int getMp3Number()
	{
		return mp3Number;
	}

	@Override
	public final int hashCode()
	{
		return catName.hashCode();
	}

	/**
	 * Save method
	 */
	public final int save() throws SQLException
	{
		if (catNumber == -1)
			catNumber = GetCategories.build().addCategory(this);

		return catNumber;
	}

	@Override
	public final String toString()
	{
		return catName;
	}
}
