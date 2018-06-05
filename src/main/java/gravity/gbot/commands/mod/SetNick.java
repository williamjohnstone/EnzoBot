package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;


public class SetNick implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (adminCheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        event.getGuild().getController().setNickname(event.getGuild().getSelfMember(), args[1]).queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Nickname Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "setNick (Nickname)";
    }

    @Override
    public String getDesc() {
        return "Changes the Bots Nickname";
    }

    @Override
    public String getAlias() {
        return "setnick";
    }

    @Override
    public String getType() {
        return "admin";
    }
}
