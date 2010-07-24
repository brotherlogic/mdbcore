package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

/**
 * Version 2 adds the user table
 * 
 * @author simon
 * 
 */
public class Version2 extends Version {

	@Override
	public int getVersion() {
		return 2;
	}

	@Override
	public boolean runLocal() throws SQLException {

		// Create the user table
		String sql = "CREATE TABLE user_table (user_id serial PRIMARY KEY, name varchar(100))";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.execute();

		// Add simon and jeanette
		String sql_person = "INSERT INTO user_table (name) VALUES (?)";
		PreparedStatement ps_person = Connect.getConnection()
				.getPreparedStatement(sql_person);
		ps_person.setString(1, "Simon");
		ps_person.addBatch();
		ps_person.setString(1, "Jeanette");
		ps_person.addBatch();
		ps_person.executeBatch();

		return true;

	}
}
