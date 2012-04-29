package uk.co.brotherlogic.mdb;

import java.sql.SQLException;
import java.util.LinkedList;

import junit.framework.TestCase;
import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;
import uk.co.brotherlogic.mdb.groop.LineUp;

public class LineUpTest extends TestCase
{
   public LineUpTest()
   {
      super();
      Connect.setForDevMode();
   }

   public void testLineUp()
   {
      try
      {
         // Create a groop
         Groop g = new Groop("lutgroop", "lutgroop");

         // Create some artists
         Artist a1 = new Artist("artist1");
         Artist a2 = new Artist("artist2");

         LineUp lineup = new LineUp(g);
         lineup.addArtist(a1);
         lineup.addArtist(a2);

         g.save();
         lineup.save();

         // Retrieve the groop
         Groop g2 = GetGroops.build().getGroop("lutgroop");
         LinkedList<LineUp> lineups = new LinkedList<LineUp>(g2.getLineUps());
         System.out.println("SIZE = " + lineups.size());
         assert (lineups.size() == 1);

         LineUp lineup2 = lineups.get(0);
         LinkedList<Artist> artists = new LinkedList<Artist>(lineup2.getArtists());
         assert (artists.size() == 2);
         assert (artists.get(0).equals(a1) || artists.get(0).equals(a2));
         assert (artists.get(0).equals(a1) || artists.get(0).equals(a2));

         Artist a3 = new Artist("artist1");
         Artist a4 = new Artist("artist2");

         Groop g3 = new Groop("lutgroop");

         LineUp lineup3 = new LineUp(g);
         lineup3.addArtist(a3);
         lineup3.addArtist(a4);

         g3.save();
         lineup3.save();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }
}
