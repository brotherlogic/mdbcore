package uk.co.brotherlogic.mdb.label;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;

import uk.co.brotherlogic.mdb.record.Record;

/**
 * Class to represent a label
 * 
 * @author Simon Tucker
 */
public class Label implements Comparable<Label>, Serializable
{
	/**
	 * The label name
	 */
	private final String labelName;

	/** The label number */
	private int labNo;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the label
	 */
	public Label(final String name)
	{
		this(name, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the label
	 * @param number
	 *            The number of the label
	 */
	public Label(final String name, final int number)
	{
		labelName = name;
		labNo = number;
	}

	public final int compareTo(final Label o)
	{
		return -labelName.toLowerCase().compareTo(o.labelName.toLowerCase());
	}

	@Override
	public final boolean equals(final Object o)
	{
		if (o instanceof Label)
			return this.compareTo((Label) o) == 0;
		else
			return false;
	}

	/**
	 * Gets the name of the label
	 * 
	 * @return The name of the label
	 */
	public final String getName()
	{
		return labelName;
	}

	/**
	 * Gets the associated records of this label
	 * 
	 * @return The collection of records that have this label
	 * @throws SQLException
	 *             If the DB connection fails
	 */
	public final Collection<Record> getRecords() throws SQLException
	{
		return GetLabels.create().getRecords(labNo);
	}

	@Override
	public final int hashCode()
	{
		return labelName.hashCode();
	}

	/**
	 * Saves the label to the DB
	 * 
	 * @throws SQLException
	 *             if the DB connection fails
	 */
	public final int save() throws SQLException
	{
		if (labNo == -1)
			labNo = GetLabels.create().addLabel(this);
		return labNo;
	}

	@Override
	public final String toString()
	{
		return labelName;
	}
}
