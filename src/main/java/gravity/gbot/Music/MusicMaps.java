package gravity.gbot.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicMaps {
    public static List<String> musicCmds = new ArrayList<>();
    public void add() {
        // musicCmds.add("ALIAS|DESCRIPTION|USAGE");
        musicCmds.add("play|Plays the supplied url or does a youtube search and returns the output if no url is supplied.|play (url or song name)");
        musicCmds.add("seek|Seeks to the specified point in the track.|seek (time)");
        musicCmds.add("pause|Pauses the player.|pause");
        musicCmds.add("resume|Resumes the player.|resume");
        musicCmds.add("leave|Makes the bot leave the current voice channel.|leave");
        musicCmds.add("skip|Skips the currently playing song.|skip");
        musicCmds.add("stop|Stops the player and clears the queue.|stop");
        musicCmds.add("volume|Changes the player volume.|volume (volume 0 - 100)");
        musicCmds.add("restart|Restarts the currently playing track.|restart");
        musicCmds.add("reset|Resets the player.|reset");
        musicCmds.add("nowplaying|Displays the currently playing track.|nowplaying or np");
        musicCmds.add("queue|Displays the player queue.|queue or q");
        musicCmds.add("shuffle|Shuffles the player.|shuffle");
        musicCmds.add("loop|Loops the currently playing track.|loop");
    }

}
