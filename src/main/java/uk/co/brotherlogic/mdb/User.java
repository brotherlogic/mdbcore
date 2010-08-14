package uk.co.brotherlogic.mdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

	public static User getUser(int id) {
		try {
			String sql = "SELECT name from user_table where user_id = ?";
			PreparedStatement ps = Connect.getConnection()
					.getPreparedStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				return new User(id, rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static User getUser(String id) {
		try {
			String sql = "SELECT user_id from user_table where name = ?";
			PreparedStatement ps = Connect.getConnection()
					.getPreparedStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				return new User(rs.getInt(1), id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private final int id;

	public String getName() {
		return name;
	}

	private final String name;

	public User(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return id;
	}
}
