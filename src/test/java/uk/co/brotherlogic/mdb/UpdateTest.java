package uk.co.brotherlogic.mdb;

import java.sql.SQLException;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class UpdateTest extends TestCase
{
   public void testUpdateScript()
   {
      try
      {
         int recNumber = GetRecords.create().getRecordNumbers().iterator().next();
         Record r = GetRecords.create().getRecord(recNumber);

         r.fixVersion();

         // Check that the version was set correctly
         Record r2 = GetRecords.create().getRecord(recNumber);
         assert (r2.getVersion().equals(Connect.getConnection().getVersionString()));

         // Update the record in some way
         r2.setTitle("TESTING title");
         r2.save();

         // Check that the version was reset
         Record r3 = GetRecords.create().getRecord(recNumber);
         System.err.println(r2.getVersion() + " and " + r2.getVersion());
         assert (!r3.getVersion().equals(r2.getVersion()));
      }
      catch (SQLException e)
      {
         e.printStackTrace();
         assert (false);
      }
   }
}
