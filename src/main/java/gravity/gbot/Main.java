package gravity.gbot;

import gravity.gbot.music.MusicMaps;
import gravity.gbot.music.PlayerControl;
import gravity.gbot.commands.basic.*;
import gravity.gbot.commands.mod.*;
import gravity.gbot.commands.owner.*;
import gravity.gbot.utils.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

import io.sentry.Sentry;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;

public class Main {

    public static List<Command> cmdlist = new ArrayList<>();

    public static void main(String[] args) {
        RestAction.DEFAULT_FAILURE = t ->
        {
            Logger LOG = JDALogger.getLog(RestAction.class);
            if (t instanceof ErrorResponseException && ((ErrorResponseException) t).getErrorCode() != 10008) {
                if (LOG.isDebugEnabled()) {
                    LOG.error("RestAction queue returned failure", t);
                } else {
                    LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage());
                }
            }
        };


        if (args.length > 0) {
            if (args[0].equals("--dev")) {
                Config.dev_mode = true;
                System.out.println("[INFO] Running in Development Mode");
            }
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
            cmdlist.add(new SetRole());
            cmdlist.add(new Eval());
            cmdlist.add(new SetNick());
            cmdlist.add(new SetPrefix());
            cmdlist.add(new SetBotChannel());
            cmdlist.add(new AddBotAdmin());
            cmdlist.add(new RemoveBotAdmin());
            cmdlist.add(new IsAdminCommand());
            cmdlist.add(new InviteCommand());
            cmdlist.add(new BotInfoCommand());
            cmdlist.add(new UserInfoCommand());
            cmdlist.add(new GuildInfoCommand());
            //cmdlist.add(new TestKick());
            cmdlist.add(new ShutdownCommand());
            cmdlist.add(new UpdateCommand());
            cmdlist.add(new RestartCommand());
            cmdlist.add(new DeployCommand());
            cmdlist.add(new QuoteCommand());
            cmdlist.add(new RollCommand());
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
