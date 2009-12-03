package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.label.GetLabels;
import uk.co.brotherlogic.mdb.label.Label;

public class LabelTest extends TestCase
{
	/**
	 * Test function for the label object
	 */
	public void testLabel()
	{
		try
		{
			//Create a label
			Label lab = new Label("test-label");

			//Persist
			lab.save();

			//Retrieve
			Label lab2 = GetLabels.create().getLabel("test-label");
			assert (lab2.equals(lab));

			//Build again and add
			Label lab3 = new Label("test-label");
			lab3.save();
			Label lab4 = GetLabels.create().getLabel("test-label");
			assert (lab4.equals(lab3));
			assert (lab4.equals(lab2));

		}
		catch (SQLException e)
		{

		}
	}
}
