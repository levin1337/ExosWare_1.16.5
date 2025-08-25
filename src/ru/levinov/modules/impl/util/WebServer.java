package ru.levinov.modules.impl.util;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "WebServer", type = Type.Util,desc = "Запуск сайта для управления")
public class WebServer extends Function {

    @Override
    public void onEvent(Event event) {
        ru.levinov.server.WebServer.startServer();
      //  toggle();
    }
}