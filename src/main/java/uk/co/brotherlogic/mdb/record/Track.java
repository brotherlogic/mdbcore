package uk.co.brotherlogic.mdb.record;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.groop.Groop;
import uk.co.brotherlogic.mdb.groop.LineUp;

/**
 * Class to represent a track on a recording
 * 
 * @author Simon Tucker
 */
public class Track implements Comparable<Track>, Serializable
{
   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;

   /** The format track number */
   private int formTrackNumber = -1;

   /** The LineUps associated with this track */
   private final List<LineUp> groops;

   /** The length of the track in seconds */
   private int lengthInSeconds;

   /** THe record from which the track is taken */
   private int parentNumber;

   /** The personnel on the track */
   private final Collection<Artist> personnel;

   /** THe track reference number */
   private int refNumber = -1;

   /** The track title */
   private String title;

   /** The track number of this track */
   private int trackNumber;

   /**
    * Constructor
    */
   public Track()
   {
      groops = new LinkedList<LineUp>();
      personnel = new LinkedList<Artist>();
      title = "";
      lengthInSeconds = -1;
      trackNumber = -1;
      refNumber = -1;
      formTrackNumber = -1;
   }

   public Track(int number)
   {
      groops = new LinkedList<LineUp>();
      personnel = new LinkedList<Artist>();
      title = "";
      lengthInSeconds = -1;
      trackNumber = number;
      refNumber = -1;
      formTrackNumber = -1;
   }

   /**
    * Constructor
    * 
    * @param titleIn
    *           Track title
    * @param lengthIn
    *           Length in Seconds
    * @param groopsIn
    *           The groops
    * @param personnelIn
    *           The personnel
    * @param trackNumberIn
    *           The track number
    * @param trackRefNumber
    *           The track reference number
    */
   public Track(final String titleIn, final int lengthIn, final Collection<LineUp> groopsIn,
         final Collection<Artist> personnelIn, final int trackNumberIn, final int trackRefNumber,
         final int formTrackNumber, final int recordnumber)
   {
      title = titleIn;
      lengthInSeconds = lengthIn;
      groops = new LinkedList<LineUp>();
      groops.addAll(groopsIn);
      personnel = new LinkedList<Artist>();
      personnel.addAll(personnelIn);
      trackNumber = trackNumberIn;
      refNumber = trackRefNumber;
      this.formTrackNumber = formTrackNumber;
      parentNumber = recordnumber;
   }

   public final void addLineUp(final LineUp lineup)
   {
      groops.add(lineup);
   }

   public final void addLineUps(final Collection<LineUp> lineups)
   {
      groops.addAll(lineups);
   }

   public final void addPersonnel(final Artist person)
   {
      personnel.add(person);
   }

   /**
    * Adds the given personnel to the track
    * 
    * @param personnelIn
    *           A collection of Artists
    */
   public final void addPersonnel(final Collection<Artist> personnelIn)
   {
      personnel.addAll(personnelIn);
   }

   @Override
   public final int compareTo(final Track in)
   {
      int otherNum = in.getTrackNumber();
      return trackNumber - otherNum;
   }

   @Override
   public final boolean equals(final Object o)
   {
      if (o instanceof Track)
         return this.compareTo((Track) o) == 0;
      else
         return false;
   }

   public int getFormTrackNumber()
   {
      return formTrackNumber;
   }

   public Collection<Groop> getGroops()
   {
      Collection<Groop> grps = new LinkedList<Groop>();

      for (LineUp lUp : groops)
         grps.add(lUp.getGroop());

      return grps;
   }

   /**
    * Get method for length
    * 
    * @return The length of the track in seconds
    */
   public final int getLengthInSeconds()
   {
      return lengthInSeconds;
   }

   /**
    * Get method for the lineups
    * 
    * @return A Collection of LineUps
    */
   public final Collection<LineUp> getLineUps()
   {
      return groops;
   }

   public Record getParent() throws SQLException
   {
      return GetRecords.create().getRecord(parentNumber);
   }

   /**
    * Get method for the personnel
    * 
    * @return A collection of artists
    */
   public final Collection<Artist> getPersonnel()
   {
      return personnel;
   }

   public String getSortedTrackAuthor()
   {
      StringBuffer author = new StringBuffer(groops.get(0).getGroop().getShowName());
      for (LineUp lUp : groops.subList(1, groops.size()))
         author.append(", " + lUp.getGroop().getShowName());

      return author.toString();
   }

   /**
    * Get method for the title
    * 
    * @return The track title
    */
   public final String getTitle()
   {
      return title;
   }

   public String getTrackAuthor()
   {
      StringBuffer author = new StringBuffer(groops.get(0).getGroop().getSortName());
      for (LineUp lUp : groops.subList(1, groops.size()))
         author.append(", " + lUp.getGroop().getSortName());

      return author.toString();
   }

   /**
    * Get method for the track number
    * 
    * @return the track number
    */
   public final int getTrackNumber()
   {
      return trackNumber;
   }

   @Override
   public int hashCode()
   {
      return title.hashCode() + refNumber;
   }

   public void save(int recordNumber) throws SQLException
   {
      if (refNumber == -1)
         GetRecords.create().addTrack(recordNumber, this);
      else
         GetRecords.create().updateTrack(recordNumber, this);
   }

   public void setFormTrackNumber(int formTrackNumber)
   {
      this.formTrackNumber = formTrackNumber;
   }

   public void setLengthInSeconds(int lengthInSeconds)
   {
      this.lengthInSeconds = lengthInSeconds;
   }

   public void setParentNumber(int parentNumber)
   {
      this.parentNumber = parentNumber;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public void setTrackNumber(int number)
   {
      trackNumber = number;
   }

   @Override
   public String toString()
   {
      StringBuffer ret = new StringBuffer("");

      // Do the static stuff
      ret.append("#T#" + trackNumber + "~" + title + "~" + lengthInSeconds);

      // Do the personnel
      Iterator<Artist> pIt = personnel.iterator();
      while (pIt.hasNext())
         ret.append("~" + pIt.next());
      ret.append("\n");

      // Do the groups
      Iterator<LineUp> gIt = groops.iterator();
      while (gIt.hasNext())
         ret.append(gIt.next());

      return ret.toString();

   }
}
