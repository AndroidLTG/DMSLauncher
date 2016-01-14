package CommonLib;

import java.util.LinkedList;

/**
 * Created by My PC on 26/11/2015.
 */
public class EventPool {
    private static EventPool instance_control = null, instance_view = null;

    private EventPool() {
    }

    public synchronized static EventPool control() {
        if (instance_control == null) {
            instance_control = new EventPool();
        }
        return instance_control;
    }

    public synchronized static EventPool view() {
        if (instance_view == null) {
            instance_view = new EventPool();
        }
        return instance_view;
    }

    private LinkedList<EventType.EventBase> events = new LinkedList<EventType.EventBase>();
    public void enQueue(EventType.EventBase event) {
        synchronized (this) {
            events.add(event);
        }
    }

    public EventType.EventBase deQueue() {
        synchronized (this) {
            return events.poll();
        }
    }
}
