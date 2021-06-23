package org.foxesworld.launchserver.command.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.profiles.ClientProfileBuilder;
import org.foxesworld.launcher.profiles.optional.OptionalFile;
import org.foxesworld.launcher.profiles.optional.OptionalTrigger;
import org.foxesworld.launcher.profiles.optional.actions.*;
import org.foxesworld.launcher.profiles.optional.triggers.OSTrigger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;
import org.foxesworld.utils.helper.IOHelper;
import org.foxesworld.utils.helper.JVMHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SaveProfilesCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public SaveProfilesCommand(LaunchServer server) {
        super(server);
    }

    public static ClientProfile makeProfile(ClientProfile.Version version, String title, MakeProfileOption... options) {
        ClientProfileBuilder builder = new ClientProfileBuilder();
        builder.setVersion(version.name);
        builder.setDir(title);
        builder.setAssetDir("asset" + version.name);
        builder.setAssetIndex(version.name);
        builder.setInfo("Информация о сервере");
        builder.setTitle(title);
        builder.setUuid(UUID.randomUUID());
        builder.setMainClass(getMainClassByVersion(version, options));
        builder.setServers(List.of(new ClientProfile.ServerProfile(title, "localhost", 25535)));
        // ------------
        builder.setUpdateVerify(List.of("libraries", "natives", "minecraft.jar", "forge.jar", "liteloader.jar", "mods"));
        builder.setClassPath(List.of("libraries", "minecraft.jar", "forge.jar", "liteloader.jar"));
        builder.setUpdate(List.of("servers.dat"));
        List<String> jvmArgs = new ArrayList<>(4);
        Set<OptionalFile> optionals = new HashSet<>();
        jvmArgs.add("-XX:+DisableAttachMechanism");
        // Official Mojang launcher java arguments
        jvmArgs.add("-XX:+UseG1GC");
        jvmArgs.add("-XX:+UnlockExperimentalVMOptions");
        jvmArgs.add("-XX:G1NewSizePercent=20");
        jvmArgs.add("-XX:MaxGCPauseMillis=50");
        jvmArgs.add("-XX:G1HeapRegionSize=32M");
        // -----------
        if (version.compareTo(ClientProfile.Version.MC1122) > 0) {
            jvmArgs.add("-Djava.library.path=natives");
            if (optionContains(options, MakeProfileOption.FORGE)) {
                builder.setClassLoaderConfig(ClientProfile.ClassLoaderConfig.AGENT);
            }
            OptionalFile optionalMacOs = new OptionalFile();
            optionalMacOs.name = "MacOSArgs";
            optionalMacOs.visible = false;
            optionalMacOs.actions = new ArrayList<>(1);
            optionalMacOs.actions.add(new OptionalActionJvmArgs(List.of("-XstartOnFirstThread")));
            optionalMacOs.triggersList = List.of(new OSTrigger(JVMHelper.OS.MACOSX));
            optionals.add(optionalMacOs);
        }
        if (optionContains(options, MakeProfileOption.LWJGLMAC)) {
            OptionalFile optionalMac = new OptionalFile();
            optionalMac.name = "MacLwjgl";
            optionalMac.visible = false;
            optionalMac.actions.add(new OptionalActionFile(Map.of(
                    "libraries/libraries/org/lwjgl/lwjgl/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-glfw/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-openal/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-stb/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-tinyfd/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-opengl/3.2.1", "",
                    "libraries/libraries/org/lwjgl/lwjgl-jemalloc/3.2.1", ""
            )));
            optionalMac.triggersList = List.of(new OSTrigger(JVMHelper.OS.MACOSX));
            optionals.add(optionalMac);
            OptionalFile optionalOther = new OptionalFile();
            optionalOther.name = "NonMacLwjgl";
            optionalOther.visible = false;
            optionalOther.actions.add(new OptionalActionFile(Map.of(
                    "libraries/libraries/org/lwjgl/lwjgl/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-glfw/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-openal/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-stb/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-tinyfd/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-opengl/3.2.2", "",
                    "libraries/libraries/org/lwjgl/lwjgl-jemalloc/3.2.2", ""
            )));
            OSTrigger nonMacTrigger = new OSTrigger(JVMHelper.OS.MACOSX);
            nonMacTrigger.inverted = true;
            optionalOther.triggersList = List.of(nonMacTrigger);
            optionals.add(optionalOther);
        }
        if (version.compareTo(ClientProfile.Version.MC117) >= 0) {
            builder.setMinJavaVersion(16);
            builder.setRecommendJavaVersion(16);
        }
        jvmArgs.add("-Dfml.ignorePatchDiscrepancies=true");
        jvmArgs.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
        builder.setJvmArgs(jvmArgs);
        builder.setUpdateOptional(optionals);
        List<String> clientArgs = new ArrayList<>();
        if (optionContains(options, MakeProfileOption.LAUNCHWRAPPER)) {
            if (optionContains(options, MakeProfileOption.LITELOADER)) {
                clientArgs.add("--tweakClass");
                clientArgs.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
            }
            if (optionContains(options, MakeProfileOption.FORGE)) {
                clientArgs.add("--tweakClass");
                if (version.compareTo(ClientProfile.Version.MC1710) > 0) {
                    clientArgs.add("net.minecraftforge.fml.common.launcher.FMLTweaker");
                } else {
                    clientArgs.add("cpw.mods.fml.common.launcher.FMLTweaker");
                }
                if (version.compareTo(ClientProfile.Version.MC1122) <= 0) {
                    builder.setMinJavaVersion(8);
                    builder.setRecommendJavaVersion(8);
                    builder.setMaxJavaVersion(8);
                }
            }
        }
        builder.setClientArgs(clientArgs);

        return builder.createClientProfile();
    }

    private static boolean optionContains(MakeProfileOption[] options, MakeProfileOption option) {
        return Arrays.stream(options).anyMatch(e -> e == option);
    }

    public static String getMainClassByVersion(@SuppressWarnings("unused") ClientProfile.Version version, MakeProfileOption... options) {
        if (optionContains(options, MakeProfileOption.LAUNCHWRAPPER)) {
            return "net.minecraft.launchwrapper.Launch";
        }
        return "net.minecraft.client.main.Main";
    }

    public static MakeProfileOption[] getMakeProfileOptionsFromDir(Path dir, ClientProfile.Version version) {
        List<MakeProfileOption> options = new ArrayList<>(2);
        if (Files.exists(dir.resolve("forge.jar"))) {
            options.add(MakeProfileOption.FORGE);
        }
        if (Files.exists(dir.resolve("liteloader.jar"))) {
            options.add(MakeProfileOption.LITELOADER);
        }
        if (Files.exists(dir.resolve("libraries/libraries/org/lwjgl/lwjgl/3.2.2")) && Files.exists(dir.resolve("libraries/libraries/org/lwjgl/lwjgl/3.2.1"))) {
            options.add(MakeProfileOption.LWJGLMAC);
        }
        if (version.compareTo(ClientProfile.Version.MC1122) <= 0) {
            options.add(MakeProfileOption.LAUNCHWRAPPER);
        }
        return options.toArray(new MakeProfileOption[0]);
    }

    @SuppressWarnings("deprecation")
    public static void saveProfile(ClientProfile profile, Path path) throws IOException {
        if (profile.getUUID() == null) profile.setUUID(UUID.randomUUID());
        if (profile.getServers().size() == 0) {
            ClientProfile.ServerProfile serverProfile = new ClientProfile.ServerProfile();
            serverProfile.isDefault = true;
            serverProfile.name = profile.getTitle();
            serverProfile.serverAddress = profile.getServerAddress();
            serverProfile.serverPort = profile.getServerPort();
            profile.getServers().add(serverProfile);
        }
        for (OptionalFile file : profile.getOptional()) {
            if (file.list != null) {
                String[] list = file.list;
                file.list = null;
                if (file.actions == null) file.actions = new ArrayList<>(2);
                OptionalAction action;
                switch (file.type) {
                    case FILE:
                        OptionalActionFile result = new OptionalActionFile(new HashMap<>());
                        for (String s : list) result.files.put(s, "");
                        action = result;
                        break;
                    case CLASSPATH:
                        action = new OptionalActionClassPath(list);
                        break;
                    case JVMARGS:
                        action = new OptionalActionJvmArgs(Arrays.asList(list));
                        break;
                    case CLIENTARGS:
                        action = new OptionalActionClientArgs(Arrays.asList(list));
                        break;
                    default:
                        continue;
                }
                file.actions.add(action);
            }
            if (file.triggers != null) {
                file.triggersList = new ArrayList<>(file.triggers.length);
                for (OptionalTrigger trigger : file.triggers) {
                    org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger newTrigger = trigger.toTrigger();
                    if (newTrigger != null) file.triggersList.add(newTrigger);
                }
                file.triggers = null;
            }
        }
        try (Writer w = IOHelper.newWriter(path)) {
            Launcher.gsonManager.configGson.toJson(profile, w);
        }
    }

    @Override
    public String getArgsDescription() {
        return "[profile names...]";
    }

    @Override
    public String getUsageDescription() {
        return "load and save profile";
    }

    @Override
    public void invoke(String... args) throws Exception {
        verifyArgs(args, 1);
        if (args.length > 0) {
            for (String profileName : args) {
                Path profilePath = server.profilesDir.resolve(profileName.concat(".json"));
                if (!Files.exists(profilePath)) {
                    logger.error("Profile {} not found", profilePath.toString());
                    return;
                }
                ClientProfile profile;
                try (Reader reader = IOHelper.newReader(profilePath)) {
                    profile = Launcher.gsonManager.configGson.fromJson(reader, ClientProfile.class);
                }
                saveProfile(profile, profilePath);
                logger.info("Profile {} save successful", profilePath.toString());
            }
            server.syncProfiles();
        }
    }

    public enum MakeProfileOption {
        LAUNCHWRAPPER, VANILLA, FORGE, FABRIC, LITELOADER, LWJGLMAC
    }
}
