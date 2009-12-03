package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.artist.GetArtists;

public class ArtistTest extends TestCase
{

	public void testArtist()
	{
		try
		{
			//Create an artist
			Artist art = new Artist("Test Artist");

			//Persist it
			art.save();

			//Retrieve it
			Artist art2 = GetArtists.create().getArtist("Test Artist");

			assert (art.equals(art2));
			assert (art.getId() > 0);

			//Try saving it again
			Artist art3 = new Artist("Test Artist");
			art3.save();
			Artist art4 = GetArtists.create().getArtist("Test Artist");

			assert (art4.equals(art3));
			assert (art4.equals(art));

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
