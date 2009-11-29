package uk.co.brotherlogic.mdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class to deal with database connection
 * 
 * @author Simon Tucker
 */
public final class Connect
{
	/**
	 * Static constructor
	 * 
	 * @return A suitable db connection
	 * @throws SQLException
	 *             if a db connection cannot be established
	 */
	public static Connect getConnection() throws SQLException
	{
		if (singleton == null)
			singleton = new Connect();
		return singleton;
	}

	/** The connection to the local DB */
	private Connection locDB;

	/** Singleton instance */
	private static Connect singleton;

	private Connect() throws SQLException
	{
		makeConnection();
	}

	/**
	 * Cancels all impending transactions
	 * 
	 * @throws SQLException
	 *             if the cancel fails
	 */
	public void cancelTrans() throws SQLException
	{
		locDB.rollback();
	}

	/**
	 * Commits the impending transactions
	 * 
	 * @throws SQLException
	 *             If the commit fails
	 */
	public void commitTrans() throws SQLException
	{
		locDB.commit();
	}

	/**
	 * Builds a prepared statements from the data store
	 * 
	 * @param sql
	 *            The statement to build
	 * @return a {@link PreparedStatement}
	 * @throws SQLException
	 *             If the construction fails
	 */
	public PreparedStatement getPreparedStatement(final String sql) throws SQLException
	{
		// Create the statement
		PreparedStatement ps = locDB.prepareStatement(sql);

		return ps;
	}

	/**
	 * Makes the connection to the DB
	 * 
	 * @throws SQLException
	 *             if something fails
	 */
	private void makeConnection() throws SQLException
	{
		try
		{
			// Load all the drivers and initialise the database connection
			Class.forName("org.postgresql.Driver");
			locDB = DriverManager.getConnection("jdbc:postgresql://192.168.1.103/music?user=music");

			// Switch off auto commit
			locDB.setAutoCommit(false);
		}
		catch (ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
	}

}
