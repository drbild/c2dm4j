/*
 * Copyright 2012 The Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.whispercomm.c2dm4j.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A thread-safe implementation of {@code ListMultimap} in which all mutative
 * operations are applied to a copy of the underlying {@code ArrayListMultimap}.
 * <p>
 * These copies are expensive, but may be acceptable when reads vastly outnumber
 * writes (i.e., mutative operations). Reads are non-blocking. Iterators see a
 * "snapshot" of the map at the time the iterator was created and may not modify
 * the map (e.g., {@code add} and {@code remove} are not supported. Writes are
 * ordered by a lock and thus are blocking.
 * <p>
 * 
 * @author David R. Bild
 * 
 * @param <K>
 *            the type of the keys held in this map
 * @param <V>
 *            the type of the values held in this map
 */
public class CopyOnWriteArrayListMultimap<K, V> implements ListMultimap<K, V> {

	/*
	 * Actual backing map, accessed by readers
	 */
	private volatile ImmutableListMultimap<K, V> map;

	/*
	 * Map used during write operations and associated lock. Accessed only via
	 * startWrite() and finishWrite();
	 */
	private ListMultimap<K, V> newMap;
	private final ReentrantLock lock;

	/**
	 * Constructs an empty {@code CopyOnWriteArrayListMultimap}.
	 */
	public static <K, V> CopyOnWriteArrayListMultimap<K, V> create() {
		return new CopyOnWriteArrayListMultimap<K, V>(
				ArrayListMultimap.<K, V> create(0, 0));
	}

	/**
	 * Constructs an {@code CopyOnWriteArrayListMultimap} containing the same
	 * mappings as the specified {@link Multimap}.
	 * 
	 * @param multimap
	 *            the mapping to be copied
	 */
	public static <K, V> CopyOnWriteArrayListMultimap<K, V> create(
			Multimap<? extends K, ? extends V> multimap) {
		return new CopyOnWriteArrayListMultimap<K, V>(multimap);
	}

	private CopyOnWriteArrayListMultimap(
			Multimap<? extends K, ? extends V> multimap) {
		map = ImmutableListMultimap.copyOf(multimap);
		lock = new ReentrantLock();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		return map.containsEntry(key, value);
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Multiset<K> keys() {
		return map.keys();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Collection<Entry<K, V>> entries() {
		return map.entries();
	}

	@Override
	public ImmutableList<V> get(K key) {
		return map.get(key);
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		return map.asMap();
	}

	/**
	 * Starts a write operation by taking the lock and, if needed, creating a
	 * mutable copy of the map.
	 * <p>
	 * This method is reentrant.
	 */
	private void startWrite() {
		lock.lock();
		if (newMap == null) // Write not already started
			newMap = ArrayListMultimap.create(map);
	}

	/**
	 * Finishes a write operation by replacing the map with the modified one, if
	 * this call corresponds to the outer-most {@link startWrite} call, and
	 * unlocking the lock.
	 * <p>
	 * Each call to the reentrant {@code startWrite} method requires a
	 * corresponding call to this {code finishWrite}.
	 */
	private void finishWrite() {
		/*
		 * Replace the map with the modified one, if we are releasing the write
		 * lock.
		 */
		if (lock.getHoldCount() == 1) {
			map = ImmutableListMultimap.copyOf(newMap);
		}
		lock.unlock();
	}

	private <T> T finishWrite(T ret) {
		finishWrite();
		return ret;
	}

	@Override
	public boolean put(K key, V value) {
		startWrite();
		return finishWrite(newMap.put(key, value));
	}

	@Override
	public boolean remove(Object key, Object value) {
		startWrite();
		return finishWrite(newMap.remove(key, value));
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		startWrite();
		return finishWrite(newMap.putAll(key, values));
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		startWrite();
		return finishWrite(newMap.putAll(multimap));
	}

	@Override
	public void clear() {
		startWrite();
		newMap.clear();
		finishWrite();
	}

	@Override
	public List<V> removeAll(Object key) {
		startWrite();
		return finishWrite(newMap.removeAll(key));
	}

	@Override
	public List<V> replaceValues(K key, Iterable<? extends V> values) {
		startWrite();
		return finishWrite(newMap.replaceValues(key, values));
	}

	@Override
	public boolean equals(Object object) {
		return map.equals(object);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}
}
