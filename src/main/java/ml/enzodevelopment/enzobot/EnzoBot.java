package ml.enzodevelopment.enzobot;

import ml.enzodevelopment.enzobot.commands.basic.*;
import ml.enzodevelopment.enzobot.commands.mod.*;
import ml.enzodevelopment.enzobot.commands.music.*;
import ml.enzodevelopment.enzobot.commands.owner.*;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.config.Config;
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

public class EnzoBot {

    public static List<Command> cmdList = new ArrayList<>();

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
                .setToken(Config.Discord_Token)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE);

        try {
            cmdList.add(new PingCommand());
            cmdList.add(new HelpCommand());
            cmdList.add(new SetRole());
            cmdList.add(new Eval());
            cmdList.add(new SetPrefix());
            cmdList.add(new SetBotChannel());
            cmdList.add(new InviteCommand());
            cmdList.add(new BotInfoCommand());
            cmdList.add(new UserInfoCommand());
            cmdList.add(new GuildInfoCommand());
            cmdList.add(new ShutdownCommand());
            cmdList.add(new UpdateCommand());
            cmdList.add(new RestartCommand());
            cmdList.add(new DeployCommand());
            cmdList.add(new QuoteCommand());
            cmdList.add(new RollCommand());
            cmdList.add(new CoinFlipCommand());
            cmdList.add(new RevokeAccessCommand());
            //Music Commands
            cmdList.add(new PlayCommand());
            cmdList.add(new LeaveCommand());
            cmdList.add(new NowPlayingCommand());
            cmdList.add(new PauseCommand());
            cmdList.add(new QueueCommand());
            cmdList.add(new RepeatCommand());
            cmdList.add(new ResetCommand());
            cmdList.add(new ResumeCommand());
            cmdList.add(new ReplayCommand());
            cmdList.add(new SeekCommand());
            cmdList.add(new ShuffleCommand());
            cmdList.add(new SkipCommand());
            cmdList.add(new StopCommand());
            cmdList.add(new VolumeCommand());
            JDA jda = builder.buildBlocking();
            jda.getPresence().setGame(Game.watching(jda.getGuildCache().size() + " servers! | !help"));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
