package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class GuildInfoCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        Guild g = event.getGuild();
        String time = g.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        int usercnt = (int)g.getMemberCache().size();
        int active = 0;
        int bots = 0;
        for (Member mem : g.getMemberCache()) {
            if (mem.getUser().isBot()) {
                bots++;
            }
            if (!mem.getUser().isBot() && mem.getOnlineStatus() != OnlineStatus.OFFLINE) {
                active++;
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(g.getName());
        builder.setThumbnail(g.getIconUrl());
        builder.setDescription("Guild Info for " + g.getName());
        builder.addField("Owner", g.getOwner().getAsMention(), true);
        builder.addField("Region", g.getRegion().getName(), true);
        builder.addField("Date Created", time, true);
        builder.addField("Users", usercnt + " (" + active + " Online, " + bots + " bots)", true);
        event.getChannel().sendMessage(builder.build()).queue();

    }

    @Override
    public String cmdUsage() {
        return "guildInfo";
    }

    @Override
    public String cmdDesc() {
        return "Displays info about the current guild.";
    }

    @Override
    public String getAlias() {
        return "guildinfo";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}
