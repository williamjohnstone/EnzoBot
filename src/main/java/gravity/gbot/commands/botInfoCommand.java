package gravity.gbot.commands;

import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class botInfoCommand implements Command {

    private RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
    private long uptimems = mxBean.getUptime();
    private long s = uptimems / 1000;
    private long m = s / 60;
    private long h = m / 60;
    private long d = h / 24;
    private String uptimeString = d + "d " + h % 24 + "h " + m % 60 + "m " + s % 60 + "s";
    private int userCnt = 0;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        for (Guild g : event.getJDA().getGuildCache()) {
            userCnt =+ g.getMembers().size();
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Info");
        builder.setColor(Color.cyan);
        builder.setImage(event.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl());
        builder.addField("Username", event.getGuild().getSelfMember().getEffectiveName(), true);
        builder.addField("Discriminator", event.getGuild().getSelfMember().getUser().getDiscriminator(), true);
        builder.addField("Commands", String.valueOf(Main.cmdlist.size()), true);
        builder.addField("Server Count", String.valueOf(event.getJDA().getGuildCache().size()), true);
        builder.addField("User Count", String.valueOf(userCnt), true);
        builder.addField("Version", Config.version, true);
        builder.addField("Latest Github Commit", Config.gh_commit, true);
        builder.addField("Uptime", uptimeString, true);
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String cmdUsage() {
        return "botInfo";
    }

    @Override
    public String cmdDesc() {
        return "Displays info about Gbot.";
    }

    @Override
    public String getAlias() {
        return "botinfo";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}
