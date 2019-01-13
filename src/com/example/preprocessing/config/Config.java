package com.example.preprocessing.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.example.preprocessing.utils.FileManager;

public class Config {
	public static HashMap<String, String> loadConfig(){
		HashMap<String, String> configDataMap = null;
		if(FileManager.makeDir("config/")){
			File file = new File("config/config.txt");
			configDataMap = new HashMap<String, String>();
			String str; 
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				while ((str = br.readLine()) != null) {
					if(!str.isEmpty())
					{
						if(!str.startsWith("#"))
						{
							configDataMap.put(str.split("=")[0], str.split("=")[1]);
						}
					}
				}
				System.out.println("load config success");
			}catch (IOException e) {
				System.out.println("fail to load config");
				e.printStackTrace();
			} 
		}
		return configDataMap;
	}
}
