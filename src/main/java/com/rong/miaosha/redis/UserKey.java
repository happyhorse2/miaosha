package com.rong.miaosha.redis;

public class UserKey extends BasicPrefix{

    private UserKey(String prefix) {
        super(prefix);
    }
    private UserKey( int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static UserKey getById = new UserKey(30, "id");
    public static UserKey getByName = new UserKey(30, "name");

}
