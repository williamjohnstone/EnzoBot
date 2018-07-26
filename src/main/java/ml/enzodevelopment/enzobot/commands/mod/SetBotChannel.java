package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.utils.Config;
import ml.enzodevelopment.enzobot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetBotChannel implements Command {

    private static Logger logger = LoggerFactory.getLogger(GuildConfig.class.getName());
    private Connection conn = Config.DB.getConnManager().getConnection();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = event.getMember().hasPermission(Permission.ADMINISTRATOR);
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        Config.DB.run(() -> {
            String channel = "0";
            if (args.length == 2) {
                if (args[1].equals("off")) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Bot Channel Removed");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("Success");
                    event.getChannel().sendMessage(builder.build()).queue();
                    channel = "0";
                } else {
                    channel = event.getMessage().getMentionedChannels().get(0).getId();
                }
            } else if (args.length == 1) {
                channel = event.getMessage().getChannel().getId();
            }
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE `Config` SET `bot_Channel_ID` = ? WHERE `Config`.`guild_ID` = ?;")) {
                stmt.setString(1, channel);
                stmt.setString(2, event.getGuild().getId());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
            }
            if (!"0".equals(channel)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Channel Set");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success, bot channel set to: " + event.getGuild().getTextChannelById(channel).getAsMention());
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Channel Removed");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        });
    }

    @Override
    public String getUsage() {
        return "setBotChat (#Channel) or 'setBotChat off' to disable bot channel.";
    }

    @Override
    public String getDesc() {
        return "Changes the channel the bot uses this channel is used for all commands.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setbotchat", "botchannel", "setbotchannel"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
