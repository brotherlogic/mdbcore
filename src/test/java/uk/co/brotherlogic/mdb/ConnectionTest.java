package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;

public class ConnectionTest extends TestCase
{
	public void testDevelopment()
	{
		//Can we connect to the development database
		try
		{
			Connect con = Connect.getConnection();

			//We should be able to reach this point
			assertTrue(con != null);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
