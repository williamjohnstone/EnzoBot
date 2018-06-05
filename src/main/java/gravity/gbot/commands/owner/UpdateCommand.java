package gravity.gbot.commands.owner;

import gravity.gbot.BuildConfig;
import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class UpdateCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!Config.dev_mode) {
            return;
        }
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
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
    public String getUsage() {
        return "update";
    }

    @Override
    public String getDesc() {
        return "Updates Gbot";
    }

    @Override
    public String getAlias() {
        return "update";
    }

    @Override
    public String getType() {
        return "owner";
    }
}
