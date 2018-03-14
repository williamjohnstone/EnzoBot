package gravity.gbot.commands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class isAdmin implements Command {

    final String Usage = "isAdmin (user)";
    final String Desc = "Checks whether or not the user is in the current admins list.";
    final String Alias = "isadmin";
    private final String type = "public";

    GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String userTest = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        if (userTest == null) {
            event.getChannel().sendMessage("user is not in admin list").queue();
        } else {
            event.getChannel().sendMessage("user: " + event.getGuild().getMemberById(userTest).getAsMention() + " is on the admin list!").queue();
        }
    }

    @Override
    public String cmdUsage() {
        return Usage;
    }

    @Override
    public String cmdDesc() {
        return Desc;
    }

    @Override
    public String getAlias() {
        return Alias;
    }

    @Override
    public String cmdType() {
        return type;
    }
}
