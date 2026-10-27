package com.fzfnotfound.group.system.utility;

import java.util.Random;

public class RandomManage {




    public static String randomNumber(int length){
        String str="";
        for(int i=0;i<length;i++){
            str+= Integer.toString((new Random()).nextInt(10));
        }
        return str;
    }
}
