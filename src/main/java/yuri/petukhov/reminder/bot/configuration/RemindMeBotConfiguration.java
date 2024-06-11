package yuri.petukhov.reminder.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yuri.petukhov.reminder.business.enums.Command;

@Configuration
@Getter
public class RemindMeBotConfiguration {

    @Value("${kanji.bot.token}")
    private String token;
    @Value("${MENU_MES}")
    private String MENU_MES;
    @Value("${WEB_LINK}")
    private String WEB_LINK;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        bot.execute(new SetMyCommands(
                new BotCommand(Command.START.getName(), Command.START.getName())
        ));
        return bot;
    }

}

