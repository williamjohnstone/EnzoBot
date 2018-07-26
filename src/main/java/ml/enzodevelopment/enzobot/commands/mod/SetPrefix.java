package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.utils.Config;
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

public class SetPrefix implements Command {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private Connection conn = Config.DB.getConnManager().getConnection();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length < 2) {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setColor(Config.ENZO_BLUE);
            error.setDescription("Invalid Usage");
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setColor(Config.ENZO_BLUE);
            error.setDescription("You do not have permission to do that.");
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        String prefix = args[1];
        Config.DB.run(() -> {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE `Config` SET `Prefix` = ? WHERE `Config`.`guild_ID` = ?;")) {
                stmt.setString(1, prefix);
                stmt.setString(2, event.getGuild().getId());
                stmt.executeUpdate();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Prefix Set");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success");
                event.getChannel().sendMessage(builder.build()).queue();
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
            }
        });
    }

    @Override
    public String getUsage() {
        return "setPrefix (Prefix)";
    }

    @Override
    public String getDesc() {
        return "Changes the Bots Prefix";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setprefix", "prefix"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
