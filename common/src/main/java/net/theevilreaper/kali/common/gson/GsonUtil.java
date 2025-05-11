package net.theevilreaper.kali.common.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.theevilreaper.aves.file.gson.PositionGsonAdapter;

public final class GsonUtil {

    public static final Gson DEFAULT_GSON;

    static {
        PositionGsonAdapter posAdapter = new PositionGsonAdapter();
        DEFAULT_GSON = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(Vec.class, posAdapter)
                .registerTypeAdapter(Pos.class, posAdapter)
                .create();
    }

    private GsonUtil() {

    }
}
