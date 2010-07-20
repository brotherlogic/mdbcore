package uk.co.brotherlogic.mdb;

import java.sql.SQLException;
import java.util.Date;

import uk.co.brotherlogic.mdb.categories.Category;
import uk.co.brotherlogic.mdb.format.Format;
import uk.co.brotherlogic.mdb.label.Label;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class CatNoTest {
	public void testCatNo() {
		Record r = new Record();
		r.setAuthor("fake-author");
		r.setCategory(new Category("fake-cat", 12));
		r.setCatNo("fake-cat-no");
		r.setDate(new Date());
		r.setDiscogsNum(12);
		r.setFormat(new Format("fake-format", "12"));
		r.setLabel(new Label("fake-label"));
		r.setNotes("fake-notes");
		r.setOwner(1);
		r.setPrice(12.65);
		r.setReleaseMonth(12);
		r.setReleaseType(1);
		r.setTitle("catno-test");
		r.setYear(2000);

		r.addCatNo("Donkey");

		try {
			r.save();
			Record r2 = GetRecords.create().getRecords("catno-test").get(0);
			assert (r2.getCatNos().size() == 2);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}
}
