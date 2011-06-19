package uk.co.brotherlogic.mdb.utils;

import java.sql.SQLException;
import java.util.Collection;

import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class Checker
{
   public static void main(String[] args) throws SQLException
   {
      Checker mine = new Checker();
      mine.runChecks();
   }

   private void checkDiscogs() throws SQLException
   {
      Collection<Record> records = GetRecords.create().getAllRecords();
      for (Record rec : records)
         System.out.println(rec.getNumber() + " - " + rec.getDiscogsNum());
   }

   public void runChecks() throws SQLException
   {
      checkDiscogs();
   }
}
