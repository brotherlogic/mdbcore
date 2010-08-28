package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.categories.Category;
import uk.co.brotherlogic.mdb.categories.GetCategories;

public class CategoryTest extends TestCase {
	public CategoryTest() {
		super();
		Connect.setForDevMode();
	}

	/**
	 * Tests that all the category stuff is working properly
	 */
	public void testSaveAndRetrieveCategory() {
		try {
			// Create an artist
			Category cat = new Category("test-category-saveandret", 12);

			// Persist it
			cat.save();

			// Retrieve it
			Category cat2 = GetCategories.build().getCategory(
					"test-category-saveandret");

			// Check that everything worked
			assert (cat.equals(cat2));
			assert (cat2.getMp3Number() == 12);

			// Create a new category with the same name
			Category cat3 = new Category("test-category-saveandret", 12);
			cat3.save();
			Category cat4 = GetCategories.build().getCategory(
					"test-category-saveandret");
			assert (cat3.equals(cat4));
			assert (cat.equals(cat4));
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}

}
