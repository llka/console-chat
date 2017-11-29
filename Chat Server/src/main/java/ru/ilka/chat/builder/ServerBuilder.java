package ru.ilka.chat.builder;

import ru.ilka.chat.exception.BuilderException;
import ru.ilka.chat.server.Server;

public class ServerBuilder implements ServerBuilderInterface {
    private static final int ARG_PORT_NUMBER_INDEX = 0;

    @Override
    public Server build(String[] arguments) throws BuilderException {
        Server server;
        switch (arguments.length) {
            case 1:
                try {
                    server = new Server(Integer.parseInt(arguments[ARG_PORT_NUMBER_INDEX]));
                } catch (NumberFormatException e) {
                    throw new BuilderException("Invalid port number.");
                }
                break;
            case 0:
                server = new Server();
                break;
            default:
                throw new BuilderException("Invalid number of arguments!");
        }
        return server;
    }
}
