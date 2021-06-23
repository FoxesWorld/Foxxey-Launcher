package org.foxesworld.launchserver.auth.core;

public interface UserSession {
    String getID();

    User getUser();

    long getExpireIn();
}
