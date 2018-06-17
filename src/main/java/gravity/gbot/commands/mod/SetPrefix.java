package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.Database;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetPrefix implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }

        Database db = new Database(Config.dbConnection);
        db.init();
        if (event.getMessage().getContentRaw().replace(args[0] + " ", "").contains(" ")) {
            event.getChannel().sendMessage("Error guild prefix CANNOT contain a space!").queue();
        }
        db.executeUpdate("UPDATE `Config` SET `Prefix` = '" + args[1] + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() + ";");
        db.close();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Prefix Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "setPrefix (Prefix)";
    }

    @Override
    public String getDesc() {
        return "Changes the Bots Prefix";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setprefix"));
    }

    @Override
    public String getType() {
        return "admin";
    }
}
