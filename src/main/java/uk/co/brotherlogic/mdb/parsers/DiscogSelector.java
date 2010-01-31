package uk.co.brotherlogic.mdb.parsers;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class DiscogHandler extends DefaultHandler
{
	boolean inResult = false;
	Map<String, Integer> releaseMap = new TreeMap<String, Integer>();
	String text = "";
	String title;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		text += new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		String qualName = localName + qName;
		if (qualName.equals("title"))
			title = text;
		else if (qualName.equals("uri"))
		{
			String[] elems = text.split("/");
			releaseMap.put(title, Integer.parseInt(elems[elems.length - 1]));
		}
	}

	public Map<String, Integer> getReleaseMap(String artist)
	{
		System.err.println(releaseMap);

		List<String> toRemove = new LinkedList<String>();
		for (String key : releaseMap.keySet())
			if (!key.contains(artist))
				toRemove.add(key);
		for (String string : toRemove)
			releaseMap.remove(string);
		return releaseMap;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		String qualName = localName + qName;
		text = "";

		if (qualName.equals("result")
				&& attributes.getValue("type").equals("release"))
			inResult = true;
	}
}

public class DiscogSelector
{
	public static void main(String[] args)
	{
		DiscogSelector selector = new DiscogSelector();
		System.out.println(selector.getDiscogID("Blondie", "Plastic Letters"));
	}

	String req = "http://www.discogs.com/search?type=all&q=QUERY&f=xml&api_key=67668099b8";

	public int getDiscogID(String artist, String title)
	{
		try
		{
			URL url = new URL(req.replace("QUERY", URLEncoder.encode(title,
					"utf-8")));
			System.err.println(url);
			DiscogHandler handler = new DiscogHandler();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new GZIPInputStream(url.openStream()), handler);

			Map<String, Integer> rMap = handler.getReleaseMap(artist);
			String[] elems = new String[rMap.keySet().size()];
			int pointer = 0;
			for (String rTitle : rMap.keySet())
				elems[pointer++] = rTitle;

			if (elems.length == 1)
				return rMap.get(elems[0]);
			else
			{
				int chosen = JOptionPane.showOptionDialog(null, "Select",
						"Select", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, elems, elems[0]);
				if (chosen >= 0)
					return rMap.get(elems[chosen]);
			}

		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		return -1;
	}
}
