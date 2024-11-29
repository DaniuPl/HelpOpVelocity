package pl.daniu.helpOpVelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import pl.daniu.helpOpVelocity.HelpOpVelocity;
import pl.daniu.helpOpVelocity.api.API;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : Daniu
 **/
public class HelpOPCommand implements SimpleCommand {


    private final ProxyServer server;

    public HelpOPCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(API.FixColor("&cKomenda nie dostępna z konsoli"));
            return;
        }

        Player player = (Player) source;
        UUID playerUUID = player.getUniqueId();


        if (args.length == 0) {
            String usageMessage = API.getConfigString("helpop.correct-usage");
            source.sendMessage(API.FixColor(usageMessage));
            return;
        }


        long cooldownTime = API.getConfigInt("helpop.cooldown") * 1000;

        long currentTime = System.currentTimeMillis();
        if (API.helpOpCooldownList.containsKey(playerUUID)) {
            long lastUsed = API.helpOpCooldownList.get(playerUUID);
            if ((currentTime - lastUsed) < cooldownTime) {
                long timeLeft = (cooldownTime - (currentTime - lastUsed)) / 1000;
                String cooldownMessage = API.getConfigString("helpop.cooldown-message");
                player.sendMessage(API.FixColor(cooldownMessage.replace("{X}", timeLeft + "")));
                return;
            }
        }

        API.helpOpCooldownList.put(playerUUID, currentTime);

        server.getScheduler().buildTask(HelpOpVelocity.getInstance(), () -> {
            API.helpOpCooldownList.remove(playerUUID);
        }).delay(cooldownTime, TimeUnit.MILLISECONDS).schedule();

        String message = String.join(" ", args);

        Optional<String> serverName = player.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName());


        String receiveFormat = API.getConfigString("helpop.receiver-format");

        Component helpopMessage = API.FixColor(receiveFormat
                .replace("{SERVER}", serverName.get())
                .replace("{PLAYER}", player.getUsername())
                .replace("{MESSAGE}", message));

        String hoverHelpOp = API.getConfigString("hover.message-gotoserver-reporter");
        helpopMessage =  helpopMessage.hoverEvent(HoverEvent.showText(API.FixColor(hoverHelpOp)));
        helpopMessage = helpopMessage.clickEvent(ClickEvent.runCommand("/server " + serverName.get()));


        Component finalHelpopMessage = helpopMessage;
        server.getAllPlayers().stream()
                .filter(admin -> admin.hasPermission("vinecore.helpop-receive"))
                .forEach(admin -> admin.sendMessage(finalHelpopMessage));

        String successMessage = API.getConfigString("helpop.success");
        player.sendMessage(API.FixColor(successMessage));
    }
}
