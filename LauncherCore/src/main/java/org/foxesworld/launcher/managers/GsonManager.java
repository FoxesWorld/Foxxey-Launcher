package org.foxesworld.launcher.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.foxesworld.launcher.hasher.HashedEntry;
import org.foxesworld.launcher.hasher.HashedEntryAdapter;
import org.foxesworld.utils.helper.CommonHelper;

public class GsonManager {
    public GsonBuilder gsonBuilder;
    public Gson gson;
    public GsonBuilder configGsonBuilder;
    public Gson configGson;

    public void initGson() {
        gsonBuilder = CommonHelper.newBuilder();
        configGsonBuilder = CommonHelper.newBuilder();
        configGsonBuilder.setPrettyPrinting();
        registerAdapters(gsonBuilder);
        registerAdapters(configGsonBuilder);
        preConfigGson(configGsonBuilder);
        preGson(gsonBuilder);
        gson = gsonBuilder.create();
        configGson = configGsonBuilder.create();
    }

    public void registerAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(HashedEntry.class, new HashedEntryAdapter());
    }

    public void preConfigGson(GsonBuilder gsonBuilder) {
        //skip
    }

    public void preGson(GsonBuilder gsonBuilder) {
        //skip
    }
}
