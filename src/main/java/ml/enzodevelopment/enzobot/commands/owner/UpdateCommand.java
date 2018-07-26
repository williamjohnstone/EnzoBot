package ml.enzodevelopment.enzobot.commands.owner;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.BuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!Config.dev_mode) {
            if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
                event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.white);
                builder.setTitle("Info");
                builder.setDescription("Updating EnzoBot.");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getJDA().shutdown();
                System.exit(0x59);
            }
            return;
        }
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.white);
            builder.setTitle("Info");
            builder.setDescription("Updating EnzoBot.");
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
        return "Updates EnzoBot";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("update"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OWNER;
    }
}
