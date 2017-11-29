package ru.ilka.chat.demo;

import org.apache.log4j.Logger;
import ru.ilka.chat.builder.ServerBuilder;
import ru.ilka.chat.exception.BuilderException;
import ru.ilka.chat.exception.ServerException;
import ru.ilka.chat.server.Server;

public class ServerDemo {
    private static Logger logger = Logger.getLogger(ServerDemo.class);

    public static void main(String[] args) {
        try {
            Server server = new ServerBuilder().build(args);
            startServer(server);
        } catch (BuilderException e) {
            logger.error("Can not create server. " + e);
        }
    }

    private static void startServer(Server server) {
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Can not start server. " + e);
        }
    }
}
