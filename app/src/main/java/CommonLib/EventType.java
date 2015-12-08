package CommonLib;

/**
 * Created by My PC on 26/11/2015.
 */
public abstract class EventType {
    public static enum Type {
        BEGIN,
        END};
    public static class EventBase {
        public Type type;
        public EventBase(Type type) {
            this.type = type;
        }
    }
}

