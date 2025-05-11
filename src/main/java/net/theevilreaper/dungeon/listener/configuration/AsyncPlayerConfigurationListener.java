package net.theevilreaper.dungeon.listener.configuration;

import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncPlayerConfigurationListener implements Consumer<AsyncPlayerConfigurationEvent> {

    private final Supplier<Instance> instanceContainerSupplier;

    public AsyncPlayerConfigurationListener(@NotNull Supplier<Instance> instanceContainerSupplier) {
        this.instanceContainerSupplier = instanceContainerSupplier;
    }

    @Override
    public void accept(@NotNull AsyncPlayerConfigurationEvent event) {
        event.setSpawningInstance(instanceContainerSupplier.get());
    }
}
