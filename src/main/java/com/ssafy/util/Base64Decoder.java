package com.ssafy.util;

public class Base64Decoder {
    public String decode(String target) {
        StringBuilder binaryCode = new StringBuilder();

        // Base64 테이블 정의
        String base64Table = 
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789-_";

        // '=' 제거
        target = target.replaceAll("=", "");

        // 각 base64 문자 → 6비트 이진 문자열
        for (int i = 0; i < target.length(); i++) {
            int index = base64Table.indexOf(target.charAt(i));
            binaryCode.append(getBinaryCode((byte) index));
        }

        // 8비트씩 끊어서 문자 복원
        StringBuilder result = new StringBuilder();
        for (int i = 0; i + 8 <= binaryCode.length(); i += 8) {
            String byteStr = binaryCode.substring(i, i + 8);
            int val = Integer.parseInt(byteStr, 2);
            result.append((char) val);
        }

        return result.toString();
    }

    private String getBinaryCode(byte target) {
        byte n = (byte) (1 << 5);
        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            if (target % 2 == 1)
                sb.append('1');
            else
                sb.append('0');
            target /= 2;
            n /= 2;
        }
        return sb.reverse().toString();
    }
}