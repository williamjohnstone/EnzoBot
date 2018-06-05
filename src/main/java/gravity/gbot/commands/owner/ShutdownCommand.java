package gravity.gbot.commands.owner;

import gravity.gbot.BuildConfig;
import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ShutdownCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Shutting down Gbot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0);
        }
    }

    @Override
    public String getUsage() {
        return "shutdown";
    }

    @Override
    public String getDesc() {
        return "Shuts Down the Gbot";
    }

    @Override
    public String getAlias() {
        return "shutdown";
    }

    @Override
    public String getType() {
        return "owner";
    }
}
