package ru.levinov.modules.impl.util;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

/**
 * @author levin1337
 * @since 12.07.2023
 */
@FunctionAnnotation(name = "NoCommands", type = Type.Util)
public class NoCommands extends Function {
    @Override
    public void onEvent(Event event) {

    }
}
