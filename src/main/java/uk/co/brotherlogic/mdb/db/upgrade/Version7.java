package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

/**
 * Adds in a possible parent id
 * 
 * @author simon
 * 
 */
public class Version7 extends Version
{

	/** THe version number for this upgrade */
	private static final int VERSION_NUMBER = 7;

	@Override
	public final int getVersion()
	{
		return VERSION_NUMBER;
	}

	@Override
	public final boolean runLocal() throws SQLException
	{

		// Add a column to represent the physical track number
		String sql = "ALTER TABLE records add column parent integer";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.execute();
		ps.close();

		// Update all the records to balance this track number
		String sqlr = "UPDATE records set parent = -1";
		PreparedStatement psr = Connect.getConnection().getPreparedStatement(
				sqlr);
		psr.execute();
		psr.close();

		return true;
	}
}