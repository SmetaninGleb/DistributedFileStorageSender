package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.amqp.core.Message;

import java.io.File;
import java.io.IOException;

public interface RabbitmqService {
    void send(String message);
    void send(Message message);
    void send(File file, String filename, String chatId) throws IOException;
    String sendAndReceive(String queue, String message);
    File sendAndReceiveFile(String queue, String message);
}
