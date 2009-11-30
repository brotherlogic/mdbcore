package uk.co.brotherlogic.mdb.groop;

/**
 * Class to represent a full groop with all the lineups
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import uk.co.brotherlogic.mdb.Utils;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.artist.GetArtists;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class Groop implements Comparable<Groop>
{
	// Groop properties
	private String sortName = "";
	private String showName = "";
	private int groopNumber;
	private Collection<LineUp> lineUps = null;

	private boolean updated = false;

	public Groop()
	{

	}

	public Groop(String sortName)
	{
		this(sortName, Utils.flipString(sortName));
	}

	public Groop(String sortName, String showName)
	{
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = -1;
		lineUps = new LinkedList<LineUp>();
	}

	public Groop(String sortName, String showName, int num)
	{
		// Set the variables
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = num;
	}

	public Groop(String sortName, String showName, int num, Collection<LineUp> lineUps)
	{
		// Set the variables
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = num;
		this.lineUps = new Vector<LineUp>();
		this.lineUps.addAll(lineUps);
	}

	public void addLineUp(LineUp in)
	{
		if (lineUps == null)
			lineUps = new LinkedList<LineUp>();
		lineUps.add(in);
	}

	public void addLineUps(Collection<LineUp> lineUpsToAdd)
	{
		if (lineUps == null)
			lineUps = new LinkedList<LineUp>();
		lineUps.addAll(lineUpsToAdd);
	}

	public Groop build(String name)
	{
		return new Groop(name, Utils.flipString(name), -1);
	}

	@Override
	public int compareTo(Groop o)
	{
		return -sortName.toLowerCase().compareTo(o.sortName.toLowerCase());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Groop)
			return this.compareTo((Groop) o) == 0;
		else
			return false;
	}

	private void fillLineUp()
	{
		try
		{
			this.lineUps = GetGroops.build().getSingleGroop(this.groopNumber).getLineUps();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Collection<Artist> getAllMembers()
	{
		Set<Artist> allMembers = new TreeSet<Artist>();
		boolean first = true;

		for (LineUp lineUp : getLineUps())
			allMembers.addAll(lineUp.getArtists());

		return allMembers;
	}

	public Collection<Record> getAuthoredRecords() throws SQLException
	{
		return GetRecords.create().getRecordsWithAuthor(this.showName);
	}

	public Collection<Artist> getCoreMembers()
	{
		Set<Artist> coreMembers = new TreeSet<Artist>();
		boolean first = true;

		for (LineUp lineUp : getLineUps())
			if (first)
			{
				// Add all the artists
				coreMembers.addAll(lineUp.getArtists());
				first = false;
			}
			else
			{
				Collection<Artist> toRemove = new LinkedList<Artist>();
				for (Artist artist : coreMembers)
					if (!lineUp.getArtists().contains(artist))
						toRemove.add(artist);
				coreMembers.removeAll(toRemove);
			}

		return coreMembers;
	}

	public LineUp getLineUp(int in)
	{
		if (lineUps == null || lineUps.size() == 0)
			fillLineUp();

		LineUp ret = null;
		// Move and iterator to the right point
		boolean found = false;
		Iterator<LineUp> it = lineUps.iterator();
		while (!found && it.hasNext())
		{
			LineUp temp = it.next();
			if (temp.getLineUpNumber() == in)
			{
				ret = temp;
				found = true;
			}
		}

		return ret;
	}

	public Collection<LineUp> getLineUps()
	{
		if (lineUps == null || lineUps.size() == 0)
			fillLineUp();
		return lineUps;
	}

	public int getNoLineUps()
	{
		return lineUps.size();
	}

	public int getNumber()
	{
		return groopNumber;
	}

	public Collection<Record> getPersRecords() throws SQLException
	{
		List<Record> persRecords = new LinkedList<Record>();

		// Get the artist
		Artist art = GetArtists.create().getArtist(this.sortName);
		persRecords.addAll(art.getRecords());

		return persRecords;
	}

	public String getShowName()
	{
		return showName;
	}

	public String getSimpRep()
	{
		return "G" + groopNumber;
	}

	public String getSortName()
	{
		return sortName;
	}

	public Collection<Record> getUnauthoredRecords() throws SQLException
	{
		return GetRecords.create().getRecordsFeaturingGroop(this.showName, this.groopNumber);
	}

	@Override
	public int hashCode()
	{
		return sortName.hashCode();
	}

	public void save() throws SQLException
	{
		if (updated)
			GetGroops.build().save(this);
	}

	public void setLineUps(Collection<LineUp> lineUpsIn)
	{
		// Clear and add lineUpsIn
		lineUps.clear();
		lineUps.addAll(lineUpsIn);
	}

	public void setNumber(int in)
	{
		groopNumber = in;
	}

	public void setShowName(String groopIn)
	{
		showName = groopIn;
		updated = true;
	}

	// Set methods
	public void setSortName(String groopIn)
	{
		sortName = groopIn;
		updated = true;
	}

	@Override
	public String toString()
	{
		return sortName;
	}

}
