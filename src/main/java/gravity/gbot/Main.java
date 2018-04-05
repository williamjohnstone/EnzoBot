package gravity.gbot;

import gravity.gbot.Music.MusicMaps;
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

import io.sentry.Sentry;

public class Main {

    public static List<Command> cmdlist = new ArrayList<>();

    public static void main(String[] args) {
        if (args[0].equals("--dev")) {
            Config.dev_mode = true;
            System.out.println("[INFO] Running in Development Mode");
        } else {
            Config.config_file = "config.json";
        }
        Config config = new Config();
        MusicMaps mcmds = new MusicMaps();
        config.loadConfig();
        Sentry.init(Config.sentry_dsn);
        mcmds.add();
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .addEventListener(new BotListener())
                .addEventListener(new PlayerControl())
                .setToken(Config.Discord_Token)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE);

        try {
            JDA jda = builder.buildBlocking();
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
            cmdlist.add(new inviteCommand());
            cmdlist.add(new botInfoCommand());
            cmdlist.add(new UserInfoCommand());
            cmdlist.add(new GuildInfoCommand());
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
