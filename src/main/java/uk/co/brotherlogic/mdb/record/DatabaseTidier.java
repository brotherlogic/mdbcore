package uk.co.brotherlogic.mdb.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.User;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.artist.GetArtists;

public class DatabaseTidier
{
   public static void main(String[] args) throws SQLException
   {
      // Make sure we don't break anything
      Connect.setForDevMode();
      DatabaseTidier tidier = new DatabaseTidier();
      tidier.tidy();
   }

   public void tidy() throws SQLException
   {
      removeNonUserRecords();
      removeIrrelevantArtists();

      // Commit all the changes
      Connect.getConnection().commitTrans();
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

      System.out.println("Found " + nonPersonnel.size() + " non personnel artists");

      List<Integer> nonGroups = new LinkedList<Integer>();
      String sql2 = "select * from artist LEFT JOIN lineupdetails ON artist_id = artistnumber WHERE artistnumber IS NULL";
      PreparedStatement ps2 = Connect.getConnection().getPreparedStatement(sql2);
      ResultSet rs2 = ps2.executeQuery();
      while (rs2.next())
         nonGroups.add(rs2.getInt(1));
      rs.close();
      ps.close();

      System.out.println("Found " + nonGroups.size() + " non group artists");

      List<Integer> overall = new LinkedList<Integer>();
      for (Integer intV : nonPersonnel)
         if (nonGroups.contains(intV))
            overall.add(intV);

      System.out.println("Found " + overall.size() + " artists to be deleted");
      for (Integer value : overall)
      {
         Artist art = GetArtists.create().getArtist(value);
         System.out.println("Deleting " + art.getShowName());
         GetArtists.create().deleteArtist(art);
      }
   }

   public void removeIrrelevantGroops() throws SQLException
   {
      // Doesn't really seem to be any irrelevant groops?
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
         System.out.println("Deleting " + r.getAuthor() + " - " + r.getTitle());
         GetRecords.create().deleteRecord(r);
      }
   }
}
