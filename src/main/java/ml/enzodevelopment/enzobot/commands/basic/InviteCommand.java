package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InviteCommand implements Command{

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invite Me");
        builder.setColor(Config.ENZO_BLUE);
        builder.setDescription("Use the provided invite link to add me to your server.");
        builder.addField("To invite me:", "[Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591)", false);
        try {
            event.getAuthor().openPrivateChannel().queue((priv -> priv.sendMessage(builder.build()).queue(null, failure -> event.getChannel().sendMessage(event.getMember().getAsMention() + " Oh no i couldn't DM you please check your privacy settings and ensure you haven't blocked me.").queue())));
        } catch (InsufficientPermissionException e) {
            return;
        }
    }

    @Override
    public String getUsage() {
        return "invite";
    }

    @Override
    public String getDesc() {
        return "Replies with a link to add EnzoBot to your server.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("invite"));
    }

    @Override
    public String getType() {
        return "public";
    }
}
