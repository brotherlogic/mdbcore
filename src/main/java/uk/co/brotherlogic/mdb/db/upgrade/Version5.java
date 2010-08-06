package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

public class Version5 extends Version {

	@Override
	public int getVersion() {
		return 5;
	}

	@Override
	public boolean runLocal() throws SQLException {

		// Add a column to represent the physical track number
		String sql = "ALTER TABLE track add column formtrack integer";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.execute();
		ps.close();

		// Update all the records to balance this track number
		String sqlr = "UPDATE track set formtrack = tracknumber";
		PreparedStatement psr = Connect.getConnection().getPreparedStatement(
				sqlr);
		psr.execute();
		psr.close();

		return true;
	}
}
