package civgame.model.event;

import java.util.ArrayList;
import java.util.List;

public class GameEventBus {

    private final List<GameEventListener> listeners;

    public GameEventBus() {
        this.listeners = new ArrayList<GameEventListener>();
    }

    public void subscribe(GameEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(GameEvent event) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onGameEvent(event);
        }
    }
}