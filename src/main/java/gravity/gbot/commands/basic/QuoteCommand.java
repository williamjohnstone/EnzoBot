package gravity.gbot.commands.basic;

import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class QuoteCommand implements Command {

    private Message msg;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        event.getChannel().getMessageById(args[1]).queue(success -> msg = success, failure -> msg = null);
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
    public String cmdUsage() {
        return "Quote (MessageID)";
    }

    @Override
    public String cmdDesc() {
        return "Quotes the supplied message.";
    }

    @Override
    public String getAlias() {
        return "quote";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}
