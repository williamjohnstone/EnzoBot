package gravity.gbot.commands;

import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class HelpCommand implements Command {

    Config config = new Config();

    private final String USAGE = "Help or help (command)";
    private final String DESC = "Sends you a private message containing help.";
    private final String ALIAS = "help";
    private final String type = "public";

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        String bot_prefix = config.getPrefix(event.getGuild().getId());
        EmbedBuilder builder0 = new EmbedBuilder();

        builder0.setTitle("Help");
        builder0.setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl());
        builder0.setDescription("Here are all the commands currently available.");

        EmbedBuilder builder1 = new EmbedBuilder();
        builder1.setTitle("Basic Commands");

        EmbedBuilder builder2 = new EmbedBuilder();
        builder2.setTitle("Admin Commands");

        EmbedBuilder builder3 = new EmbedBuilder();
        builder3.setTitle("Bot Owner Commands");

        for (Command command : Main.cmdlist) {
            if (command.cmdType().equals("public")) {
                builder1.addField(bot_prefix + command.getAlias(), "**Usage:** *" + bot_prefix + command.cmdUsage() + "*, **Description:** *" + command.cmdDesc() + "*", false);
            }
            if (command.cmdType().equals("admin")) {
                builder2.addField(bot_prefix + command.getAlias(), "**Usage:** *" + bot_prefix + command.cmdUsage() + "*, **Description:** *" + command.cmdDesc() + "*", false);
            }
            if (command.cmdType().equals("owner")) {
                builder3.addField(bot_prefix + command.getAlias(), "**Usage:** *" + bot_prefix + command.cmdUsage() + "*, **Description:** *" + command.cmdDesc() + "*", false);
            }
        }


        if (event.getChannelType() == ChannelType.PRIVATE) {
            event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder0.build()).queue());
            event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder1.build()).queue());
            if (admincheck != null) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder2.build()).queue());
            }
            if (event.getAuthor().getId().equals("205056315351891969")) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder3.build()).queue());
            }
        } else if (event.getChannelType() == ChannelType.TEXT) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I sent you a DM containing help. :mailbox_with_mail:").queue();
            event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder0.build()).queue());
            event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder1.build()).queue());
            if (admincheck != null) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder2.build()).queue());
            }
            if (event.getAuthor().getId().equals("205056315351891969")) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder3.build()).queue());
            }
            event.getMessage().delete().queue();
        }
    }

    public void HelpSpecific(String[] args, GuildMessageReceivedEvent event, String desc, String help, String alias) {
        String bot_prefix = config.getPrefix(event.getGuild().getId());
        if (args.length < 2) {
            return;
        } else if (args.length > 2) {
            return;
        }
        if (alias.equals(help)) {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle(bot_prefix + alias)
                    .setDescription(desc)
                    .setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl())
                    .setColor(Color.GREEN)
                    .addField("Usage:", bot_prefix + "Help or " + bot_prefix + "help (command)", true)
                    .addField("Want me in your server?", "Hey!, want to add me to your server? [Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591) to invite me to your server.", false);
            if (event.getChannelType() == ChannelType.PRIVATE) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("This command is guild only").queue());
            } else if (event.getChannelType() == ChannelType.TEXT) {
                event.getChannel().sendMessage(builder.build()).queue();
            }

        } else {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle(bot_prefix + alias)
                    .setDescription(desc)
                    .setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl())
                    .setColor(Color.GREEN)
                    .addField("Usage:", bot_prefix + help, true)
                    .addField("Want me in your server?", "Hey!, want to add me to your server? [Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591) to invite me to your server.", false);
            if (event.getChannelType() == ChannelType.PRIVATE) {
                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("This command is guild only").queue());
            } else if (event.getChannelType() == ChannelType.TEXT) {
                event.getChannel().sendMessage(builder.build()).queue();
            }
        }
    }


    @Override
    public String cmdUsage() {
        return USAGE;
    }

    @Override
    public String cmdDesc() {
        return DESC;
    }

    @Override
    public String getAlias() {
        return ALIAS;
    }

    @Override
    public String cmdType() {
        return type;
    }


    //public void NOcmd(MessageReceivedEvent event) {
    //    String bot_prefix = config.getPrefix(event.getGuild().getId());
    //    EmbedBuilder builder = new EmbedBuilder();
//
    //   builder.setTitle("Error!")
    //           .setDescription("Oh no not again!")
    //           .setAuthor(event.getAuthor().getName(), "https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591", event.getAuthor().getAvatarUrl())
    //           .setColor(Color.RED);
    //   builder.addField("Sorry i didn't recognise '" + event.getMessage().getContentDisplay().replace(bot_prefix + "help ", "") + "'", "If you need help send " + bot_prefix + "help or you can send " + bot_prefix + "help <command> for more info on that specific command.", true);
    //   builder.addField("Want me in your server?", "Hey!, want to add me to your server? [Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591) to invite me to your server.", false);
    //   if (event.getChannelType() == ChannelType.TEXT) {
    //       event.getChannel().sendMessage(builder.build()).queue();
    //   } else if (event.getChannelType() == ChannelType.PRIVATE) {
    //       event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(builder.build()).queue());
    //   }
    //}
}