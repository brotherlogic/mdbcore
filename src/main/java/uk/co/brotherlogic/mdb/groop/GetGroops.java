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
import java.util.Vector;

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
		if (groopNumber < 1 && groops.containsKey(in.getSortName()))
			groopNumber = (groops.get(in.getSortName())).getNumber();
		if (!(groopNumber > 0))
			if (tempStore.containsKey(in.getSortName()))
				groopNumber = (tempStore.get(in.getSortName())).getNumber();
			else
			{
				// Add the groop name and get the number
				PreparedStatement ps = Connect.getConnection().getPreparedStatement(
						"INSERT INTO Groops (sort_name,show_name) VALUES (?,?)");
				ps.setString(1, in.getSortName());
				ps.setString(2, in.getShowName());
				ps.execute();

				// Now get the group number
				PreparedStatement s = Connect.getConnection().getPreparedStatement(
						"SELECT GroopNumber FROM Groops WHERE sort_name = ?");
				s.setString(1, in.getSortName());
				ResultSet rs = s.executeQuery();
				rs.next();

				// Set the groop number
				groopNumber = rs.getInt(1);
				rs.close();
				s.close();
			}

		// Set the number in the groop
		in.setNumber(groopNumber);

		// Get the lineup number
		int lineupNum = -1;
		if (lineup.getLineUpNumber() == -1)
			lineupNum = saveLineUp(lineup);
		else
			lineupNum = lineup.getLineUpNumber();

		return lineupNum;

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
		// Get the line ups for this groop
		Collection<LineUp> lineups = new Vector<LineUp>();

		if (GetGroops.build().getGroop(grp.getSortName()) != null)
			lineups = GetGroops.build().getGroop(grp.getSortName()).getLineUps();

		// Work through each line up
		Iterator<LineUp> lIt = lineups.iterator();
		while (lIt.hasNext())
		{
			// Compare the two artist sets
			LineUp tempLineUp = lIt.next();
			Collection<Artist> arts = tempLineUp.getArtists();

			if (arts.containsAll(lup.getArtists()) && lup.getArtists().containsAll(arts))
				ret = tempLineUp.getLineUpNumber();
		}

		// Search the temporary store if we haven't found the value yet
		if (tempStore.containsKey(grp.getSortName()) && ret < 1)
		{

			Groop tempGroop = tempStore.get(grp.getSortName());

			Iterator<LineUp> tLupIt = tempGroop.getLineUps().iterator();
			while (tLupIt.hasNext())
			{
				// Compare the two artist sets
				LineUp tempLineUp = tLupIt.next();
				Collection<Artist> arts = tempLineUp.getArtists();

				if (arts.containsAll(lup.getArtists()) && lup.getArtists().containsAll(arts))
					ret = tempLineUp.getLineUpNumber();
			}
		}

		// If we haven't seen this line up before
		if (ret < 1)
		{
			// Add the line up

			// First register the new line up and retrieve the number
			PreparedStatement ps = Connect.getConnection().getPreparedStatement(
					"INSERT INTO LineUp (GroopNumber) VALUES (?)");
			ps.setInt(1, grp.getNumber());
			ps.execute();

			// Now select the lineup ID which has a line up containing zero
			// artists (i.e....)
			PreparedStatement s = Connect
					.getConnection()
					.getPreparedStatement(
							"SELECT DISTINCT LineUp.LineUpNumber FROM LineUp LEFT JOIN LineUpDetails ON LineUp.LineUpNumber = LineUpDetails.LineUpNumber WHERE (((LineUpDetails.LineUpNumber) Is Null))");
			ResultSet rs = s.executeQuery();

			// Grab the first entry
			rs.next();
			ret = rs.getInt(1);

			// Close the statements
			rs.close();
			s.close();

			// Now add the artists
			int[] artNums = GetArtists.create().addArtists(lup.getArtists());

			for (int artNum : artNums)
			{
				PreparedStatement lps = Connect.getConnection().getPreparedStatement(
						"INSERT INTO LineUpDetails (LineUpNumber,ArtistNumber) VALUES (?,?)");
				lps.setInt(1, ret);
				lps.setInt(2, artNum);
				lps.execute();
			}

			// Construct the new full groop if required
			if (!tempStore.containsKey(grp.getSortName()))
			{
				Groop newGrp = new Groop(grp.getSortName(), grp.getShowName(), grp.getNumber(),
						new Vector<LineUp>());
				tempStore.put(newGrp.getSortName(), newGrp);
			}

			// Add the new lineup
			Groop tempGrp = tempStore.get(grp.getSortName());
			tempGrp.addLineUp(new LineUp(ret, lup.getArtists(), tempGrp));
		}

		return ret;

	}

	public String toString()
	{
		return "Groops";
	}
}
