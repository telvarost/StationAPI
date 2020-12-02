package net.modificationstation.stationloader.api.common.event;

import java.util.function.Function;

public class SimpleEvent<T> extends Event<T> {

    public SimpleEvent(Class<T> type, Function<T[], T> eventFunc) {
        super(type, eventFunc);
    }

    @Override
    public void register(T listener) {
        super.register(listener);
    }
}