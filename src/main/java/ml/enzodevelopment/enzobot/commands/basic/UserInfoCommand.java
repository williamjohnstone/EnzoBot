/*
 * Enzo Bot, a multipurpose discord bot
 *
 * Copyright (c) 2018 William "Enzo" Johnstone
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        Member member;
        if (event.getMessage().getMentionedMembers().size() != 0) {
            member = event.getMessage().getMentionedMembers().get(0);
        } else {
            member = event.getGuild().getMember(event.getAuthor());
        }
        boolean isBot = member.getUser().isBot();
        String time = member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        String joinDate = member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("User Info");
        builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        builder.setColor(member.getColor());
        builder.setDescription(member.getAsMention());
        builder.addField("Name + Discriminator", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true);
        builder.addField("Display Name", member.getEffectiveName(), true);
        builder.addField("Online Status", member.getOnlineStatus().toString(), true);
        builder.addField("Status", getGameStatus(member.getGame()), true);
        builder.addField("Account Created", time, true);
        builder.addField("Joined Server", joinDate, true);
        builder.addField("User ID", member.getUser().getId(), true);
        builder.addField("Bot?", (isBot ? "Yes" : "No"), true);
        builder.addField("Join Order", getOrder(event, member.getUser()), true);
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "userInfo (@user)";
    }

    @Override
    public String getDesc() {
        return "Displays info on specified user.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("userinfo"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }

    private static String getGameStatus(Game game) {
        if (game == null) return "Idle";

        String type;
        switch (game.getType().getKey()) {
            case 1:
                type = "Streaming";
                break;
            case 2:
                type = "Listening to";
                break;
            case 3:
                type = "Watching";
                break;
            default:
                type = "Playing";
                break;
        }

        String gameName = game.getName();
        return type + " " + gameName;
    }

    private static String getOrder(GuildMessageReceivedEvent event, User u) {
        StringBuilder joinOrder = new StringBuilder();
        List<Member> joins = event.getGuild().getMemberCache().stream().sorted(Comparator.comparing(Member::getJoinDate)).collect(Collectors.toList());
        Member m = event.getGuild().getMember(u);
        int index = joins.indexOf(m);
        index -= 3;
        if (index < 0)
            index = 0;
        if (joins.get(index).getUser() == u)
            joinOrder.append("[").append(joins.get(index).getEffectiveName()).append("](https://g-bot.tk/)");
        else
            joinOrder.append(joins.get(index).getEffectiveName());
        for (int i = index + 1; i < index + 7; i++) {
            if (i >= joins.size())
                break;
            User user = joins.get(i).getUser();
            String usrName = user.getName();
            if (u.equals(user))
                usrName = "[" + usrName + "](https://g-bot.tk/)";
            joinOrder.append(" > ").append(usrName);
        }
        return joinOrder.toString();
    }

}
