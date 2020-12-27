package com.rong.miaosha.redis;


public class MiaoshaUserKey extends BasicPrefix{

    public static final int TOKEN_EXPIRE = 3600*24 * 2;
    public MiaoshaUserKey(String prefix){
        super(prefix);
    }
    public MiaoshaUserKey(int expireSecond, String prefix){
        super(expireSecond, prefix);
    }
    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
