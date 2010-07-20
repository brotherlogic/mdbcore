package uk.co.brotherlogic.mdb.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;

public class RecordScore {
	Record rec;

	boolean[] changed = new boolean[1];
	private static int SIMON_RANK_CHANGED = 0;

	public static RecordScore get(Record rec) throws SQLException {
		String sql = "SELECT * from score_table WHERE record_id = ?";
		PreparedStatement ps = Connect.getConnection()
				.getPreparedStatement(sql);
		ps.setInt(1, rec.getNumber());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			RecordScore score = new RecordScore();
			score.simonRank = rs.getDouble("simon_rank");
			score.rec = rec;
			return score;
		} else
			throw new SQLException("Record " + rec + " not found!");
	}

	private double simonRank;

	public void save() throws SQLException {
		if (rec != null) {
			StringBuffer sql = new StringBuffer("UPDATE score_table ");

			for (int i = 0; i < changed.length; i++)
				if (changed[i])
					if (i == 0)
						sql.append("SET simon_rank = ?");

			sql.append(" WHERE record_id = ?");

			PreparedStatement ps = Connect.getConnection()
					.getPreparedStatement(sql.toString());

			int sCount = 1;
			for (int i = 0; i < changed.length; i++)
				if (changed[i]) {
					if (i == 0)
						ps.setDouble(sCount, simonRank);
					sCount++;
				}

			ps.setInt(sCount, rec.getNumber());
			System.out.println(ps);

			ps.execute();
			ps.close();

			// Update the record to ranked
			rec.save();
		}
	}

	public void setSimonRank(double rank) {
		System.out.println("Setting rank: " + rank);
		simonRank = rank;
		changed[SIMON_RANK_CHANGED] = true;
	}
}
