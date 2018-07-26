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

public class ShutdownCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Shutting down EnzoBot.");
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
        return "Shuts Down the EnzoBot";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("shutdown"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OWNER;
    }
}
