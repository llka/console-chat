package ru.ilka.chat.demo;

import org.apache.log4j.Logger;
import ru.ilka.chat.builder.ClientBuilder;
import ru.ilka.chat.client.Client;
import ru.ilka.chat.entity.Message;
import ru.ilka.chat.entity.MessageType;
import ru.ilka.chat.exception.BuilderException;
import ru.ilka.chat.exception.ClientException;

import java.util.Scanner;

import static ru.ilka.chat.entity.MessageType.LOG_OUT;

public class ClientDemo {
    private static Logger logger = Logger.getLogger(ClientDemo.class);

    public static void main(String[] args) {
        try {
            Client client = new ClientBuilder().build(args);

            try {
                client.start();
            } catch (ClientException e) {
                logger.error("Can not start client! " + e);
            }

            Scanner scan = new Scanner(System.in);

            while (true) {
                System.out.print("> ");
                String clientText = scan.nextLine();
                MessageType messageType;
                try {
                    messageType = MessageType.valueOf(clientText);
                } catch (IllegalArgumentException e) {
                    messageType = MessageType.TEXT;
                }
                try {
                    if(MessageType.LOG_IN.equals(messageType)){
                        System.out.println("You are already logged in.");
                    }else {
                        client.sendMessage(new Message(messageType, clientText, client.getUserName()));
                    }

                } catch (ClientException e) {
                    logger.error("Can not send message " + e);
                }
                if (LOG_OUT.equals(messageType)) {
                    break;
                }
            }

            try {
                client.disconnect();
            } catch (ClientException e) {
                logger.error("Client can not disconnect " + e);
            }

        } catch (BuilderException e) {
            logger.error("Can not create client " + e);
        }
    }
}
