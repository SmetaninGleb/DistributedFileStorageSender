package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.amqp.core.Message;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.utils.FileData;

import java.io.File;
import java.io.IOException;

public interface RabbitmqService {
    void send(String message);
    void send(Message message);
    void send(File file, String filename, String chatId) throws IOException;
    String sendAndReceiveFileList(String chatId);
    FileData sendAndReceiveFile(String queue, String filename, String chatId);
}
