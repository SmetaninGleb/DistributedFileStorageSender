package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    public String sendAndReceive(String queue, String message) {
        return (String) rabbitTemplate.convertSendAndReceive(queue, message);
    }

    @Override
    public File sendAndReceiveFile(String queue, String message) {
        return (File) rabbitTemplate.convertSendAndReceive(queue, message);
    }
}
