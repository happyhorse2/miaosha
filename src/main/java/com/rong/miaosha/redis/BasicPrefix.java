package com.rong.miaosha.redis;

public abstract class BasicPrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasicPrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BasicPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int getExpireSeconds() {
        return this.expireSeconds;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + this.prefix;
    }
}

