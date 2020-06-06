package cat.nyaa.melodica;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.PlayInfo;
import cat.nyaa.nyaacore.ILocalizer;
import cat.nyaa.nyaacore.cmdreceiver.Arguments;
import cat.nyaa.nyaacore.cmdreceiver.CommandReceiver;
import cat.nyaa.nyaacore.cmdreceiver.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MelodicaCommand extends CommandReceiver {
    /**
     * @param plugin for logging purpose only
     * @param _i18n
     */
    public MelodicaCommand(Plugin plugin, ILocalizer _i18n) {
        super(plugin, _i18n);
    }

    @Override
    public String getHelpPrefix() {
        return null;
    }

    @SubCommand(value = "play", permission = "melodica", tabCompleter = "sheetList")
    public void onPlay(CommandSender sender, Arguments arguments){
        String sheetName = arguments.nextString();
        Player player = asPlayer(sender);
        PlayInfo playInfo = resolvePlayInfo(sender, arguments);
        IMusicSheet sheet = SheetManager.getInstance().getSheet(sheetName);
        if (sheet == null){
            msg(sender, "error.sheet_not_exist", sheetName);
            return;
        }

        playInfo.setSheet(sheet);
        MelodicaPlugin.plugin.play(playInfo, player);
    }

    private PlayInfo resolvePlayInfo(CommandSender sender, Arguments arguments) {
        PlayInfo playInfo = new PlayInfo();
        if (arguments.top() != null) {
            arguments.nextString().split(":",2);
        }
        return playInfo;
    }

    public List<String> sheetList(CommandSender sender, Arguments arguments) {
        List<String> completeStr = new ArrayList<>();
        switch (arguments.remains()) {
            case 1:
                completeStr.addAll(SheetManager.getInstance().getSheets());
                break;
            case 2:
                completePlayInfo(sender, arguments);
                break;
        }
        return Utils.filtered(arguments, completeStr);
    }

    private void completePlayInfo(CommandSender sender, Arguments arguments) {

    }

    public List<String> sampleCompleter(CommandSender sender, Arguments arguments) {
        List<String> completeStr = new ArrayList<>();
        switch (arguments.remains()) {
            case 1:
                break;
        }
        return Utils.filtered(arguments, completeStr);
    }
}
