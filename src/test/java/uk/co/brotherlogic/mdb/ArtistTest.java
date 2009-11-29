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
			Artist art = Artist.build("Test Artist");

			//Persist it
			art.save();

			//Retrieve it
			Artist art2 = GetArtists.create().getArtist("Test Artist");

			assert (art.equals(art2));
			assert (art.getId() > 0);

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
