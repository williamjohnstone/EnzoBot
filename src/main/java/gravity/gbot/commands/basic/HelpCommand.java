package gravity.gbot.commands.basic;

import gravity.gbot.BuildConfig;
import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.music.MusicMaps;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gravity.gbot.utils.BotListener.getCommand;

public class HelpCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 1) {
            boolean adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
            String botPrefix = GuildConfig.getPrefix(event.getGuild().getId(), this.getClass().getName());
            EmbedBuilder builder0 = new EmbedBuilder();

            builder0.setTitle("Help");
            builder0.setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl());
            builder0.setDescription("Here are all the commands currently available.");
            builder0.setColor(Config.GBOT_BLUE);

            EmbedBuilder builder1 = new EmbedBuilder();
            builder1.setTitle("Basic Commands");
            builder1.setColor(Config.GBOT_BLUE);

            EmbedBuilder builder2 = new EmbedBuilder();
            builder2.setTitle("Music Commands");
            builder2.setColor(Config.GBOT_BLUE);

            EmbedBuilder builder3 = new EmbedBuilder();
            builder3.setTitle("Admin Commands");
            builder3.setColor(Config.GBOT_BLUE);

            EmbedBuilder builder4 = new EmbedBuilder();
            builder4.setTitle("Bot Owner Commands");
            builder4.setColor(Config.GBOT_BLUE);

            for (Command command : Main.cmdlist) {
                if (command.getType() != null) {
                    if (command.getType().equals("public")) {
                        builder1.addField(botPrefix + command.getAliases().get(0), "**Usage:** *" + botPrefix + command.getUsage() + "*\n**Description:** *" + command.getDesc() + "*" + "*\n**Aliases:** *" + getAliasesString(command) + "*", false);
                    }
                    if (command.getType().equals("admin")) {
                        builder3.addField(botPrefix + command.getAliases().get(0), "**Usage:** *" + botPrefix + command.getUsage() + "*\n**Description:** *" + command.getDesc() + "*" + "*\n**Aliases:** *" + getAliasesString(command) + "*", false);
                    }
                    if (command.getType().equals("owner")) {
                        builder4.addField(botPrefix + command.getAliases().get(0), "**Usage:** *" + botPrefix + command.getUsage() + "*\n**Description:** *" + command.getDesc() + "*" + "*\n**Aliases:** *" + getAliasesString(command) + "*", false);
                    }
                }
            }

            for (String command : MusicMaps.musicCmds) {
                String[] commandParts = command.split("\\|");
                String alias = commandParts[0];
                String description = commandParts[1];
                String usage = commandParts[2];
                builder2.addField(botPrefix + "m " + alias, "**Usage:** *" + botPrefix + "m " + usage + "*\n**Description:** *" + description + "*", false);
            }

            if (event.getChannel().getType() == ChannelType.TEXT && event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder0.build()).queue(success -> event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I sent you a DM containing help. :mailbox_with_mail:").queue(), failure -> event.getChannel().sendMessage(event.getMember().getAsMention() + " Oh no i couldn't DM you please check your privacy settings and ensure you haven't blocked me.").queue()));

                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder1.build()).queue(null, failure -> {
                }));
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder2.build()).queue(null, failure -> {
                }));
                if (adminCheck) {
                    event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder3.build()).queue(null, failure -> {
                    }));
                }
                if (event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
                    event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder4.build()).queue(null, failure -> {
                    }));
                }
            }
        } else if (args.length == 2) {
            Command cmd = getCommand(args[1].toLowerCase());
            if (cmd == null) {
                return;
            }
            getSpecififcHelp(args, event, cmd);
        }
    }

    private void getSpecififcHelp(String[] args, GuildMessageReceivedEvent event, Command command) {
        String botPrefix = GuildConfig.getPrefix(event.getGuild().getId(), HelpCommand.class.getName());
        if (args.length < 2) {
            return;
        } else if (args.length > 2) {
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(botPrefix + command.getAliases().get(0))
                .setDescription(command.getDesc())
                .setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl())
                .setColor(Config.GBOT_BLUE)
                .addField("Usage:", botPrefix + command.getUsage(), true)
                .addField("Aliases:", getAliasesString(command), true)
                .addField("Want me in your server?", "Hey!, want to add me to your server? [Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591) to invite me to your server.", false);
        if (event.getChannel().getType() == ChannelType.TEXT) {
            event.getChannel().sendMessage(builder.build()).queue();
        }

    }

    private String getAliasesString(Command command) {
        StringBuilder sb = new StringBuilder();
        for (String alias : command.getAliases()) {
            if (command.getAliases().indexOf(alias) != command.getAliases().size()) {
                sb.append(alias).append(", ");
            } else {
                sb.append(alias);
            }
        }
        return sb.toString();
    }

    @Override
    public String getUsage() {
        return "help or help (command)";
    }

    @Override
    public String getDesc() {
        return "Sends you a private message containing help.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("help"));
    }

    @Override
    public String getType() {
        return "public";
    }
}
