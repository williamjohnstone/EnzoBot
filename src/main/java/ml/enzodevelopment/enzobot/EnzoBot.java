/*
 * Enzo Bot, a multipurpose discord bot
 *
 * Copyright (c) 2018 William "Enzo" Johnstone
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ml.enzodevelopment.enzobot;

import ml.enzodevelopment.enzobot.commands.basic.*;
import ml.enzodevelopment.enzobot.commands.mod.*;
import ml.enzodevelopment.enzobot.commands.music.*;
import ml.enzodevelopment.enzobot.commands.owner.*;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
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

        Config config = new Config();
        config.loadConfig();
        GuildSettingsUtils.loadGuildSettings();
        Sentry.init(Config.sentry_dsn);
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .addEventListener(new BotListener())
                .setToken(Config.Discord_Token)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE);

        try {
            //Basic Commands
            cmdList.add(new PingCommand());
            cmdList.add(new HelpCommand());
            cmdList.add(new InviteCommand());
            cmdList.add(new BotInfoCommand());
            cmdList.add(new UserInfoCommand());
            cmdList.add(new GuildInfoCommand());
            cmdList.add(new QuoteCommand());
            cmdList.add(new RollCommand());
            cmdList.add(new CoinFlipCommand());
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
            //Mod Commands
            cmdList.add(new SetRole());
            cmdList.add(new SetLogChannelCommand());
            cmdList.add(new SetMuteRoleCommand());
            cmdList.add(new SetPrefix());
            cmdList.add(new SetBotChannel());
            cmdList.add(new BanCommand());
            cmdList.add(new TempBanCommand());
            cmdList.add(new UnbanCommand());
            cmdList.add(new SoftbanCommand());
            cmdList.add(new MuteCommand());
            cmdList.add(new TempMuteCommand());
            cmdList.add(new UnmuteCommand());
            cmdList.add(new KickCommand());
            cmdList.add(new WarnCommand());
            cmdList.add(new setWarningThresholdCommand());
            //Owner Commands
            cmdList.add(new ShutdownCommand());
            cmdList.add(new UpdateCommand());
            cmdList.add(new RestartCommand());
            cmdList.add(new DeployCommand());
            cmdList.add(new Eval());
            JDA jda = builder.buildBlocking();
            jda.getPresence().setGame(Game.watching(jda.getGuildCache().size() + " servers! | !help"));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
