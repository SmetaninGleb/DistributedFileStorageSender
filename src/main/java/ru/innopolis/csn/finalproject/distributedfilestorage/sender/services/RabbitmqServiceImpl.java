package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.utils.FileData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RabbitmqServiceImpl implements RabbitmqService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${config.rabbitmq.sender_exchange_name}")
    private String senderExchangeName;

    @Value("${config.rabbitmq.filename_header_name}")
    private String filenameHeaderName;

    @Value("${config.rabbitmq.chat_id_header_name}")
    private String chatIdHeaderName;

    @Value("${config.rabbitmq.messages.get_document_list}")
    private String getDocumentListMessageName;

    @Value("${config.rabbitmq.get_document_list_queue_name}")
    private String getDocumentListQueueName;

    @Autowired
    public RabbitmqServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(String message) {
        rabbitTemplate.setExchange(senderExchangeName);
        rabbitTemplate.convertAndSend(message);
    }

    @Override
    public void send(Message message) {
        rabbitTemplate.setExchange(senderExchangeName);
        rabbitTemplate.convertAndSend(message);
    }

    @Override
    public void send(File file, String filename, String chatId) throws IOException {
        byte[] fileData = Files.readAllBytes(Path.of(file.getPath()));
        Message message = MessageBuilder
                .withBody(fileData)
                .setHeader(filenameHeaderName, filename)
                .setHeader(chatIdHeaderName, chatId)
                .build();
        rabbitTemplate.setExchange(senderExchangeName);
        rabbitTemplate.send(message);
    }

    @Override
    public String sendAndReceiveFileList(String chatId) {
        Message message = MessageBuilder
                .withBody(getDocumentListMessageName.getBytes())
                .setHeader(chatIdHeaderName, chatId)
                .build();
        return (String) rabbitTemplate.convertSendAndReceive(getDocumentListQueueName, message);
    }

    @Override
    public FileData sendAndReceiveFile(String queue, String filename, String chatId) {
        Message sendMes = MessageBuilder
                .withBody(filename.getBytes())
                .setHeader(chatIdHeaderName, chatId)
                .build();
        Message rabbitAns = rabbitTemplate.sendAndReceive(queue, sendMes);
        byte[] fileBytes = rabbitAns.getBody();
        return new FileData(filename, fileBytes);
    }
}
