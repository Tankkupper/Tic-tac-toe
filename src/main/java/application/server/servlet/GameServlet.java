package application.server.servlet;

import application.action.Action;
import application.server.ServerUserProfile;

import java.net.Socket;

public class GameServlet implements Servlet{
    Socket circle;
    Socket cross;


    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {

    }

    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {

    }
}
