package gravity.gbot.commands.owner;

import gravity.bot.BuildConfig;
import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class RestartCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.ownerId)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Restarting Gbot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0x44);
        }
    }

    @Override
    public String cmdUsage() {
        return "restart";
    }

    @Override
    public String cmdDesc() {
        return "Restarts Gbot";
    }

    @Override
    public String getAlias() {
        return "restart";
    }

    @Override
    public String cmdType() {
        return "owner";
    }
}
