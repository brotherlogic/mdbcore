package uk.co.brotherlogic.mdb.label;

/**
 * Class to deal with getting groops
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.co.brotherlogic.mdb.Connect;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class GetLabels
{
   private static GetLabels singleton;

   public static GetLabels create() throws SQLException
   {
      if (singleton == null)
         singleton = new GetLabels();
      return singleton;
   }

   PreparedStatement collectQuery;

   PreparedStatement getQuery;

   PreparedStatement insertQuery;

   // Map of labelName --> Label
   Map<String, Label> labels;

   private GetLabels() throws SQLException
   {
      // Initialise the set
      labels = new TreeMap<String, Label>();
      insertQuery = Connect.getConnection().getPreparedStatement(
            "INSERT INTO Labels (LabelName) VALUES (?)");
      collectQuery = Connect.getConnection().getPreparedStatement(
            "SELECT LabelNumber FROM Labels WHERE LabelName = ?");
      getQuery = Connect.getConnection().getPreparedStatement(
            "SELECT LabelName FROM Labels WHERE LabelNumber = ?");

   }

   public int addLabel(Label currLabel) throws SQLException
   {
      int retNumber = 0;

      collectQuery.setString(1, currLabel.getName());
      ResultSet rs = collectQuery.executeQuery();
      if (!rs.next())
      {
         // Add the new label
         insertQuery.setString(1, currLabel.getName());
         insertQuery.execute();

         // Get the number
         collectQuery.setString(1, currLabel.getName());
         rs = collectQuery.executeQuery();
         rs.next();
      }

      retNumber = rs.getInt(1);
      rs.close();

      return retNumber;
   }

   private void execute() throws SQLException
   {
      // Get a statement and run the query
      PreparedStatement s = Connect.getConnection().getPreparedStatement(
            "SELECT LabelName, LabelNumber FROM Labels");
      ResultSet rs = s.executeQuery();

      // Initialise the Set
      labels = new TreeMap<String, Label>();

      // Fill the set
      while (rs.next())
      {
         String name = rs.getString(1);
         int number = rs.getInt(2);
         labels.put(name, new Label(name, number));
      }

      // Close the database objects
      rs.close();
      s.close();
   }

   public Label getLabel(int number) throws SQLException
   {
      // Try to manually retrieve the label
      getQuery.setInt(1, number);
      ResultSet rs = getQuery.executeQuery();

      if (rs.next())
      {
         Label temp = new Label(rs.getString(1), number);
         rs.close();
         return temp;
      }
      else
      {
         rs.close();
         return new Label("", -1);
      }
   }

   public Label getLabel(String name) throws SQLException
   {

      // Try to manually retrieve the label
      collectQuery.setString(1, name);
      ResultSet rs = collectQuery.executeQuery();

      if (rs.next())
      {
         Label temp = new Label(name, rs.getInt(1));
         rs.close();
         return temp;
      }
      else
      {
         rs.close();
         return new Label(name, -1);
      }
   }

   public Collection<Label> getLabels()
   {
      try
      {
         if (labels.size() == 0)
            execute();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      return labels.values();
   }

   public Collection<Record> getRecords(int labelNumber) throws SQLException
   {
      String sql = "SELECT records.recordnumber from records,labels,labelset where labels.labelnumber = ? AND labels.labelnumber = labelset.labelnumber AND records.recordnumber = labelset.recordnumber";
      PreparedStatement ps = Connect.getConnection().getPreparedStatement(sql);
      ps.setInt(1, labelNumber);

      Collection<Record> records = new LinkedList<Record>();
      ResultSet rs = ps.executeQuery();
      while (rs.next())
         records.add(GetRecords.create().getRecord(rs.getInt(1)));

      return records;
   }

   public Collection<Label> search(String query) throws SQLException
   {
      List<Label> labels = new LinkedList<Label>();
      PreparedStatement s = Connect.getConnection().getPreparedStatement(
            "SELECT labelnumber FROM labels WHERE lower(labelname) like ?");
      s.setString(1, "%" + query.toLowerCase() + "%");

      ResultSet rs = Connect.getConnection().executeQuery(s);
      while (rs.next())
         labels.add(getLabel(rs.getInt(1)));

      return labels;

   }

}
