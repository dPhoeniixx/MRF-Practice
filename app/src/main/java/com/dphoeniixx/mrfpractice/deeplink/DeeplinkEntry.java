package com.dphoeniixx.mrfpractice.deeplink;

public class DeeplinkEntry {
    private final String entryRegex;
    private final String handlerMethod;

    public DeeplinkEntry(String regex, String handlerMethod) {
        this.entryRegex = regex;
        this.handlerMethod = handlerMethod;
    }

    public String getRegex() {
        return entryRegex;
    }

    public String getHandlerMethod() {
        return handlerMethod;
    }
}
