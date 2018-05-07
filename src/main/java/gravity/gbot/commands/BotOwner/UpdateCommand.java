package gravity.gbot.commands.BotOwner;

import gravity.bot.BuildConfig;
import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class UpdateCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.ownerId)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Updating Gbot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0x89);
        }
    }

    @Override
    public String cmdUsage() {
        return "Update";
    }

    @Override
    public String cmdDesc() {
        return "Updates Gbot";
    }

    @Override
    public String getAlias() {
        return "update";
    }

    @Override
    public String cmdType() {
        return "owner";
    }
}
