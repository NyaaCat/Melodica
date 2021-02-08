package cat.nyaa.melodica;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.PlayInfo;
import cat.nyaa.nyaacore.ILocalizer;
import cat.nyaa.nyaacore.cmdreceiver.Arguments;
import cat.nyaa.nyaacore.cmdreceiver.BadCommandException;
import cat.nyaa.nyaacore.cmdreceiver.CommandReceiver;
import cat.nyaa.nyaacore.cmdreceiver.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        Location playLocation = null;
        Entity playEntity = null;
        if (hasArgLocation(arguments)){
            playLocation = nextLocation(arguments);
        }else if (hasArgEntityUuid(arguments)){
            playEntity = arguments.nextEntity();
        }

        PlayInfo playInfo = resolvePlayInfo(sender, arguments);
        IMusicSheet sheet = SheetManager.getInstance().getSheet(sheetName);
        if (sheet == null){
            msg(sender, "error.sheet_not_exist", sheetName);
            return;
        }
        Player player = asPlayer(sender);
        if (playLocation != null){
            MelodicaPlugin.plugin.play(playInfo, playLocation);
        }else if (playEntity != null){
            MelodicaPlugin.plugin.play(playInfo, playEntity);
        }else{
            MelodicaPlugin.plugin.play(playInfo, player);
        }
    }

    private boolean hasArgEntityUuid(Arguments arguments) {
        try{
            Arguments parse = Arguments.parse(arguments.getRawArgs());
            int remains = arguments.remains();
            while (parse.remains() > arguments.remains()){
                parse.next();
            }
            Entity entity = parse.nextEntity();
            return entity != null;
        }catch (Exception e){
            return false;
        }
    }

    private Location nextLocation(Arguments arguments) {
        String world = arguments.nextString();
        double x = arguments.nextDouble();
        double y = arguments.nextDouble();
        double z = arguments.nextDouble();
        World world1 = Bukkit.getWorld(world);
        return new Location(world1, x, y, z);
    }

    private boolean hasArgLocation(Arguments arguments) {
        if (! (arguments.remains() >= 4)) {
            return false;
        }
        int remains = arguments.remains();
        String[] rawArgs = arguments.getRawArgs();
        int start = rawArgs.length - remains;
        String world = rawArgs[start];
        String xStr = rawArgs[start+1];
        String yStr = rawArgs[start+2];
        String zStr = rawArgs[start+3];
        try{
            double x = Double.parseDouble(xStr);
            double y = Double.parseDouble(yStr);
            double z = Double.parseDouble(zStr);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    @SubCommand(value = "reload", permission = "melodica")
    public void onReload(CommandSender sender, Arguments arguments){
        MelodicaPlugin.plugin.reload();
    }

    private PlayInfo resolvePlayInfo(CommandSender sender, Arguments arguments) {
        PlayInfo playInfo = new PlayInfo();
        while (arguments.top() != null) {
            try {
                String[] split = arguments.nextString().split(":", 2);
                String prop = split[0];
                String val = split[1];
                Field declaredField = PlayInfo.class.getDeclaredField(prop);
                declaredField.setAccessible(true);
                declaredField.set(playInfo, val);
            }catch (Exception e){
                throw new BadCommandException();
            }
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
