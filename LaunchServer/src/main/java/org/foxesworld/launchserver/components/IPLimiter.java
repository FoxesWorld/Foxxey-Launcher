package org.foxesworld.launchserver.components;

public abstract class IPLimiter extends AbstractLimiter<String> {
    @Override
    protected String getFromString(String str) {
        return str;
    }
}
