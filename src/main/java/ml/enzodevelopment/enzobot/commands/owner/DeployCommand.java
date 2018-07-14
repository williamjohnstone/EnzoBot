package ml.enzodevelopment.enzobot.commands.owner;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.utils.Config;
import ml.enzodevelopment.enzobot.BuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (Config.dev_mode) {
            return;
        }
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Deploying Enzobot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0x29);
        }
    }

    @Override
    public String getUsage() {
        return "deploy";
    }

    @Override
    public String getDesc() {
        return "Deploys test bot version to production";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("deploy"));
    }

    @Override
    public String getType() {
        return "owner";
    }
}
