package com.rong.miaosha.redis;

public interface KeyPrefix{
    public int getExpireSeconds();
    public String getPrefix();
}
