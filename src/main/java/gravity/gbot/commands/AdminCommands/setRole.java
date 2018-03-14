package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;

public class setRole implements Command {

    private final String USAGE = "setRole (@member) (@role)";
    private final String DESC = "Changes the specified member's role.";
    private final String ALIAS = "setrole";
    private final String type = "admin";

    GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        if (event.getChannelType() == ChannelType.PRIVATE | event.getChannelType() == ChannelType.GROUP) {
            event.getChannel().sendMessage("Sorry this is *Guild Only* Command").queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I cant let you do that, For security reasons of course...").queue();
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I don't have the permission to do that.").queue();
            return;
        }
        if (!event.getMember().canInteract(event.getMessage().getMentionedMembers().get(0))) {
            event.getChannel().sendMessage("You cant manage roles for this member").queue();
            event.getMessage().delete().queue();
            return;
        }
        if (!event.getMember().canInteract(event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", "")))) {
            event.getChannel().sendMessage("You cant manage roles higher or equal to yourself").queue();
            event.getMessage().delete().queue();
            return;
        }
        if (args.length < 3) {
            event.getChannel().sendMessage("Command usage Incorrect! Correct usage: " + USAGE).queue();
        } else if (args.length > 3) {
            event.getChannel().sendMessage("Command usage Incorrect! Correct usage: " + USAGE).queue();
        } else if (args.length == 3) {
            try {
                event.getGuild().getController().addRolesToMember(event.getMessage().getMentionedMembers().get(0), event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", ""))).queue();
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " :white_check_mark: Roles assigned successfully.").queue();
            } catch (HierarchyException e) {
                event.getChannel().sendMessage("Sorry i can't set roles higher than my own.").queue();
                event.getMessage().delete().queue();
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
}
