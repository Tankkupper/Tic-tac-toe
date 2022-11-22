package application.server.servlet;

import application.action.Action;
import application.server.Server;
import application.server.ServerUserProfile;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface Servlet {
    Logger log = Logger.getLogger(Server.class);
    void dispose(Action action, Socket socket, ServerUserProfile profile);

    void handleException(Socket socket, ServerUserProfile profile);

    default void send(Action action, Socket socket) throws IOException {
        log.trace("Send: " + action + " TO " + socket);
        ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                        socket.getOutputStream()));
        oos.writeObject(action);
        oos.flush();
    }
}
