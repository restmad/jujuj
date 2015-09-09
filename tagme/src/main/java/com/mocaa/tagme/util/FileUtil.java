package com.mocaa.tagme.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mocaa.tagme.global.GlobalDefs;
import android.os.Environment;

public class FileUtil {
	
	private String SDCardPath = null;
	public String getPath() {
		return SDCardPath;
	}

	public FileUtil() {
		SDCardPath = Environment.getExternalStorageDirectory() + "/";
	}

	public File createSDFile(String absolute) throws IOException {
		File file = new File(absolute);
		file.createNewFile();
		return file;
	}

	public File createSDDir(String dirName) {
		File dir = new File(SDCardPath + dirName);
		dir.mkdir();
		return dir;
	}

	public boolean isFileExist(String fileName) {
		File file = new File(SDCardPath + fileName);
		return file.exists();
	}
	public static void checkRoot(){
		System.out.println(Environment.getExternalStorageState());
		File file = new File(GlobalDefs.FILE_PATH);
		System.out.println(file.getAbsolutePath());
		if(!file.exists() || file.isFile()){
			boolean b = file.mkdirs();
			System.out.println("make dir:"+b);
		}else{
			System.out.println("exist");
		}
	}

	public File writeToSDCard(String path, String fileName,
			InputStream inputStream) {
		File file = null;
		OutputStream output = null;
		try{
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];
			while((inputStream.read(buffer)) != -1){
				output.write(buffer);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
	
	private static void deleteBeforeRename(File file){
		File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
		file.renameTo(to);
		to.delete();
	}
	
	public static boolean deleteFile(File file) {
        if (file.exists()) {
                if (file.isFile()) {
                	deleteBeforeRename(file);
                } else if (file.isDirectory()) {
                        File files[] = file.listFiles();
                        if (files != null) {
                                for (int i = 0; i < files.length; i++) {
                                	deleteFile(files[i]);
                                }
                        }
                }
//            	deleteBeforeRename(file);
        }
        return true;
	}
	
}

