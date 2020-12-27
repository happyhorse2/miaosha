package com.rong.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static final String salt = "1a2b3c4d";
    public static String hashMd5(String string){
        return DigestUtils.md5Hex(string);
    }

    public static String formPassWordToDbPassWord(String formPassWord){
        String str = salt.charAt(0)+salt.charAt(2)+formPassWord+salt.charAt(5)+salt.charAt(4);
        return hashMd5(str);
    }

    public static String inputPassToFormPass(String inputPass){
        String str = salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return hashMd5(str);
    }

    public static String inputPassToDbPass(String pass){
        String str = inputPassToFormPass(pass);
        return formPassWordToDbPassWord(str);
    }

    public static void main(String[] args) {
        String pass = "Helloworld930925";
        System.out.println(inputPassToDbPass(pass));
    }


}
