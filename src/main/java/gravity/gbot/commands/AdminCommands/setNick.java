package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;


public class setNick implements Command {

    private final String Usage = "setNick (Nickname)";
    private final String Desc = "Changes the Bots Nickname";
    private final String Alias = "setnick";
    private final String type = "admin";

    Config config = new Config();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        event.getGuild().getController().setNickname(event.getGuild().getSelfMember(), args[1]).queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Nickname Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
        event.getMessage().delete().queue();
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
