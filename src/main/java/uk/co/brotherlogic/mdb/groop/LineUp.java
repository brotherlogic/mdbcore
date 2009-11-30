package uk.co.brotherlogic.mdb.groop;

/**
 * Class to represent a line up
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import uk.co.brotherlogic.mdb.artist.Artist;

/**
 * A LineUp is a manifestation of a groop
 * 
 * @author sat
 * 
 */
public class LineUp implements Comparable<LineUp>
{
	/** The number of this lineup */
	private int lineUpNumber;

	/** The artists in this lineup */
	private final Collection<Artist> artists;

	/** The groop associated with the linup */
	private final Groop grp;

	/**
	 * Constructor
	 */
	public LineUp()
	{
		lineUpNumber = -1;
		artists = new Vector<Artist>();
		grp = null;
	}

	/**
	 * Constrcutor
	 * @param grp The groop of which this is a line up
	 */
	public LineUp(Groop grp)
	{
		lineUpNumber = -1;
		this.grp = grp;
		artists = new LinkedList<Artist>();
	}

	/**
	 * Constructor
	 * 
	 * @param number
	 *            The Lineup number
	 * @param arts
	 *            The artists in the lineup
	 * @param groop
	 *            The groop for the lineup
	 */
	public LineUp(final int number, final Collection<Artist> arts, final Groop groop)
	{
		grp = groop;
		lineUpNumber = number;
		artists = new LinkedList<Artist>(arts);
	}

	/**
	 * Add an artist to the lineup
	 * 
	 * @param art
	 *            The artist to add
	 */
	public final void addArtist(final Artist art)
	{
		artists.add(art);
	}

	@Override
	public final int compareTo(final LineUp o)
	{
		return this.toString().compareTo(o.toString());
	}

	@Override
	public final boolean equals(final Object o)
	{
		if (o instanceof LineUp)
			return this.compareTo((LineUp) o) == 0;
		else
			return false;
	}

	/**
	 * Gets the artists for this lineup
	 * 
	 * @return A Collection of the artists for this lineup
	 */
	public final Collection<Artist> getArtists()
	{
		return artists;
	}

	/**
	 * Gets the groop for this lineup
	 * 
	 * @return The groop associated with this lineup
	 */
	public final Groop getGroop()
	{
		return grp;
	}

	/**
	 * Gets the number of this lineup
	 * 
	 * @return The lineup number of this lineup
	 */
	public final int getLineUpNumber()
	{
		return lineUpNumber;
	}

	@Override
	public final int hashCode()
	{
		return grp.getShowName().hashCode() + lineUpNumber;
	}

	public int save() throws SQLException
	{
		lineUpNumber = GetGroops.build().addLineUp(this);
		return lineUpNumber;
	}

	@Override
	public final String toString()
	{
		return "" + lineUpNumber + ": " + artists;
	}
}
