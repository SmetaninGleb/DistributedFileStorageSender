package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.utils.FileData;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BotMessageProcessingServiceImpl implements BotMessageProcessingService {

    private final RabbitmqService rabbitmqService;

    @Value("${config.rabbitmq.get_document_queue_name}")
    private String getDocumentQueueName;

    public BotMessageProcessingServiceImpl(RabbitmqService rabbitmqService) {
        this.rabbitmqService = rabbitmqService;
    }

    @Override
    public void saveDocument(File file, String filename, String chatId) throws IOException {
        rabbitmqService.send(file, filename, chatId);
    }

    @Override
    public List<String> getDocumentsNames(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String reply = rabbitmqService.sendAndReceiveFileList(chatId);
        return List.of(reply.split("\n"));
    }

    @Override
    public SendDocument getDocument(Update update) {
        String filename = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        FileData fileData = rabbitmqService.sendAndReceiveFile(getDocumentQueueName, filename, chatId);
        SendDocument sendDocument = new SendDocument();
        InputFile inputFile = new InputFile();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(fileData.getFileBytes());
        inputFile.setMedia(byteStream, fileData.getFilename());
        sendDocument.setDocument(inputFile);
        sendDocument.setChatId(chatId);
        return sendDocument;
    }
}
