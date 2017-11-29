package ru.ilka.chat.listner;

import org.apache.log4j.Logger;
import ru.ilka.chat.entity.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerMessagesListener implements Runnable {
    private static Logger logger = Logger.getLogger(ServerMessagesListener.class);
    private ObjectInputStream socketInput;

    public ServerMessagesListener(ObjectInputStream socketInput) {
        this.socketInput = socketInput;
    }

    public ObjectInputStream getSocketInput() {
        return socketInput;
    }

    public void setSocketInput(ObjectInputStream socketInput) {
        this.socketInput = socketInput;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = (Message) socketInput.readObject();
                System.out.println("[ " + message.getSenderClientName() + " ] : " + message.getText());
                System.out.print("> ");
            } catch (IOException e) {
                logger.info("You are disconnected now. " + e.getMessage());
                break;
            } catch (ClassNotFoundException e) {
                logger.error("Error while waiting for messages from server: " + e);
                break;
            }
        }
    }
}
