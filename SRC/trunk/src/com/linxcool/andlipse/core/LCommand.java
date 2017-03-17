package com.linxcool.andlipse.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 命令辅助类
 * @author huchanghai
 */
public class LCommand {
	
	public static int getOptIndex(String[] args, String opt){
		for (int i = 0; i < args.length; i++) {
			if(args[i].equals(opt)){
				return i;
			}
		}
		return -1;
	}

	public static String getOptParameter(String[] args, String opt){
		int index = getOptIndex(args, opt);
		if(index == -1 || index + 1 >= args.length) {
			return null;
		}
		return args[index + 1];
	}

	/**
	 * 执行shell指令
	 * @param cmd 指令
	 */
	public static String exec(String cmd) throws Exception{

		Process process = Runtime.getRuntime().exec(cmd);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream())); 
		String line = null;
		StringBuilder builder = new StringBuilder();
		while ( (line=bufferedReader.readLine()) != null){
			builder.append(line);
		}
		process.waitFor(); 

		return builder.toString();
	}

}