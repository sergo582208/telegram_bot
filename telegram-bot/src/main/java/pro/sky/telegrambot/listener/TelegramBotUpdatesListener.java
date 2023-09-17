package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotSender;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final static Pattern INCOMING_MESSAGE_PATTERN =
            Pattern.compile("([0-9\\.\\:\\s]{16})(\s)([\\W+]+)");
private final static DateTimeFormatter NOTIFICATION_DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;
    private final TelegramBotSender telegramBotSender;

    private final NotificationTaskRepository notificationTaskRepository;


    public TelegramBotUpdatesListener(TelegramBot telegramBot, TelegramBotSender telegramBotSender,
                                      NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.telegramBotSender = telegramBotSender;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            Long chatId = update.message().chat().id();

            if ("/start".equals(text)) {
                telegramBotSender.send(chatId, "Привет, мой друг");
            }else {
                Matcher matcher = INCOMING_MESSAGE_PATTERN.matcher(text);
                if(matcher.matches()){


                    String dateTimeString = matcher.group(1);
                    String notificationText = matcher.group(3);
                    notificationTaskRepository.save(
                            new NotificationTask(chatId, notificationText,
                                    LocalDateTime.parse(dateTimeString, NOTIFICATION_DATE_TIME_FORMAT))
                    );

                    telegramBotSender.send(chatId, "Нотификация успешно сохранена!");
                }else{
                    telegramBotSender.send(chatId, "Формат неверный !");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
