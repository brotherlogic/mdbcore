package uk.co.brotherlogic.mdb.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.User;

public class DatabaseTidier
{
   public static void main(String[] args) throws SQLException
   {
      DatabaseTidier tidier = new DatabaseTidier();
      tidier.tidy();
   }

   public void tidy() throws SQLException
   {
      // Make sure we don't break anything
      Connect.setForDevMode();
      removeNonUserRecords();
   }

   public void removeIrrelevantArtists() throws SQLException
   {
      List<Integer> nonPersonnel = new LinkedList<Integer>();
      String sql = "select * from artist LEFT JOIN personnel ON artist_id = artistnumber WHERE artistnumber IS NULL";
      PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next())
      {
         Integer val = rs.getInt(1);
         nonPersonnel.add(val);
      }
      rs.close();
      ps.close();

      List<Integer> nonGroups = new LinkedList<Integer>();
      String sql2 = "select * from artist LEFT JOIN lineupdetails ON artist_id = artistnumber WHERE artistnumber IS NULL LIMIT 5;";
   }

   public void removeNonUserRecords() throws SQLException
   {
      Collection<User> users = User.getUsers();
      Collection<Integer> recordNumbers = GetRecords.create().getRecordNumbers();
      Collection<Integer> toBeDeleted = new LinkedList<Integer>();
      for (Integer recNumber : recordNumbers)
      {
         Record r = GetRecords.create().getRecord(recNumber);
         boolean delete = true;
         for (User user : users)
            if (user.getID() == r.getOwner())
               delete = false;
         if (delete)
            toBeDeleted.add(r.getNumber());
      }

      System.out.println("Deleting " + toBeDeleted.size() + " records");
      for (Integer rec : toBeDeleted)
      {
         Record r = GetRecords.create().getRecord(rec);
         GetRecords.create().deleteRecord(r);
      }
   }
}
