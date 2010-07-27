package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

public class Version4 extends Version {

	@Override
	public int getVersion() {
		return 4;
	}

	@Override
	public boolean runLocal() throws SQLException {

		// Add a random column into the record table
		String sql = "ALTER TABLE records add column recrand float";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.execute();
		ps.close();

		// Update all the records to have a random element
		String sqlr = "UPDATE records set recrand = random()";
		PreparedStatement psr = Connect.getConnection().getPreparedStatement(
				sqlr);
		psr.execute();
		psr.close();

		return true;
	}
}
