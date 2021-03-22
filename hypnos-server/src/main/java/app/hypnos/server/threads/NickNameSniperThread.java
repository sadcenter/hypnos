package app.hypnos.server.threads;

import app.hypnos.server.Server;
import app.hypnos.server.data.Snipe;
import app.hypnos.server.data.User;
import app.hypnos.server.utils.SniperUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class NickNameSniperThread extends Thread {

    private final Server server;

    public NickNameSniperThread(Server server) {
        this.server = server;

        setDaemon(true);
        setName("sniper");
    }

    private final LoadingCache<Snipe, String> loadingCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @Nullable String load(@NotNull Snipe snipe) throws Exception {
                    return SniperUtil.getAuthToken(snipe.getAccount().getUserName(), snipe.getAccount().getPassword());
                }
            });

    @SneakyThrows
    @Override
    public void run() {
        for (User user : this.server.getUsers()) {
            for (Snipe snipe : user.getSnipes()) {
                if (snipe.getAccessTime() - TimeUnit.SECONDS.toMillis(1) <= System.currentTimeMillis()) {
                    String authToken = loadingCache.get(snipe);
                    for (int i = 0; i < 15; i++) {
                        SniperUtil.changeName(user, snipe, authToken);
                    }

                }
            }
        }

        Thread.sleep(250L);
        run();
    }
}
