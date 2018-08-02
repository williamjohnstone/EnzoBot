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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Credit goes to Duncte123 original source here: https://github.com/DuncteBot/SkyBot/blob/dev/src/main/java/ml/duncte123/skybot/commands/essentials/TokenCommand.java

public class TokenCommand implements Command {

    private static final Pattern TOKEN_REGEX = Pattern.compile("([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)\\.([a-zA-Z0-9\\-_]+)");
    private static final long TOKEN_EPOCH = 1293840000L;
    private static final String STRING_FORMAT = "Deconstruction results for token: `%s`%n%n" +
            "**ID:** %s%n**Generated:** %s%n%n" +
            "Checking validity...%s%n%n" +
            "Keep in mind that verifying if the token is valid by making a request to discord is against the TOS";

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        if (args.length == 1) {
            return;
        }

        Matcher matcher = TOKEN_REGEX.matcher(args[1]);

        if (!matcher.matches()) {
            event.getChannel().sendMessage("Your input `" + args[1] + "` has the wrong token format.").queue();
            return;
        }

        String id = decodeBase64ToString(matcher.group(1));
        String timestamp = toTimeStamp(matcher.group(2)).format(DateTimeFormatter.RFC_1123_DATE_TIME);

        event.getChannel().sendMessage(String.format(STRING_FORMAT, args[1], id, timestamp, "")).queue((message) -> {
            try {
                event.getJDA().retrieveUserById(id).queue((user) -> {
                    String userinfo = String.format("%n%nToken has a valid structure. It belongs to **%#s** (%s).", user, user.getId());
                    String newMessage = String.format(STRING_FORMAT, args[1], id, timestamp, userinfo);
                    message.editMessage(newMessage).queue();
                }, (error) -> {
                    String info = String.format("%n%nToken is not valid or the account has been deleted (%s)", error.getMessage());
                    String newMessage = String.format(STRING_FORMAT, args[1], id, timestamp, info);
                    message.editMessage(newMessage).queue();
                });
            } catch (NumberFormatException e) {
                String info = String.format("%n%nThat token does not have a valid structure (%s)", e.getMessage());
                String newMessage = String.format(STRING_FORMAT, args[1], id, timestamp, info);
                message.editMessage(newMessage).queue();
            }
        });
    }

    @Override
    public String getUsage() {
        return "token (token)";
    }

    @Override
    public String getDesc() {
        return "Displays info about a discord token.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("token"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }

    private byte[] decodeBase64(String input) {
        return Base64.getDecoder().decode(input);
    }

    private String decodeBase64ToString(String input) {
        return new String(decodeBase64(input));
    }

    private OffsetDateTime toTimeStamp(String input) {
        BigInteger decoded = new BigInteger(decodeBase64(input));
        long receivedTime = Long.valueOf(decoded.toString());

        long timestamp = TOKEN_EPOCH + receivedTime;

        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long millis = timestamp * 1000;
        gmt.setTimeInMillis(millis);

        return OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId());
    }
}
