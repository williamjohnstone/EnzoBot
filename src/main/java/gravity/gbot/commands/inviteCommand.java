package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class inviteCommand implements Command{

    final String DESC = "Replies with a link to add Gbot to your server.";
    final String USAGE = "invite";
    final String ALIAS = "invite";
    private final String type = "public";

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Invite Me");
        builder.setDescription("Use the provided invite link to add me to your server.");
        builder.addField("To invite me:", "[Click Here](https://discordapp.com/oauth2/authorize?client_id=391558265265192961&scope=bot&permissions=2146958591)", false);
        event.getAuthor().openPrivateChannel().queue((priv -> {
            priv.sendMessage(builder.build()).queue();
        }));

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
}
