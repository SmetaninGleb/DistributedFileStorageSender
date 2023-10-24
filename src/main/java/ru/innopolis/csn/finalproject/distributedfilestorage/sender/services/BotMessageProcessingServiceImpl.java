package ru.innopolis.csn.finalproject.distributedfilestorage.sender.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.innopolis.csn.finalproject.distributedfilestorage.sender.utils.SendType;

import java.util.List;

@Service
public class BotMessageProcessingServiceImpl implements BotMessageProcessingService {

    @Override
    public void saveDocument(Update update) {

    }

    @Override
    public List<String> getDocumentsNames(Update update) {
        return null;
    }

    @Override
    public SendDocument getDocument(Update update) {
        return null;
    }
}
