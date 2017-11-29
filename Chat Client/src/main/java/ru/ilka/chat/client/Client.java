package ru.ilka.chat.client;

import ru.ilka.chat.entity.Message;
import ru.ilka.chat.entity.MessageType;
import ru.ilka.chat.exception.ClientException;
import ru.ilka.chat.listner.ServerMessagesListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final int DEFAULT_PORT_NUMBER = 8844;
    private static final String DEFAULT_SERVER_NAME = "localhost";
    private static final String DEFAULT_USER_NAME = "User undefined .-. ";

    private ObjectInputStream socketInput;
    private ObjectOutputStream socketOutput;
    private Socket socket;
    private String serverName;
    private String userName;
    private int portNumber;

    public Client() {
        this.serverName = DEFAULT_SERVER_NAME;
        this.userName = DEFAULT_USER_NAME;
        this.portNumber = DEFAULT_PORT_NUMBER;
    }

    public Client(String serverAddress) {
        this.serverName = serverAddress;
        this.userName = DEFAULT_USER_NAME;
        this.portNumber = DEFAULT_PORT_NUMBER;
    }

    public Client(String serverAddress, String userName) {
        this.serverName = serverAddress;
        this.userName = userName;
        this.portNumber = DEFAULT_PORT_NUMBER;
    }

    public Client(String serverAddress, String userName, int portNumber) {
        this.serverName = serverAddress;
        this.portNumber = portNumber;
        this.userName = userName;
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() throws ClientException {
        try {
            socket = new Socket(serverName, portNumber);
        } catch (Exception e) {
            throw new ClientException("Error while connecting to server.", e);
        }

        System.out.println("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            socketInput = new ObjectInputStream(socket.getInputStream());
            socketOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ClientException("Exception creating new Input/output Streams.", e);
        }

        new Thread(new ServerMessagesListener(socketInput)).start();

        try {
            sendMessage(new Message(MessageType.LOG_IN, userName, userName));
        } catch (ClientException e) {
            disconnect();
            throw new ClientException("Error while logging in.", e);
        }
    }

    public void sendMessage(Message message) throws ClientException {
        try {
            socketOutput.writeObject(message);
        } catch (IOException e) {
            throw new ClientException("Exception sending message to server.", e);
        }
    }

    public void disconnect() throws ClientException {
        try {
            if (socketInput != null) {
                socketInput.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socketInput.", e);
        }
        try {
            if (socketOutput != null) {
                socketOutput.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socketOutput.", e);
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socket.", e);
        }
    }
}
