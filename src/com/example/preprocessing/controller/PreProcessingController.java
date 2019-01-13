package com.example.preprocessing.controller;

import java.util.ArrayList;

public class PreProcessingController {
	
	public static ArrayList<String> normalizedStringArray(String preString, int maxLength) {
		ArrayList<String> preStringArray = new ArrayList<String>();
		String currentString;
		for(int j =0; j<maxLength-1; j++) {
			if(j>preString.split(" ").length-1) {
				preStringArray.add("x");
			}else {
				if(!preString.split(" ")[j].isEmpty()&&preString.split(" ")[j]!=null) {
					currentString = preString.split(" ")[j].replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "").replace(",", "");
					preStringArray.add(currentString);
					
				}else{
					preStringArray.add("x");
				}
			}
		}
		return preStringArray;
	}
}
