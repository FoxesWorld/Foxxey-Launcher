package org.foxesworld.launchserver.manangers;

import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.NeedGarbageCollection;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.RequiredDAO;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.utils.HookSet;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SessionManager implements NeedGarbageCollection {

    private final LaunchServer server;
    public HookSet<Client> clientRestoreHook = new HookSet<>();

    public SessionManager(LaunchServer server) {
        this.server = server;
    }


    @SuppressWarnings("UnusedReturnValue")
    public boolean addClient(Client client) {
        if (client == null || client.session == null) return false;
        return server.config.sessions.writeSession(client.uuid, client.session, compressClient(client));
    }

    @SuppressWarnings("unused")
    public Stream<UUID> findSessionsByUUID(UUID uuid) {
        return server.config.sessions.getSessionsFromUserUUID(uuid);
    }

    @SuppressWarnings("unused")
    public boolean removeByUUID(UUID uuid) {
        return server.config.sessions.deleteSessionsByUserUUID(uuid);
    }

    @Deprecated
    public Set<UUID> getSavedUUIDs() {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        server.config.sessions.clear();
    }

    private byte[] compressClient(Client client) {
        return Launcher.gsonManager.gson.toJson(client).getBytes(StandardCharsets.UTF_8); //Compress using later
    }

    private Client decompressClient(byte[] client) {
        return Launcher.gsonManager.gson.fromJson(new String(client, StandardCharsets.UTF_8), Client.class); //Compress using later
    }

    @SuppressWarnings("deprecation")
    private Client restoreFromString(byte[] data) {
        Client result = decompressClient(data);
        result.updateAuth(server);
        if (result.auth != null && (result.username != null)) {
            if (result.auth.isUseCore()) {
                result.coreObject = result.auth.core.getUserByUUID(result.uuid);
            } else {
                if (result.auth.handler instanceof RequiredDAO || result.auth.provider instanceof RequiredDAO || result.auth.textureProvider instanceof RequiredDAO) {
                    result.daoObject = server.config.dao.userDAO.findByUsername(result.username);
                }
            }
        }
        if (result.refCount == null) result.refCount = new AtomicInteger(1);
        clientRestoreHook.hook(result);
        return result;
    }

    @Override
    public void garbageCollection() {
    }


    public Client getClient(UUID session) {
        if (session == null) return null;
        byte[] data = server.config.sessions.getSessionData(session);
        if (data == null) return null;
        return restoreFromString(data);
    }

    @SuppressWarnings("unused")
    public Client getOrNewClient(UUID session) {
        Client client = getClient(session);
        return client == null ? new Client(session) : client;
    }

    public boolean remove(UUID session) {
        return server.config.sessions.deleteSession(session);
    }

    @Deprecated
    public void removeClient(UUID session) {
        remove(session);
    }

    @Deprecated
    public void updateClient(@SuppressWarnings("unused") UUID session) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Set<Client> getSessions() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void loadSessions(@SuppressWarnings("unused") Set<Client> set) {
        throw new UnsupportedOperationException();
    }
}
