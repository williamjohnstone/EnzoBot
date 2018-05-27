package gravity.gbot.commands.owner;

import gravity.bot.BuildConfig;
import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class DeployCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (Config.dev_mode) {
            return;
        }
        if (!event.getAuthor().getId().equals(BuildConfig.ownerId)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Deploying Gbot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0x29);
        }
    }

    @Override
    public String cmdUsage() {
        return "Deploy";
    }

    @Override
    public String cmdDesc() {
        return "Deploys test bot version to production";
    }

    @Override
    public String getAlias() {
        return "deploy";
    }

    @Override
    public String cmdType() {
        return "owner";
    }
}
