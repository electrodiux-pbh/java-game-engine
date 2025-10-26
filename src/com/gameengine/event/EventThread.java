package com.gameengine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EventThread extends Thread {
	
	private boolean running;
	
	private Map <Class<? extends Event>, List<EventListener<? extends Event>>> listeners;
	private List<Event> events;
	
	public EventThread() {
		this.listeners = new HashMap<>();
		this.events = new ArrayList<>();
	}
	
	public void run() {
		running = true;
		
		super.setPriority(Thread.MIN_PRIORITY);
		
		while(running) {
			if(events.size() > 0) {
//				Iterator<Event> eventIterator = events.iterator();
//				while(eventIterator.hasNext()) {
//					Event e = eventIterator.next();
//					List<EventListener<? extends Event>> listenerList = listeners.get(e.getClass());
//					if(listenerList != null) {
//						int size = listenerList.size();
//						for(int i = 0; i < size; i++) {
//							if(listenerList.size() < size) {
//								i -= size - listenerList.size();
//								size = listenerList.size();
//							}
//							EventListener<? extends Event> listener = listenerList.get(i);
//							listener.execute(e);
//						}
//					}
//					eventIterator.remove();
//				}
			}
		}
	}
	
	public void stopThread() {
		running = false;
	}

	public <E extends Event> void addEventListener(EventListener<E> listener, Class<E> eventClass) {
		List<EventListener<? extends Event>> listenerList = listeners.get(eventClass);
		if(listenerList != null)
			listenerList.add(listener);
		else {
			List<EventListener<? extends Event>> newList = new ArrayList<>();
			newList.add(listener);
			listeners.put(eventClass, newList);
		}
	}

	public <E extends Event> void removeEventListener(EventListener<E> listener, Class<E> eventClass) {
		List<EventListener<? extends Event>> listenerList = listeners.get(eventClass);
		if(listenerList != null)
			listenerList.remove(listener);
	}
	
	public <E extends Event> void removeEventListener(EventListener<E> listener) {
		Set<Entry<Class<? extends Event>, List<EventListener<? extends Event>>>> set = this.listeners.entrySet();
		for(Entry<Class<? extends Event>, List<EventListener<? extends Event>>> entry : set) {
			if(entry.getValue().remove(listener))
				return;
		}
	}
	
	public void performanceEvent(Event event) {
		events.add(event);
	}
	
}
