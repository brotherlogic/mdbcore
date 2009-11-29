package uk.co.brotherlogic.mdb.format;

/**
 * Class to deal with getting formats
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.categories.Category;
import uk.co.brotherlogic.mdb.categories.GetCategories;

public class GetFormats
{
	public static GetFormats create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetFormats();
		return singleton;
	}

	// Maps format name to format
	Collection<Format> formats;

	Set<String> baseFormats;

	private static GetFormats singleton;

	private GetFormats() throws SQLException
	{
		// Set the required parameters
		formats = new LinkedList<Format>();
	}

	private void fillAll() throws SQLException
	{
		// Get a statement and run the query
		PreparedStatement s = Connect.getConnection().getPreparedStatement(
				"SELECT FormatName,FormatNumber,baseformat  FROM Formats");
		ResultSet rs = s.executeQuery();

		baseFormats = new TreeSet<String>();

		// Fill the set
		while (rs.next())
		{
			// Construct the new format
			Format temp = new Format(rs.getInt(2), rs.getString(1), rs.getString(3));

			baseFormats.add(temp.getBaseFormat());
			formats.add(temp);
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Collection<String> getBaseFormats()
	{
		if (baseFormats == null)
			try
			{
				fillAll();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

		return baseFormats;
	}

	public Collection<Category> getCategories(int num) throws SQLException
	{
		// Vector to be returned
		Vector<Category> ret = new Vector<Category>();

		PreparedStatement s = Connect
				.getConnection()
				.getPreparedStatement(
						"SELECT DISTINCT CategoryName,CategoryNumber FROM Categories, Records WHERE Categories.CategoryNumber = Records.category AND format = ?");
		s.setInt(1, num);
		ResultSet rs = s.executeQuery();

		while (rs.next())
			ret.add(GetCategories.build().getCategory(rs.getString(1)));

		rs.close();
		return ret;
	}

	public Format getFormat(int formatNumber) throws SQLException
	{
		// Get a statement and run the query
		PreparedStatement s = Connect.getConnection().getPreparedStatement(
				"SELECT formatname,baseformat FROM Formats WHERE formatnumber = ?");
		s.setInt(1, formatNumber);
		ResultSet rs = s.executeQuery();

		Format toReturn = null;
		if (rs.next())
			toReturn = new Format(formatNumber, rs.getString(1), rs.getString(2));

		rs.close();
		return toReturn;
	}

	public Format getFormat(String formatName) throws SQLException
	{
		// Get a statement and run the query
		PreparedStatement s = Connect.getConnection().getPreparedStatement(
				"SELECT formatnumber,baseformat FROM Formats WHERE formatname = ?");
		s.setString(1, formatName);
		ResultSet rs = s.executeQuery();

		Format toReturn = null;
		if (rs.next())
			toReturn = new Format(rs.getInt(1), formatName, rs.getString(2));

		rs.close();
		return toReturn;
	}

	public Collection<Format> getFormats()
	{
		try
		{
			if (formats.size() == 0)
				fillAll();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return formats;
	}

	public int save(Format in) throws SQLException
	{
		// Add the new format and commit the update
		PreparedStatement ps = Connect.getConnection().getPreparedStatement(
				"INSERT INTO formats (formatname, baseformat) VALUES (?,?)");
		ps.setString(1, in.getName());
		ps.setString(2, in.getBaseFormat());
		ps.execute();

		// Get the new format number
		PreparedStatement ps2 = Connect.getConnection().getPreparedStatement(
				"SELECT FormatNumber FROM Formats WHERE FormatName = ?");
		ps2.setString(1, in.getName());
		ResultSet rs = ps2.executeQuery();
		rs.next();

		int val = rs.getInt(1);

		// Close the database objects
		rs.close();

		return val;

	}
}
