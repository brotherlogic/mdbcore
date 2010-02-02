package uk.co.brotherlogic.mdb;

import java.util.Map;
import java.util.TreeMap;

public class Cache<X> {

	Map<Integer, X> objectCache = new TreeMap<Integer, X>();

	public void add(int id, X object) {
		objectCache.put(id, object);
	}

	public X get(int id) {
		if (objectCache.containsKey(id))
			return objectCache.get(id);
		else
			return null;
	}

}
