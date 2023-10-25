package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BotMessageProcessingServiceImpl implements BotMessageProcessingService {

    private final RabbitmqService rabbitmqService;

    @Value("${config.rabbitmq.messages.get_document_list}")
    private String getDocumentListMessageName;
    @Value("${config.rabbitmq.get_document_list_queue_name}")
    private String getDocumentListQueueName;
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
        String reply = rabbitmqService.sendAndReceive(getDocumentListQueueName, getDocumentListMessageName);
        return List.of(reply.split("\n"));
    }

    @Override
    public SendDocument getDocument(Update update) {
        File file = rabbitmqService.sendAndReceiveFile(getDocumentQueueName, update.getMessage().getText());
        SendDocument sendDocument = new SendDocument();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(file);
        sendDocument.setDocument(new InputFile());
        return sendDocument;
    }
}
