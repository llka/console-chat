package ru.ilka.chat.builder;

import ru.ilka.chat.client.Client;
import ru.ilka.chat.exception.BuilderException;

public class ClientBuilder implements ClientBuilderInterface {
    private static final int ARG_SERVER_ADDRESS_INDEX = 0;
    private static final int ARG_USER_NAME_INDEX = 1;
    private static final int ARG_PORT_NUMBER_INDEX = 2;

    @Override
    public Client build(String[] arguments) throws BuilderException {
        Client client;
        switch (arguments.length) {
            case 3:
                try {
                    client = new Client(arguments[ARG_SERVER_ADDRESS_INDEX],
                            arguments[ARG_USER_NAME_INDEX],
                            Integer.parseInt(arguments[ARG_PORT_NUMBER_INDEX]));
                } catch (NumberFormatException e) {
                    throw new BuilderException("Invalid port Number.");
                }
                break;
            case 2:
                client = new Client(arguments[ARG_SERVER_ADDRESS_INDEX],
                        arguments[ARG_USER_NAME_INDEX]);
                break;
            case 1:
                client = new Client(arguments[ARG_SERVER_ADDRESS_INDEX]);
                break;
            case 0:
                client = new Client();
                break;
            default:
                throw new BuilderException("Invalid number of arguments!");
        }
        return client;
    }
}
