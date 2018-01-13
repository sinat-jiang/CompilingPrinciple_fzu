package com.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件操作类
 * 
 * @author hasee
 */
public class FileOperationTools {

//	public static void main(String[] args) throws IOException {
//		String read = readFromFile("./input.txt");
//		System.out.println(read);
//	}

	/**
	 * 读文件操作-按字符读入
	 * @param filepath : 文件路径
	 * @throws IOException
	 */
	public static String readFromFile(String filepath) throws IOException {
		StringBuffer buffer = new StringBuffer();
		FileReader fileReader = null;
		try {
			File file = new File(filepath);
			// 如果文件不存在，创建一个新的空文件
			if (!file.exists()) {
				file.createNewFile();
			}
			fileReader = new FileReader(file);
			// 每次读入1024字节
			char[] buf = new char[1024];
			int charread = 0;
			while ((charread = fileReader.read(buf)) != -1) {
				String str = new String(buf);
				buffer.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 读写完文件执行关闭操作，释放资源
			try {
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

	/**
	 * 写入操作
	 * @param str : 本次的终结符或非终结符
	 * @param type ： 所属类型
	 */
	public static void writeToFile(String str, String type) {
		String pathname = "./output.txt";
		File file = null;
		FileWriter fileWriter = null;
		try {
			file = new File(pathname);
			// 若不存在，直接新创建一个文件
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("文件不存在，将新创建一个");
			}
			fileWriter = new FileWriter(file.getName(), true);
			String message = "<" + str + "," + type + ">";
			fileWriter.write(message + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 调用完毕，释放连接
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println("*");
	}

}
