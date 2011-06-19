package uk.co.brotherlogic.mdb.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import uk.co.brotherlogic.mdb.parsers.DiscogParser;
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
      DiscogParser dParser = new DiscogParser();
      Collection<Record> records = GetRecords.create().getAllRecords();
      for (Record rec : records)
         try
         {
            if (rec.getDiscogsNum() > 0)
            {
               Record cRecord = dParser.parseDiscogRelease(rec.getDiscogsNum());
               compare(cRecord, rec);
               System.out.println(rec.getNumber() + " - " + rec.getDiscogsNum());
            }
         }
         catch (IOException e)
         {
            System.err.println("Unable to parse: " + rec.getDiscogsNum());
         }
   }

   private void compare(Record r1, Record r2)
   {
      if (!r1.getTitle().equals(r2.getTitle()))
         System.out.println("MISMATCH TITLES: " + r1.getTitle() + " and " + r2.getTitle());
   }

   public void runChecks() throws SQLException
   {
      checkDiscogs();
   }
}
