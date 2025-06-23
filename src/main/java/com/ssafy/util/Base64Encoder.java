package com.ssafy.util;

import java.util.*;
import java.io.*;

public class Base64Encoder {
	
	public String getBase64(String a) {
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < a.length();i++)
			ret.append(getBinaryCode((byte)a.charAt(i)));
		
		return getByteValue(ret);
	}

	private String getByteValue(StringBuilder binaryCode) {
		StringBuilder ret = new StringBuilder();

		 String base64Table = 
			        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
			        "abcdefghijklmnopqrstuvwxyz" +
			        "0123456789-_";
			
		
		while(binaryCode.length() % 6 != 0)
			binaryCode.append('0');
		
		int idx = binaryCode.length();	
		
		
		for(int i = idx -1; i>= 0;) {
			byte n =(byte)(1);
			byte val = 0;
			for(int j = 5; j >= 0; j--, i--) {
				if(i>= 0 && binaryCode.charAt(i) == '1')
					val+=n;
				n = (byte)(n<<1);
			}
			ret.append(base64Table.charAt(val));	
		}	
		ret.reverse();
		while(ret.length() % 4 != 0)
			ret.append('=');
			
		
		
		return ret.toString();
	}

	private String getBinaryCode(byte target) {
	
		byte n =(byte)(1<<7);
		
		
		StringBuilder sb = new StringBuilder();
		while(n!= (byte)0) {
			if(target %2 == 1) 
				sb.append('1');
			else
				sb.append('0');
			target /= 2;
			n /= 2;
		}
		
		return sb.reverse().toString();
	}

	
}
