package uk.co.brotherlogic.mdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class User
{

   public static User getUser(int id)
   {
      try
      {
         String sql = "SELECT name from user_table where user_id = ?";
         PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
         ps.setInt(1, id);
         ResultSet rs = ps.executeQuery();
         while (rs.next())
            return new User(id, rs.getString(1));
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   public static User getUser(String id)
   {
      try
      {
         String sql = "SELECT user_id from user_table where lower(name) = ?";
         PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
         ps.setString(1, id.toLowerCase());
         ResultSet rs = ps.executeQuery();
         while (rs.next())
            return new User(rs.getInt(1), id);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }

      return null;
   }

   public static Collection<User> getUsers()
   {
      List<User> users = new LinkedList<User>();
      try
      {
         String sql = "SELECT user_id,name from user_table";
         PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
         ResultSet rs = ps.executeQuery();
         while (rs.next())
            users.add(new User(rs.getInt(1), rs.getString(2)));
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }

      return users;
   }

   private final int id;

   private final String name;

   public User(int id, String name)
   {
      this.id = id;
      this.name = name;
   }

   public int getID()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }
}
