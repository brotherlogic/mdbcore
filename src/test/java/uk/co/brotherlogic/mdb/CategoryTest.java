package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.categories.Category;
import uk.co.brotherlogic.mdb.categories.GetCategories;

public class CategoryTest extends TestCase
{
	/**
	 * Tests that all the category stuff is working properly
	 */
	public void testSave()
	{
		try
		{
			//Create an artist
			Category cat = new Category("test-category", 12);

			//Persist it
			cat.save();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Tests that all the category stuff is working properly
	 */
	public void testSaveAndRetrieveCategory()
	{
		try
		{
			//Create an artist
			Category cat = new Category("test-category-saveandret", 12);

			//Persist it
			cat.save();

			//Retrieve it
			Category cat2 = GetCategories.build().getCategory("test-category-saveandret");

			//Check that everything worked
			assert (cat.equals(cat2));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Tests that all the category stuff is working properly
	 */
	public void testSaveAndRetrieveMp3NumberCategory()
	{
		try
		{
			//Create an artist
			Category cat = new Category("test-category-saveandretmp3", 12);

			//Persist it
			cat.save();

			//Retrieve it
			Category cat2 = GetCategories.build().getCategory("test-category-saveandretmp3");

			assert (cat2.getMp3Number() == 12);
		}
		catch (SQLException e)
		{
			System.err.println("SQL Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
