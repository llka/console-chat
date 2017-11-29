package ru.ilka.chat.builder;

import ru.ilka.chat.client.Client;
import ru.ilka.chat.exception.BuilderException;

public interface ClientBuilderInterface {
    Client build(String[] arguments) throws BuilderException;
}
