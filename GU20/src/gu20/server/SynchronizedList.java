package gu20.server;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents an ArrayList as a synchronized data structure, implementing some often used methods. Is iterable.
 * @author Pontus Laos
 *
 * @param <T> Anything.
 */
public class SynchronizedList<T> implements Iterable<T> {
	private ArrayList<T> list;
	
	public SynchronizedList() {
		list = new ArrayList<>();
	}
	
	public synchronized boolean add(T item) {
		return list.add(item);
	}
	
	public synchronized int size() {
		return list.size();
	}
	
	@Override
	public synchronized Iterator<T> iterator() {
		return list.iterator();
	}

}
