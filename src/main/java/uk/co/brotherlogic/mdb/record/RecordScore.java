package uk.co.brotherlogic.mdb.record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.User;

public class RecordScore
{
   public static void add(Record rec, User user, int score) throws SQLException
   {
      if (rec != null && user != null)
      {
         String sql = "INSERT INTO score_history (record_id,user_id,score_date,score_value) VALUES (?,?,now(),?)";

         PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql.toString());

         ps.setInt(1, rec.getNumber());
         ps.setInt(2, user.getID());
         ps.setInt(3, score);

         ps.execute();
         ps.close();

         // Update the record to ranked
         rec.save();
      }
   }

   public static double get(Record rec) throws SQLException
   {
      double scoreVal = 0.0;
      int count = 0;
      for (User user : User.getUsers())
      {
         double score = get(rec, user);
         if (score >= 0)
         {
            scoreVal += get(rec, user);
            count++;
         }
      }

      if (count > 0)
         return scoreVal / count;
      else
         return 0.0;
   }

   public static double get(Record rec, User user) throws SQLException
   {
      String sql = "SELECT score_value from score_history WHERE record_id = ? AND user_id = ?";
      PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
      ps.setInt(1, rec.getNumber());
      ps.setInt(2, user.getID());
      ResultSet rs = ps.executeQuery();
      int count = 0;
      double sum = 0;
      if (rs.next())
      {
         count++;
         sum += rs.getInt(1);
      }

      if (count == 0)
         return -1;
      else
         return sum / count;
   }

   public static int getCount(Record rec, User user) throws SQLException
   {
      String sql = "SELECT count(score_value) from score_history WHERE record_id = ? AND user_id = ?";
      PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
      ps.setInt(1, rec.getNumber());
      ps.setInt(2, user.getID());
      ResultSet rs = ps.executeQuery();
      int count = 0;
      if (rs.next())
         count = rs.getInt(1);
      rs.close();
      ps.close();

      return count;
   }

   public static long getLastScoreDate(Record rec, User user) throws SQLException
   {
      String sql = "SELECT score_date from score_history WHERE record_id = ? AND user_id = ? ORDER BY score_date DESC LIMIT 1";
      PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
      ps.setInt(1, rec.getNumber());
      ps.setInt(2, user.getID());
      ResultSet rs = ps.executeQuery();
      long count = 0;
      if (rs.next())
         count = rs.getTimestamp(1).getTime();
      rs.close();
      ps.close();

      return count;
   }
}
