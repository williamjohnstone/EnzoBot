package gravity.gbot.commands.basic;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class IsAdminCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        User user;
        if (args.length == 1) {
            user = event.getAuthor();
        } else if (args.length == 2) {
            user = event.getMessage().getMentionedMembers().get(0).getUser();
            if (user == null) {
                EmbedBuilder error = new EmbedBuilder();
                error.setTitle("Error");
                error.setDescription("Invalid command usage!");
                error.setColor(Config.GBOT_BLUE);
                event.getChannel().sendMessage(error.build()).queue();
                return;
            }
        } else {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setDescription("Invalid command usage!");
            error.setColor(Config.GBOT_BLUE);
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        boolean adminCheck = GuildConfig.isAdmin(user.getId(), event.getGuild().getId(), event.getJDA());
        EmbedBuilder adminBuilder = new EmbedBuilder();
        adminBuilder.setTitle("");
        adminBuilder.setColor(Config.GBOT_BLUE);
        adminBuilder.setDescription(user.getAsMention() + "is " + (adminCheck ? "an admin." : "not an admin."));
    }

    @Override
    public String getUsage() {
        return "isAdminCommand (user)";
    }

    @Override
    public String getDesc() {
        return "Checks whether or not the user is in the current admins list.";
    }

    @Override
    public String getAlias() {
        return "isadmin";
    }

    @Override
    public String getType() {
        return "public";
    }
}
