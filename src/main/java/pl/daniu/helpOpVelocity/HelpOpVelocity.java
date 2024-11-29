package pl.daniu.helpOpVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import pl.daniu.helpOpVelocity.commands.HelpOPCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(
        id = "helpopvelocity",
        name = "HelpOpVelocity",
        version = "1.0-SNAPSHOT",
        authors = {"DaniuPl"}
)
public class HelpOpVelocity {


    @Inject
    private final ProxyServer server;

    @Inject
    private Logger logger;

    public YamlDocument config;

    private static HelpOpVelocity Instance;

    public static HelpOpVelocity getInstance() {return Instance;}

    public HelpOpVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        Instance = this;

        this.server = server;
        this.logger = logger;

        try{
            config = YamlDocument.create(new File(dataDirectory.toFile(), "config.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                    UpdaterSettings.builder().setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
            );
        }catch (IOException e){
            logger.log(Level.INFO, "Error with creating config file. Disabling plugin");
            Optional<PluginContainer> container = server.getPluginManager().getPlugin("VineCoreVelocity");
            container.ifPresent(pluginContainer -> pluginContainer.getExecutorService().shutdown());
        }

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register("helpop", new HelpOPCommand(server));
    }
}
