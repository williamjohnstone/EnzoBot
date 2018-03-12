package gravity.gbot.commands.BotOwner;

import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GiveRole implements Command {

    private final String USAGE = "giveRole @role";
    private final String DESC = "Gives the mentioned role";
    private final String ALIAS = "giveRole";
    private final String type = "owner";

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 2) {
        if (event.getAuthor().getId().equals("205056315351891969")) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(args[1].replace("<@&", "").replace(">", ""))).queue();
            event.getMessage().delete().queue(); }


        } else event.getMessage().delete().queue();
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
