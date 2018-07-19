package ml.enzodevelopment.enzobot;

import ml.enzodevelopment.enzobot.commands.basic.*;
import ml.enzodevelopment.enzobot.commands.mod.*;
import ml.enzodevelopment.enzobot.commands.music.*;
import ml.enzodevelopment.enzobot.commands.owner.*;
import ml.enzodevelopment.enzobot.music.PlayerControl;
import ml.enzodevelopment.enzobot.utils.BotListener;
import ml.enzodevelopment.enzobot.utils.Config;
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
        config.loadConfig();
        Sentry.init(Config.sentry_dsn);
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .addEventListener(new BotListener())
                .addEventListener(new PlayerControl())
                .setToken(Config.Discord_Token)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE);

        try {
            JDA jda = builder.buildBlocking();
            jda.getPresence().setGame(Game.watching(jda.getGuildCache().size() + " servers! | !help"));
            cmdlist.add(new PingCommand());
            cmdlist.add(new HelpCommand());
            cmdlist.add(new GiveRole());
            cmdlist.add(new SetRole());
            cmdlist.add(new Eval());
            cmdlist.add(new SetPrefix());
            cmdlist.add(new SetBotChannel());
            cmdlist.add(new IsAdminCommand());
            cmdlist.add(new InviteCommand());
            cmdlist.add(new BotInfoCommand());
            cmdlist.add(new UserInfoCommand());
            cmdlist.add(new GuildInfoCommand());
            cmdlist.add(new ShutdownCommand());
            cmdlist.add(new UpdateCommand());
            cmdlist.add(new RestartCommand());
            cmdlist.add(new DeployCommand());
            cmdlist.add(new QuoteCommand());
            cmdlist.add(new RollCommand());
            cmdlist.add(new CoinFlipCommand());
            //Music Commands
            cmdlist.add(new PlayCommand());
            cmdlist.add(new LeaveCommand());
            cmdlist.add(new NowPlayingCommand());
            cmdlist.add(new PauseCommand());
            cmdlist.add(new QueueCommand());
            cmdlist.add(new RepeatCommand());
            cmdlist.add(new ResetCommand());
            cmdlist.add(new ResumeCommand());
            cmdlist.add(new ReplayCommand());
            cmdlist.add(new SeekCommand());
            cmdlist.add(new ShuffleCommand());
            cmdlist.add(new SkipCommand());
            cmdlist.add(new StopCommand());
            cmdlist.add(new VolumeCommand());

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
