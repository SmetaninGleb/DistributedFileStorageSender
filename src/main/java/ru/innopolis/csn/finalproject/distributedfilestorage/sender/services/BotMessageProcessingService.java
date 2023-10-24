package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;


import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.utils.SendType;

import java.util.List;

public interface BotMessageProcessingService {
    void saveDocument(Update update);
    List<String> getDocumentsNames(Update update);
    SendDocument getDocument(Update update);
}
