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
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.brotherlogic.mdb.Cache;
import uk.co.brotherlogic.mdb.Connect;
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

	// Maps groopnumber to Groop
	Map<String, Groop> groops;

	// Temporary store of groop name -> lineup
	Map<String, Groop> tempStore;

	PreparedStatement updateState;
	PreparedStatement addGroop;
	PreparedStatement getGroop;

	private static GetGroops singleton = null;

	private final Cache<Groop> grpCache = new Cache<Groop>();

	private GetGroops() throws SQLException
	{
		// Set the required parameters
		tempStore = new TreeMap<String, Groop>();
		groops = new TreeMap<String, Groop>();

		updateState = Connect
				.getConnection()
				.getPreparedStatement(
						"UPDATE groops SET sort_name = ?, show_name = ? WHERE groopnumber = ?");
		addGroop = Connect.getConnection().getPreparedStatement(
				"INSERT INTO groops (show_name,sort_name) VALUES (?,?)");
		getGroop = Connect
				.getConnection()
				.getPreparedStatement(
						"SELECT groopnumber FROM groops WHERE show_name = ? AND sort_name = ?");
	}

	public int addGroop(Groop grp) throws SQLException
	{
		getGroop.setString(1, grp.getShowName());
		getGroop.setString(2, grp.getSortName());
		ResultSet rs = getGroop.executeQuery();

		if (!rs.next())
		{
			addGroop.setString(1, grp.getShowName());
			addGroop.setString(2, grp.getSortName());
			Connect.getConnection().executeStatement(addGroop);

			getGroop.setString(1, grp.getShowName());
			getGroop.setString(2, grp.getSortName());
			rs = getGroop.executeQuery();
			rs.next();
		}

		int grpNumber = -1;
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
			// Save the groop
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
				(groops.get(groopName)).addLineUps((tempStore.get(groopName))
						.getLineUps());
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
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
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

		Groop groop = grpCache.get(num);

		if (groop == null)
		{
			// Get the groop name
			PreparedStatement s = Connect
					.getConnection()
					.getPreparedStatement(
							"SELECT sort_name, show_name FROM Groops WHERE GroopNumber = ?");
			s.setInt(1, num);
			ResultSet rs = s.executeQuery();

			if (rs.next())
			{
				groop = new Groop(rs.getString(1), rs.getString(2), num);
				rs.close();

				// Cache the groop
				grpCache.add(num, groop);
			}
		}

		return groop;
	}

	public Groop getGroop(String sortName) throws SQLException
	{
		// Get the groop name
		PreparedStatement s = Connect
				.getConnection()
				.getPreparedStatement(
						"SELECT groopnumber, show_name FROM Groops WHERE sort_name = ?");
		s.setString(1, sortName);
		ResultSet rs = s.executeQuery();

		if (rs.next())
		{
			Groop ret = new Groop(sortName, rs.getString(2), rs.getInt(1));
			rs.close();

			// Cache the groop
			groopMap.put(ret.getNumber(), ret);

			return ret;
		} else
			return null;
	}

	public Groop getGroopFromShowName(String sortName) throws SQLException
	{
		// Get the groop name
		PreparedStatement s = Connect
				.getConnection()
				.getPreparedStatement(
						"SELECT groopnumber, sort_name FROM Groops WHERE show_name = ?");
		s.setString(1, sortName);
		ResultSet rs = s.executeQuery();

		if (rs.next())
		{
			Groop ret = new Groop(sortName, rs.getString(2), rs.getInt(1));
			rs.close();

			// Cache the groop
			groopMap.put(ret.getNumber(), ret);

			return ret;
		} else

			return null;
	}

	public Map<String, Groop> getGroopMap()
	{
		if (groops.size() == 0)
			try
			{
				execute();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		return groops;
	}

	public Collection<LineUp> getLineUps(Groop grp) throws SQLException
	{
		Collection<LineUp> lineups = new LinkedList<LineUp>();

		// Get a statement and run the query
		String sql = "SELECT LineUp.LineUpNumber, ArtistNumber FROM LineUp,LineUpDetails WHERE LineUp.GroopNumber = ? AND LineUp.LineUpNumber = LineUpDetails.LineUpNumber ORDER BY LineUp.LineUpNumber ASC";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.setInt(1, grp.getNumber());
		ps.execute();
		ResultSet rs = ps.getResultSet();

		LineUp currLineUp = null;
		while (rs.next())
		{
			// Read the info
			int lineUpNumber = rs.getInt(1);
			int artistNumber = rs.getInt(2);

			if (currLineUp == null)
			{
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(),
						grp);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			} else if (currLineUp.getLineUpNumber() != lineUpNumber)
			{
				// Add the line up
				lineups.add(currLineUp);

				// Construct the new line up
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(),
						grp);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			} else
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
		}
		if (currLineUp != null && currLineUp.getArtists().size() > 0)
			lineups.add(currLineUp);

		return lineups;
	}

	public Groop getSingleGroop(int num) throws SQLException
	{
		// Get a statement and run the query
		String sql = "SELECT Groops.GroopNumber, groops.sort_name, groops.show_name, LineUp.LineUpNumber, ArtistNumber FROM Groops,LineUp,LineUpDetails WHERE Groops.groopnumber = ? AND Groops.GroopNumber = LineUp.GroopNumber AND LineUp.LineUpNumber = LineUpDetails.LineUpNumber ORDER BY sort_name, LineUp.LineUpNumber ASC";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
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
				currGroop = new Groop(sortName, showName, groopNumber,
						new TreeSet<LineUp>());
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(),
						currGroop);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			} else if (!sortName.equals(currGroop.getSortName()))
			{
				// Add the groop and create a new one
				// Ensure that we add the last lineUp
				currGroop.addLineUp(currLineUp);

				// Construct the current groop and line up
				currGroop = new Groop(sortName, showName, groopNumber,
						new TreeSet<LineUp>());
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(),
						currGroop);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));

			} else if (currLineUp.getLineUpNumber() != lineUpNumber)
			{
				// Add the line up
				currGroop.addLineUp(currLineUp);

				// Construct the new line up
				currLineUp = new LineUp(lineUpNumber, new TreeSet<Artist>(),
						currGroop);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			} else
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));

		}
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
		Groop grp = lup.getGroop();

		// Check to see if this lineup already exists
		Collection<LineUp> currentLineups = grp.getLineUps();

		for (LineUp lineUp : currentLineups)
			if (lineUp.equals(lup) && lineUp.getLineUpNumber() >= 0)
				return lineUp.getLineUpNumber();

		// Add the lineup - step 1, add the lineup to get the lineup number
		PreparedStatement ps = Connect.getConnection().getPreparedStatement(
				"INSERT INTO lineup (groopnumber) VALUES (?)");
		ps.setInt(1, grp.getNumber());
		ps.execute();

		PreparedStatement psg = Connect
				.getConnection()
				.getPreparedStatement(
						"SELECT lineupnumber FROM lineup ORDER BY lineupnumber DESC LIMIT 1");
		ResultSet rs = psg.executeQuery();

		if (!rs.next())
			return -1;
		int lineupNumber = rs.getInt(1);

		// Now add the details
		PreparedStatement psa = Connect
				.getConnection()
				.getPreparedStatement(
						"INSERT INTO lineupdetails(lineupnumber,artistnumber) VALUES (?,?)");
		for (Artist art : lup.getArtists())
		{
			art.save();

			psa.setInt(1, lineupNumber);
			psa.setInt(2, art.getId());
			psa.execute();
		}

		return lineupNumber;
	}

	public String toString()
	{
		return "Groops";
	}
}
