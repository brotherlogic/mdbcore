package uk.co.brotherlogic.mdb.db.upgrade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import uk.co.brotherlogic.mdb.Connect;

/**
 * Version 3 adds the new score table
 * 
 * @author simon
 * 
 */
public class Version3 extends Version {

	@Override
	public int getVersion() {
		return 3;
	}

	@Override
	public boolean runLocal() throws SQLException {
		String sql = "CREATE TABLE score_history (score_id serial PRIMARY KEY, "
				+ "record_id integer references records(recordnumber),"
				+ "user_id integer references user_table(user_id),"
				+ "score_date timestamp," + "score_value integer)";

		Connect.getConnection().getPreparedStatement(sql).execute();

		// Now copy over all the old scores
		int simon_id = -1;
		int jeanette_id = -1;
		String usql = "SELECT user_id,name from user_table";
		ResultSet urs = Connect.getConnection().getPreparedStatement(usql)
				.executeQuery();
		while (urs.next())
			if (urs.getString(2).equals("Simon"))
				simon_id = urs.getInt(1);
			else
				jeanette_id = urs.getInt(1);
		urs.close();

		String asql = "INSERT INTO score_history (record_id,user_id,score_value,score_date) VALUES (?,?,?,?)";
		PreparedStatement aps = Connect.getConnection().getPreparedStatement(
				asql);

		String qsql = "SELECT record_id, jeanette_score,jeanette_score_date,simon_score,simon_score_date,simon_rank_count from score_table";
		ResultSet rs = Connect.getConnection().getPreparedStatement(qsql)
				.executeQuery();
		while (rs.next()) {
			int record_id = rs.getInt(1);

			// Deal with jeanette's stuff
			int jeanette_score = rs.getInt(2);
			Timestamp jeanette_date = rs.getTimestamp(3);
			if (jeanette_date != null && jeanette_date.getTime() > 0) {
				aps.setInt(1, record_id);
				aps.setInt(2, jeanette_id);
				aps.setInt(3, jeanette_score);
				aps.setTimestamp(4, jeanette_date);
				aps.addBatch();
			}

			// Deal with my stuff
			int simon_score = rs.getInt(4);
			Timestamp simon_date = rs.getTimestamp(5);
			aps.setInt(1, record_id);
			aps.setInt(2, simon_id);
			aps.setInt(3, simon_score);
			aps.setTimestamp(4, simon_date);
			aps.addBatch();

			if (rs.getInt(6) > 1)
				aps.addBatch();
		}
		rs.close();
		aps.executeBatch();

		return true;
	}
}
