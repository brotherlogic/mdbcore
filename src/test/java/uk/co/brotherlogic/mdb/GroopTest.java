package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;

public class GroopTest extends TestCase
{
	public void testGroop()
	{
		try
		{
			//Create
			Groop g = new Groop("TestGroop", "TestGroop");

			//Persist
			g.save();

			//Retrieve
			Groop g2 = GetGroops.build().getGroop("TestGroop");

			//Test
			assert (g2.equals(g));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
