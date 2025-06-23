package com.ssafy.util;

import java.math.BigInteger;

public class Hash {
    public String getHash(String target) {
        BigInteger hash = BigInteger.ZERO;
        BigInteger k = BigInteger.valueOf(1);
        
        for(int i = 0; i < target.length();i++) {
            hash = hash.add(BigInteger.valueOf(target.charAt(i)).multiply(k));
            k = k.multiply(BigInteger.valueOf(67));
        }
        // System.out.println(hash.toString(16));
        return hash.toString(16);
    }
}