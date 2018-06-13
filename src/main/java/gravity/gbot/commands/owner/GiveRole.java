package gravity.gbot.commands.owner;

import gravity.gbot.BuildConfig;
import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GiveRole implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 2 && event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(args[1].replace("<@&", "").replace(">", ""))).queue();
        }
    }

    @Override
    public String getUsage() {
        return "giveRole @role";
    }

    @Override
    public String getDesc() {
        return "Gives the mentioned role";
    }

    @Override
    public String getAlias() {
        return "giveRole";
    }

    @Override
    public String getType() {
        return "owner";
    }
}
