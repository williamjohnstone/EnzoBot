package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.Database;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class SetBotChannel implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
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

        Database db = new Database(Config.dbConnection);
        db.init();
        db.executeUpdate("UPDATE `Config` SET `bot_Channel_ID` = '" + channel + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() + ";");
        db.close();
        if (!"0".equals(channel)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Bot Channel Set");
            builder.setColor(Color.WHITE);
            builder.setDescription("Success");
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Bot Channel Removed");
            builder.setColor(Color.WHITE);
            builder.setDescription("Success");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String getUsage() {
        return "setBotChat (#Channel) or 'setBotChat off' to disable bot channel.";
    }

    @Override
    public String getDesc() {
        return "Changes the channel the bot uses this channel is used for all commands. (Note: Admins bypass this)";
    }

    @Override
    public String getAlias() {
        return "setbotchat";
    }

    @Override
    public String getType() {
        return "admin";
    }
}
