package ml.enzodevelopment.enzobot.commands.owner;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.BuildConfig;
import ml.enzodevelopment.enzobot.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestartCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Restarting EnzoBot.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getJDA().shutdown();
            System.exit(0x44);
        }
    }

    @Override
    public String getUsage() {
        return "restart";
    }

    @Override
    public String getDesc() {
        return "Restarts EnzoBot";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("restart"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OWNER;
    }
}
