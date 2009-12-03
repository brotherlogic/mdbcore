package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.format.Format;
import uk.co.brotherlogic.mdb.format.GetFormats;

public class FormatTest extends TestCase
{
	public void testFormat()
	{
		try
		{
			//Create
			Format form = new Format("TestFormat", "12");

			//Persist
			form.save();

			//Retrieve
			Format form2 = GetFormats.create().getFormat("TestFormat");

			assert (form.equals(form2));
			assert (form2.getBaseFormat().equals("12"));

			//Try re-adding the same format
			Format form3 = new Format("TestFormat", "12");
			form3.save();

			//Retrieve and check
			Format form4 = GetFormats.create().getFormat("TestFormat");
			assert (form4.equals(form3));
			assert (form4.equals(form));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			assert (false);
		}
	}
}
