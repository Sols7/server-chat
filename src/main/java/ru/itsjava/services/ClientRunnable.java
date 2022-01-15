package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final ServerService serverService;
    private User user;
    private final UserDao userDao;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connected");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String messageClient = bufferedReader.readLine();
        String messageFromClient;
        if (registration(messageClient) || authorization(messageClient)) {
            serverService.addObserver(this);
            while ((messageFromClient = bufferedReader.readLine()) != null) {
                System.out.println(user.getName() + ":" + messageFromClient);
                serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);
            }
        }
    }

    @SneakyThrows
    private boolean authorization(String authorizationMessage) {
        //!autho!login:password
        if (authorizationMessage.startsWith("!autho!")) {
            String login = authorizationMessage.substring(7).split(":")[0];
            String password = authorizationMessage.substring(7).split(":")[1];
            user = userDao.findByNameAndPassword(login, password);
            //user = new User(login, password);
            return true;
        }
        return false;
    }

    @SneakyThrows
    private boolean registration(String registrationMessage) {
        //!reg!login:password
        if (registrationMessage.startsWith("!reg!")) {
            String login = registrationMessage.substring(5).split(":")[0];
            String password = registrationMessage.substring(5).split(":")[1];
            user = userDao.createUser(login, password);
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void notifyMe(String message) {
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());
        clientWriter.println(message);
        clientWriter.flush();
    }
}
