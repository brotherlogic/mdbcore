package uk.co.brotherlogic.mdb;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.db.upgrade.Version;

public class DBUpgrade {

	private static Integer getDBVersion() {
		String sql = "SELECT version from db_props ORDER BY version DESC LIMIT 1";
		try {
			PreparedStatement ps = Connect.getConnection()
					.getPreparedStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				Connect.getConnection().cancelTrans();
				System.err.println("Cancelled failing version check");
			} catch (SQLException e2) {
				System.err.println("Something serious has gone down");
				e2.printStackTrace();
			}
		}

		return 1;
	}

	public static void main(String[] args) {
		DBUpgrade.upgradeDB();
	}

	public static void upgradeDB() {
		int version = getDBVersion();

		// Look for an upgrade
		String className = "uk.co.brotherlogic.mdb.db.upgrade.Version"
				+ version;
		try {
			@SuppressWarnings("unchecked")
			Class<Version> cls = (Class<Version>) Class.forName(className);
			Version v = cls.getConstructor(new Class[0]).newInstance(
					new Object[0]);
			if (v != null && v.run()) {
				Connect.getConnection().commitTrans();
				upgradeDB();
			} else {
				System.err.println("Unable to upgrade - force exit");
				System.exit(1);
			}
		} catch (ClassNotFoundException e) {
			// We can safely ignore this one
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getNextException().printStackTrace();
		}
	}
}
