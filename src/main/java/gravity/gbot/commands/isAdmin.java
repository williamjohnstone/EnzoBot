package gravity.gbot.commands;

import gravity.gbot.Command;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class isAdmin implements Command {

    private GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String userTest = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (userTest == null) {
            event.getChannel().sendMessage("user is not in admin list").queue();
        } else {
            event.getChannel().sendMessage("user: " + event.getGuild().getMemberById(userTest).getAsMention() + " is on the admin list!").queue();
        }
    }

    @Override
    public String cmdUsage() {
        return "isAdmin (user)";
    }

    @Override
    public String cmdDesc() {
        return "Checks whether or not the user is in the current admins list.";
    }

    @Override
    public String getAlias() {
        return "isadmin";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}
