package gravity.gbot.commands.basic;

import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteCommand implements Command {

    private Message msg;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        event.getChannel().getMessageById(args[1]).queue(success -> msg = success);
        if (msg != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(event.getGuild().getMember(msg.getAuthor()).getColor());
            builder.setDescription(msg.getContentRaw());
            builder.setAuthor(msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator(), null, msg.getAuthor().getEffectiveAvatarUrl());
            builder.setTimestamp(msg.getCreationTime());
            builder.setFooter("#" + msg.getChannel().getName() + " | Sent", null);
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            event.getChannel().sendMessage("Invalid message ID: `" + args[1] + "`").queue();
        }
    }

    @Override
    public String getUsage() {
        return "quote (MessageID)";
    }

    @Override
    public String getDesc() {
        return "Quotes the supplied message.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("quote"));
    }

    @Override
    public String getType() {
        return "public";
    }
}
