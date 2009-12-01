package uk.co.brotherlogic.mdb.groop;

/**
 * Class to deal with getting groops
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.Utils;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.artist.GetArtists;

public class GetGroops
{
	private static Map<Integer, Groop> groopMap = new TreeMap<Integer, Groop>();

	public static GetGroops build() throws SQLException
	{
		if (singleton == null)
			singleton = new GetGroops();

		return singleton;
	}

	public static void main(String[] args)
	{
		try
		{
			Map<String, Groop> gMap = GetGroops.build().getGroopMap();
			int count = 0;
			for (Groop grp : gMap.values())
				if (grp.getShowName() == null || grp.getShowName().equals("null"))
				{
					System.err.println("GROOP = " + grp.getNumber());
					System.err.println("SHOW = " + grp.getShowName());
					System.err.println("SORT = " + grp.getSortName());

					grp.setShowName(Utils.flipString(grp.getSortName()));

					System.err.println("SHOW = " + grp.getShowName());
					System.err.println("SORT = " + grp.getSortName());

					grp.save();
					System.err.println(grp);
					count++;

				}

			System.err.println("Done " + count);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Maps groopnumber to Groop
	Map<String, Groop> groops;

	// Temporary store of groop name -> lineup
	Map<String, Groop> tempStore;

	PreparedStatement updateState;
	PreparedStatement addGroop;
	PreparedStatement getGroop;

	private static GetGroops singleton = null;

	private GetGroops() throws SQLException
	{
		// Set the required parameters
		tempStore = new TreeMap<String, Groop>();
		groops = new TreeMap<String, Groop>();

		updateState = Connect.getConnection().getPreparedStatement(
				"UPDATE groops SET sort_name = ?, show_name = ? WHERE groopnumber = ?");
		addGroop = Connect.getConnection().getPreparedStatement(
				"INSERT INTO groops (show_name,sort_name) VALUES (?,?)");
		getGroop = Connect.getConnection().getPreparedStatement(
				"SELECT groopnumber FROM groops WHERE show_name = ? AND sort_name = ?");
	}

	public int addGroop(Groop grp) throws SQLException
	{
		addGroop.setString(1, grp.getShowName());
		addGroop.setString(2, grp.getSortName());
		addGroop.execute();

		getGroop.setString(1, grp.getShowName());
		getGroop.setString(2, grp.getSortName());
		ResultSet rs = getGroop.executeQuery();

		int grpNumber = -1;
		if (rs.next())
			grpNumber = rs.getInt(1);

		return grpNumber;
	}

	public int addLineUp(LineUp lineup) throws SQLException
	{
		Groop in = lineup.getGroop();
		// Get the groop number
		int groopNumber = in.getNumber();
		if (groopNumber < 1)
		{
			//Save the groop
			in.save();
			groopNumber = in.getNumber();
		}

		// Get the lineup number
		if (lineup.getLineUpNumber() == -1)
			return saveLineUp(lineup);
		else
			return lineup.getLineUpNumber();

	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void commitGroops()
	{
		Iterator<String> kIt = tempStore.keySet().iterator();
		while (kIt.hasNext())
		{
			// Get the groop name
			String groopName = kIt.next();
			Groop grp = tempStore.get(groopName);

			// Get the full groop
			if (!groops.keySet().contains(groopName))
				groops.put(groopName, grp);
			else
				(groops.get(groopName)).addLineUps((tempStore.get(groopName)).getLineUps());
		}
		tempStore.clear();
	}

	public void execute() throws SQLException
	{
		PreparedStatement ss = Connect.getConnection().getPreparedStatement(
				"Select Count(sort_name) FROM Groops");
		ResultSet rss = ss.executeQuery();
		rss.next();
		rss.close();
		ss.close();

		// Initialise the groop store
		groops = new TreeMap<String, Groop>();

		// Get the bare bones of the groops
		String sql = "SELECT sort_name, show_name, GroopNumber from Groops";
		PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
		ps.execute();
		ResultSet rs = ps.getResultSet();
		while (rs.next())
		{
			String sortName = rs.getString(1);
			String showName = rs.getString(2);
			int groopNumber = rs.getInt(3);

			Groop fGroop = new Groop(sortName, showName, groopNumber);
			groops.put(sortName, fGroop);
		}
	}

	public Collection<Groop> getData()
	{
		return groops.values();
	}

	public Groop getGroop(int num) throws SQLException
	{
		// Get the groop name
		PreparedStatement s = Connect.getConnection().getPreparedStatement(
				"SELECT sort_name, show_name FROM Groops WHERE GroopNumber = ?");
		s.setInt(1, num);
		ResultSet rs = s.executeQuery();

		if (rs.next())
		{
			Groop ret = new Groop(rs.getString(1), rs.getString(2), num);
			rs.close();

			// Cache the groop
			groopMap.put(ret.getNumber(), ret);

			return ret;
		}
		else
			return null;
	}

	public Groop getGroop(String groopName)
	{
		if (groops.containsKey(groopName))
			return groops.get(groopName);
		else
			// Construct the groop with the required groop name
			return new Groop(groopName, Utils.flipString(groopName));
	}

	public Map<String, Groop> getGroopMap()
	{
		if (groops.size() == 0)
			try
			{
				execute();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		return groops;
	}

	public Groop getSingleGroop(int num) throws SQLException
	{
		// Get a statement and run the query
		String sql = "SELECT Groops.GroopNumber, groops.sort_name, groops.show_name, LineUp.LineUpNumber, ArtistNumber FROM Groops,LineUp,LineUpDetails WHERE Groops.groopnumber = ? AND Groops.GroopNumber = LineUp.GroopNumber AND LineUp.LineUpNumber = LineUpDetails.LineUpNumber ORDER BY sort_name, LineUp.LineUpNumber ASC";
		PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
		ps.setInt(1, num);
		ps.execute();
		ResultSet rs = ps.getResultSet();

		Groop currGroop = null;
		LineUp currLineUp = null;
		while (rs.next())
		{
			// Read the info
			int groopNumber = rs.getInt(1);
			String sortName = rs.getString(2);
			String showName = rs.getString(3);
			int lineUpNumber = rs.getInt(4);
			int artistNumber = rs.getInt(5);

			if (currGroop == null)
			{
				// Construct the current groop and line up
				currGroop = new Groop(sortName, showName, groopNumber, new TreeSet<LineUp>());
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(), currGroop);
				currLineUp.addArtist(GetArtists.create().getArtist(artistNumber));
			}
			else if (!sortName.equals(currGroop.getSortName()))
			{
				// Add the groop and create a new one
				// Ensure that we add the last lineUp
				currGroop.addLineUp(currLineUp);

				// Construct the current groop and line up
				currGroop = new Groop(sortName, showName, groopNumber, new TreeSet<LineUp>());
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(), currGroop);
				currLineUp.addArtist(GetArtists.create().getArtist(artistNumber));

			}
			else if (currLineUp.getLineUpNumber() != lineUpNumber)
			{
				// Add the line up
				currGroop.addLineUp(currLineUp);

				// Construct the new line up
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(), currGroop);
				currLineUp.addArtist(GetArtists.create().getArtist(artistNumber));
			}
			else
				currLineUp.addArtist(GetArtists.create().getArtist(artistNumber));
		}

		currGroop.addLineUp(currLineUp);

		groopMap.put(currGroop.getNumber(), currGroop);
		return currGroop;
	}

	public void save(Groop g) throws SQLException
	{
		updateState.setString(1, g.getSortName());
		updateState.setString(2, g.getShowName());
		updateState.setInt(3, g.getNumber());

		updateState.execute();
	}

	public int saveLineUp(LineUp lup) throws SQLException
	{
		// Initialise the return value
		int ret = 0;
		Groop grp = lup.getGroop();

		// Check to see if this lineup already exists
		Collection<LineUp> currentLineups = grp.getLineUps();
		for (LineUp lineUp : currentLineups)
			if (lineUp.equals(lup))
				return lineUp.getLineUpNumber();

		//Add the lineup - step 1, add the lineup to get the lineup number
		PreparedStatement ps = Connect.getConnection().getPreparedStatement(
				"INSERT INTO lineup (groopnumber) VALUES (?)");
		ps.setInt(1, grp.getNumber());
		ps.execute();

		PreparedStatement psg = Connect.getConnection().getPreparedStatement(
				"SELECT lineupnumber FROM lineup ORDER BY lineup DESC LIMIT 1");
		ResultSet rs = psg.executeQuery();

		if (!rs.next())
			return -1;
		int lineupNumber = rs.getInt(1);

		//Now add the details
		PreparedStatement psa = Connect.getConnection().getPreparedStatement(
				"INSERT INTO lineupdetails(lineupnumber,artistnumber) VALUE (?,?)");
		for (Artist art : lup.getArtists())
		{
			psa.setInt(1, lineupNumber);
			psa.setInt(2, art.getId());
			psa.execute();
		}

		return ret;
	}

	public String toString()
	{
		return "Groops";
	}
}
