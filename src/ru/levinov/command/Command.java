package ru.levinov.command;


import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;

public abstract class Command implements IMinecraft {
    public final String command, description;

    public Command() {
        command = this.getClass().getAnnotation(Cmd.class).name();
        description = this.getClass().getAnnotation(Cmd.class).description();
    }

    public abstract void run(String[] args) throws Exception;
    public abstract void error();
    public void sendMessage(String message) {
        ClientUtil.sendMesage(message);
    }

}
