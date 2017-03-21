package de.upb.wever.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUtil {

	public static <K, X> void safeAddToSetInMap(final Map<K, Set<X>> map, final K key, final X value) {
		Set<X> setForKey = map.get(key);
		if (setForKey == null) {
			setForKey = new HashSet<>();
			map.put(key, setForKey);
		}
		setForKey.add(value);
	}

	public static <K, X> void safeAddAllToSetInMap(final Map<K, Set<X>> map, final K key, final Set<X> value) {
		Set<X> setForKey = map.get(key);
		if (setForKey == null) {
			setForKey = new HashSet<>();
			map.put(key, setForKey);
		}
		setForKey.addAll(value);
	}

	public static <K, X, Y> void safePutToMapInMap(final Map<K, Map<X, Y>> map, final K key, final X secondLvlKey, final Y value) {
		Map<X, Y> mapForKey = map.get(key);
		if (mapForKey == null) {
			mapForKey = new HashMap<>();
			map.put(key, mapForKey);
		}
		mapForKey.put(secondLvlKey, value);
	}

	public static <K, X, Y> void safePutToMapInMap(final Map<K, Map<X, Y>> map, final K key, final Map<X, Y> secondLvlMap) {
		Map<X, Y> mapForKey = map.get(key);
		if (mapForKey == null) {
			mapForKey = new HashMap<>();
			map.put(key, mapForKey);
		}
		mapForKey.putAll(secondLvlMap);
	}

	public static <K, X> void safeAddToListInMap(final Map<K, List<X>> map, final K key, final X elementToAdd) {
		List<X> listForKey = map.get(key);
		if (listForKey == null) {
			listForKey = new LinkedList<>();
			map.put(key, listForKey);
		}
		listForKey.add(elementToAdd);
	}

}
