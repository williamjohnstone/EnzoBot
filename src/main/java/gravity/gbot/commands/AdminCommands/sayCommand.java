package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class sayCommand implements Command {

    private final String Usage = "Announce (announcement name|message)";
    private final String Desc = "Takes your supplied message and sends it using the bot account";
    private final String Alias = "announce";
    private final String type = "admin";

    Config config = new Config();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        if(!event.getMessage().getContentRaw().contains("|")) {
            event.getChannel().sendMessage("Incorrect usage" + "%n" + Usage).queue();
            return;
        }
        String[] ann = event.getMessage().getContentRaw().replace(config.getPrefix(event.getGuild().getId()) + "announce ", "").split("\\|");
        if (event.getGuild().getId().equals("388024502921068555")) {
            event.getChannel().sendMessage("@everyone").queue();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("AFP Announcement");
            builder.setColor(Color.WHITE);
            builder.addField(ann[0], ann[1], false);
            event.getChannel().sendMessage(builder.build()).queue();
            event.getMessage().delete().queue();
        }else {
            event.getChannel().sendMessage("@everyone").queue();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Announcement");
            builder.setColor(Color.WHITE);
            builder.addField(ann[0], ann[1], false);
            event.getChannel().sendMessage(builder.build()).queue();
            event.getMessage().delete().queue();
        }


    }

    @Override
    public String cmdUsage() {
        return Usage;
    }

    @Override
    public String cmdDesc() {
        return Desc;
    }

    @Override
    public String getAlias() {
        return Alias;
    }

    @Override
    public String cmdType() {
        return type;
    }
}
