package gravity.gbot.commands.BotOwner;

import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GiveRole implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 2) {
        if (event.getAuthor().getId().equals("205056315351891969")) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(args[1].replace("<@&", "").replace(">", ""))).queue();
            }
        }
    }

    @Override
    public String cmdUsage() {
        return "giveRole @role";
    }

    @Override
    public String cmdDesc() {
        return "Gives the mentioned role";
    }

    @Override
    public String getAlias() {
        return "giveRole";
    }

    @Override
    public String cmdType() {
        return "owner";
    }
}
