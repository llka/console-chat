package ru.ilka.chat.builder;

import ru.ilka.chat.exception.BuilderException;
import ru.ilka.chat.server.Server;

public interface ServerBuilderInterface {
    Server build(String[] arguments) throws BuilderException;
}
