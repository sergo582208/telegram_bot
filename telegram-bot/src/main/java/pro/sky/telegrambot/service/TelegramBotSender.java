package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TelegramBotSender {

    private final Logger logger = (Logger) LoggerFactory.getLogger(TelegramBotSender.class);
    private final TelegramBot telegramBot;

    public TelegramBotSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void send(Long chatId, String message){
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);

        if(!response.isOk()) {
            logger.error("Error occured during the sending message, response = {}", response);
        }else{
            logger.info("Message was successfully sent to chatId = {}", chatId);
        }
}
}
