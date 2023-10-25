package ru.innopolis.csn.finalproject.distributedfilestorage.sender.bots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.services.BotMessageProcessingService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class FileStorageTelegramBot extends TelegramLongPollingBot {

    private final BotMessageProcessingService messageProcessingService;

    @Value("${config.bot.name}")
    private String botName;

    @Value("${config.bot.texts.document_saving}")
    private String documentSavingText;

    @Value("${config.bot.texts.get_documents_names}")
    private String getDocumentsNamesText;

    @Value("${config.bot.texts.no_documents_yet}")
    private String noDocumentsYetText;

    @Value("${config.bot.texts.document_not_found}")
    private String documentNotFoundText;

    @Value("${config.bot.texts.getting_file_error}")
    private String gettingFileErrorText;

    @Autowired
    public FileStorageTelegramBot(@Value("${config.bot.token}") String botToken,
                                  BotMessageProcessingService messageProcessingService) {
        super(botToken);
        this.messageProcessingService = messageProcessingService;

    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message.hasDocument()) {
            processDocumentSaving(update);
        } else if (message.getText().equals(getDocumentsNamesText)){
            processGettingNames(update);
        } else {
            processSendDocument(update);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void processDocumentSaving(Update update) {
        GetFile getFile = new GetFile();
        getFile.setFileId(update.getMessage().getDocument().getFileId());
        String filePath;
        String filename = update.getMessage().getDocument().getFileName();
        String chatId = update.getMessage().getChatId().toString();
        try
        {
            filePath = execute(getFile).getFilePath();
        } catch (TelegramApiException e)
        {
            sendMessage(update, gettingFileErrorText);
            return;
        }
        File file;
        try
        {
            file = downloadFile(filePath);
        } catch (TelegramApiException e)
        {
            sendMessage(update, gettingFileErrorText);
            return;
        }
        try
        {
            messageProcessingService.saveDocument(file, filename, chatId);
        } catch (IOException e)
        {
            sendMessage(update, gettingFileErrorText);
            return;
        }
        sendMessage(update, documentSavingText);
    }

    private void processGettingNames(Update update) {
        List<String> nameList = messageProcessingService.getDocumentsNames(update);
        if (!nameList.isEmpty())
        {
            String ansStr = nameList
                    .stream()
                    .reduce((acc, name) -> acc + "\n" + name)
                    .get();
            sendMessage(update, ansStr);
        } else {
            sendMessage(update, noDocumentsYetText);
        }
    }

    private void processSendDocument(Update update) {
        List<String> namesList = messageProcessingService.getDocumentsNames(update);
        String documentName = update.getMessage().getText();
        if (!namesList.contains(documentName)) {
            sendMessage(update, documentNotFoundText);
            return;
        }
        try
        {
            execute(messageProcessingService.getDocument(update));
        } catch (TelegramApiException e)
        {
            throw new RuntimeException(e);
        }
    }

    private SendMessage getSendMessageWithText(Update update, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(text);
        return message;
    }

    private void sendMessage(SendMessage sendMessage) {
        try
        {
            execute(sendMessage);
        } catch (TelegramApiException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Update update, String text) {
        sendMessage(getSendMessageWithText(update, text));
    }
}
