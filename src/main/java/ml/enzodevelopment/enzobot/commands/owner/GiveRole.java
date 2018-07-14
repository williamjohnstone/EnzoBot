package ml.enzodevelopment.enzobot.commands.owner;

import ml.enzodevelopment.enzobot.BuildConfig;
import ml.enzodevelopment.enzobot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("giverole"));
    }

    @Override
    public String getType() {
        return "owner";
    }
}
