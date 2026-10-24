package net.dingletherat.client;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;

public final class DevShutdownFix {
    private DevShutdownFix() {}

    public static void register() {
        if (!Platform.isDevelopmentEnvironment()) return;

        ClientLifecycleEvent.CLIENT_STOPPING.register(client -> {
            Thread renderThread = Thread.currentThread();
            Thread exitThread = new Thread(() -> {
                try {
                    renderThread.join();
                } catch (InterruptedException ignored) {
                    return;
                }
                System.exit(0);
            }, "MoneyTalks dev shutdown");
            exitThread.setDaemon(true);
            exitThread.start();
        });
    }
}
