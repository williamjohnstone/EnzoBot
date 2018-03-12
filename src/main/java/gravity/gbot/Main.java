package gravity.gbot;

import gravity.gbot.Music.PlayerControl;
import gravity.gbot.utils.*;
import gravity.gbot.commands.AdminCommands.*;
import gravity.gbot.commands.*;
import gravity.gbot.commands.BotOwner.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static JDA jda;

    public static List<Command> cmdlist = new ArrayList<>();


    public static void main(String[] args) {
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .addEventListener(new BotListener())
                .addEventListener(new PlayerControl())
                .setToken(Config.Discord_Token)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE);

        try {
            jda = builder.buildBlocking();
            jda.getPresence().setGame(Game.watching(jda.getGuildCache().size() + " servers! | g-bot.tk"));
            cmdlist.add(new PingCommand());
            cmdlist.add(new HelpCommand());
            cmdlist.add(new GiveRole());
            cmdlist.add(new setRole());
            cmdlist.add(new Eval());
            cmdlist.add(new setNick());
            cmdlist.add(new setPrefix());
            cmdlist.add(new setBotChannel());
            cmdlist.add(new addBotAdmin());
            cmdlist.add(new removeBotAdmin());
            cmdlist.add(new isAdmin());
            cmdlist.add(new sayCommand());
            cmdlist.add(new inviteCommand());
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
