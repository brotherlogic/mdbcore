package uk.co.brotherlogic.mdb;

import java.sql.SQLException;
import java.util.LinkedList;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;
import uk.co.brotherlogic.mdb.groop.LineUp;

public class LineUpTest extends TestCase
{
	public void testLineUp()
	{
		try
		{
			//Create a groop
			Groop g = new Groop("lutgroop", "lutgroop");

			//Create some artists
			Artist a1 = Artist.build("artist1");
			Artist a2 = Artist.build("artist2");

			LineUp lineup = new LineUp(g);
			lineup.addArtist(a1);
			lineup.addArtist(a2);

			g.save();
			lineup.save();

			//Retrieve the groop
			System.err.println("Getting groops");
			Groop g2 = GetGroops.build().getGroop("lutgroop");
			System.err.println(g2.getLineUps().size());
			LinkedList<LineUp> lineups = new LinkedList<LineUp>(g2.getLineUps());
			assert (lineups.size() == 1);

			LineUp lineup2 = lineups.get(0);
			LinkedList<Artist> artists = new LinkedList<Artist>(lineup2.getArtists());
			assert (artists.size() == 2);
			assert (artists.get(0).equals(a1) || artists.get(0).equals(a2));
			assert (artists.get(0).equals(a1) || artists.get(0).equals(a2));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
