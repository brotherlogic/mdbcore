package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;

public class GroopTest extends TestCase {
	public GroopTest() {
		super();
		Connect.setForDevMode();
	}

	public void testGroop() {
		try {
			// Create
			Groop g = new Groop("TestGroop", "TestGroop");

			// Persist
			g.save();

			// Retrieve
			Groop g2 = GetGroops.build().getGroop("TestGroop");

			// Test
			assert (g2.equals(g));

			// Make a new groop and test for collisions
			Groop g3 = new Groop("TestGroop", "TestGroop");
			g3.save();
			Groop g4 = GetGroops.build().getGroop("TestGroop");

			// Check
			assert (g3.equals(g4));
			assert (g3.equals(g2));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
