package ru.ilka.chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.ilka.chat.entity.Message;
import ru.ilka.chat.entity.MessageType;
import ru.ilka.chat.exception.ClientException;
import ru.ilka.chat.exception.LogicException;
import ru.ilka.chat.exception.ServerException;
import ru.ilka.chat.logic.WeatherLogic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static Logger logger = LogManager.getLogger(Server.class);
    private static final int DEFAULT_PORT_NUMBER = 8844;
    private static final int IMMEDIATE_MESSAGES = 1;
    private static final String DATE_TIME_REGEX = "yyyy-MM-dd HH:mm:ss";

    private static AtomicInteger connectionsCount = new AtomicInteger(0);
    private ArrayList<ClientThread> clientThreads;
    private int portNumber;
    private AtomicBoolean isServerWorking;
    private Semaphore semaphore;

    public Server() {
        this.portNumber = DEFAULT_PORT_NUMBER;
        this.clientThreads = new ArrayList<>();
        this.isServerWorking = new AtomicBoolean(false);
        this.semaphore = new Semaphore(IMMEDIATE_MESSAGES);
    }

    public Server(int port) {
        this.portNumber = port;
        this.clientThreads = new ArrayList<>();
        this.isServerWorking = new AtomicBoolean(false);
        this.semaphore = new Semaphore(IMMEDIATE_MESSAGES);
    }

    public void start() throws ServerException {
        isServerWorking.set(true);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (isServerWorking.get()) {
                display("Server waiting for Clients on port number: " + portNumber);

                Socket socket = null;
                try {
                /*This method waits until a client connects to the server on the given port.*/
                    socket = serverSocket.accept();
                /*When the ServerSocket invokes accept(), the method does not return until a client connects.
                 After a client does connect, the ServerSocket creates a new Socket on an unspecified port and
                 returns a reference to this new Socket. A TCP connection now exists between the client and
                 the server, and communication can begin.*/
                } catch (IOException e) {
                    throw new ServerException("Can not accept Server Socket.", e);
                }
                if (!isServerWorking.get()) {
                    break;
                }

                try {
                    ClientThread client = new ClientThread(socket);
                    clientThreads.add(client);
                    client.start();
                } catch (ClientException e) {
                    logger.error("Can not log in client: " + e);
                }
            }
        } catch (IOException e) {
            throw new ServerException("Can not init ServerSocket on port number: " + portNumber, e);
        }

        for (ClientThread client : clientThreads) {
            try {
                client.disconnect();
            } catch (ClientException e) {
                throw new ServerException("Error while closing clients socket's threads. ", e);
            }
        }
    }

    private void removeClient(int clientId) {
        for (int i = 0; i < clientThreads.size(); i++) {
            ClientThread client = clientThreads.get(i);
            if (client.clientId == clientId) {
                clientThreads.remove(i);
            }
        }
    }

    private void display(Message message) {
        System.out.println("[" + calculateCurrentTime() + "] - " + "[ " + message.getSenderClientName() + " ] : " + message.getText());
    }

    private void display(String text) {
        System.out.println("[" + calculateCurrentTime() + "] " + text);
    }

    private String calculateCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_TIME_REGEX);
        return now.format(format);
    }

    class ClientThread extends Thread {
        private Socket socket;
        private ObjectInputStream socketInput;
        private ObjectOutputStream socketOutput;

        private int clientId;
        private String userName;

        public ClientThread(Socket socket) throws ClientException {
            this.clientId = connectionsCount.incrementAndGet();
            this.socket = socket;

            try {
                socketOutput = new ObjectOutputStream(socket.getOutputStream());
                socketInput = new ObjectInputStream(socket.getInputStream());
                Message login = receiveMessage(socketInput);
                userName = login.getText();
                display(userName + " just connected");
            } catch (IOException e) {
                throw new ClientException("Error while creating new Input / output Streams: " + e);
            }
        }

        @Override
        public void run() {
            boolean keepGoing = true;
            Message receivedMessage;
            while (keepGoing) {
                try {
                    receivedMessage = (Message) socketInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    logger.error("Error while reading message " + e);
                    break;
                }
                MessageType messageType = receivedMessage.getType();
                try {
                    switch (messageType) {
                        case TEXT:
                            semaphore.acquire();
                            broadCast(receivedMessage, userName);
                            semaphore.release();
                            break;
                        case LOG_OUT:
                            semaphore.acquire();
                            broadCast(receivedMessage, userName);
                            semaphore.release();
                            keepGoing = false;
                            break;
                        case WHO_IS_ONLINE:
                            StringBuffer onlineClients = new StringBuffer("");
                            onlineClients.append("List of the users connected at " + calculateCurrentTime() + " : ");
                            for (ClientThread client : clientThreads) {
                                onlineClients.append("\n" + client.userName);
                            }
                            try {
                                this.sendMessage(new Message(MessageType.WHO_IS_ONLINE, onlineClients.toString(), userName));
                            } catch (ClientException e) {
                                logger.error("Can not show online users " + e);
                            }
                            break;
                        case WEATHER_MINSK:
                            WeatherLogic weatherLogic = new WeatherLogic();
                            try {
                                this.sendMessage(new Message(MessageType.WEATHER_MINSK, weatherLogic.findWeatherInMinsk(), userName));
                            } catch (ClientException | LogicException e) {
                                logger.error("Can not show weather in Minsk " + e);
                            }
                            break;
                        default:
                            logger.error("Unknown message type. " + messageType);
                    }
                } catch (InterruptedException e) {
                    logger.error("Can not send message " + e);
                }
            }
            try {
                semaphore.acquire();
                removeClient(clientId);
            } catch (InterruptedException e) {
                logger.error("Can not remove client before disconnection. " + e);
            }
            semaphore.release();

            try {
                disconnect();
            } catch (ClientException e) {
                logger.error("Can not disconnect. " + e);
            }
        }

        private void broadCast(Message message, String senderName) {
            display(message);
            for (int i = 0; i < clientThreads.size(); i++) {
                ClientThread client = clientThreads.get(i);
                if (!senderName.equals(client.getUserName())) {
                    try {
                        client.sendMessage(message);
                    } catch (ClientException e) {
                        clientThreads.remove(i);
                    }
                }
            }
        }

        private void disconnect() throws ClientException {
            try {
                if (socketInput != null) {
                    socketInput.close();
                }
            } catch (IOException e) {
                throw new ClientException("Error while closing socketInput" + e);
            }
            try {
                if (socketOutput != null) {
                    socketOutput.close();
                }
            } catch (IOException e) {
                throw new ClientException("Error while closing socketOutput" + e);
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new ClientException("Error while closing socket" + e);
            }
            connectionsCount.decrementAndGet();
        }

        private void sendMessage(Message message) throws ClientException {
            if (!socket.isConnected()) {
                disconnect();
                throw new ClientException("Socket is closed");
            }

            try {
                socketOutput.writeObject(message);
            } catch (IOException e) {
                throw new ClientException("Error while sending message from " + userName);
            }
        }

        private Message receiveMessage(ObjectInputStream inputStream) throws ClientException {
            try {
                return (Message) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new ClientException("Can not receive message");
            }
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public ObjectInputStream getSocketInput() {
            return socketInput;
        }

        public void setSocketInput(ObjectInputStream socketInput) {
            this.socketInput = socketInput;
        }

        public ObjectOutputStream getSocketOutput() {
            return socketOutput;
        }

        public void setSocketOutput(ObjectOutputStream socketOutput) {
            this.socketOutput = socketOutput;
        }

        public int getClientId() {
            return clientId;
        }

        public void setClientId(int clientId) {
            this.clientId = clientId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
