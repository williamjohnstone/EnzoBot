package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class sayCommand implements Command {

    private final String Usage = "Announce (announcement name|message)";

    private GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        if(!event.getMessage().getContentRaw().contains("|")) {
            event.getChannel().sendMessage("Incorrect usage" + "%n" + Usage).queue();
            return;
        }
        String[] ann = event.getMessage().getContentRaw().replace(config.getPrefix(event.getGuild().getId(), this.getClass().getName()) + "announce ", "").split("\\|");
        if (event.getGuild().getId().equals("388024502921068555")) {
            event.getChannel().sendMessage("@everyone").queue();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("AFP Announcement");
            builder.setColor(Color.WHITE);
            builder.addField(ann[0], ann[1], false);
            event.getChannel().sendMessage(builder.build()).queue();
        }else {
            event.getChannel().sendMessage("@everyone").queue();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Announcement");
            builder.setColor(Color.WHITE);
            builder.addField(ann[0], ann[1], false);
            event.getChannel().sendMessage(builder.build()).queue();
        }


    }

    @Override
    public String cmdUsage() {
        return Usage;
    }

    @Override
    public String cmdDesc() {
        return "Takes your supplied message and sends it using the bot account";
    }

    @Override
    public String getAlias() {
        return "announce";
    }

    @Override
    public String cmdType() {
        return "admin";
    }
}
