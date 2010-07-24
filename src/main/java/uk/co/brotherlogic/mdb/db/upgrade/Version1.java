package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

/**
 * Version 1 has no db_props table
 * 
 * @author simon
 * 
 */
public class Version1 extends Version {

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean runLocal() throws SQLException {
		// Create the version table
		String sql = "CREATE TABLE db_props(version integer)";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.execute();
		return true;
	}

}
