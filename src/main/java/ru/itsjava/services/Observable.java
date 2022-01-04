package ru.itsjava.services;

public interface Observable {
    void addObserver(Observer observer);

    void deleteObserver(Observer observer);

    void notifyObservers(String message);

    void notifyObserverExpectMe(String message, Observer observerExpectMe);
}
