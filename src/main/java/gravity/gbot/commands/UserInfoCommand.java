package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import java.time.format.DateTimeFormatter;

public class UserInfoCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        Member member = event.getMessage().getMentionedMembers().get(0);
        String bot;
        String time = member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        String joined = member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        if (member.getUser().isBot()) {
            bot = "Yes";
        } else {
            bot = "No";
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("User Info");
        builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        builder.setColor(member.getColor());
        builder.setDescription(member.getAsMention());
        builder.addField("Username + Discriminator", member.getUser().getName() + member.getUser().getDiscriminator(), true);
        builder.addField("Display Name", member.getEffectiveName(), true);
        builder.addField("Online Status", member.getOnlineStatus().toString(), true);
        builder.addField("Status", member.getGame().toString(), true);
        builder.addField("Account Created", time, true);
        builder.addField("Joined Server", joined, true);
        builder.addField("Bot?", bot, true);
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String cmdUsage() {
        return "userInfo (@user)";
    }

    @Override
    public String cmdDesc() {
        return "Displays info on specified user.";
    }

    @Override
    public String getAlias() {
        return "userinfo";
    }

    @Override
    public String cmdType() {
        return "public";
    }


}
