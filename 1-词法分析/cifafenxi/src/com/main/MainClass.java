package com.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.tools.Analysis;
import com.tools.FileOperationTools;

public class MainClass {
	
	/**
	 * 主函数
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileReader fileReader = null;
		try {
			File file = new File("./output.txt");
			// 如果文件不存在，创建一个新的空文件
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//读取文件路径
		String readfilepath = "./input.txt";
		//调用 readFromFile() 函数将文件读取成 String 类型返回
		String readStr = FileOperationTools.readFromFile(readfilepath);
		//直接将 string 输入analysis() 函数进行分析
		Analysis.analysis(readStr);
	}

}
