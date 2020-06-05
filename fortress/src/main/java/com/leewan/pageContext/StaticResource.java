package com.leewan.pageContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class StaticResource {

	private File file;
	
	private long lastModifyTime;
	
	private String content;
	
	private String charset = "utf-8";
	
	private String resourceName;

	public static StaticResource createResource(File file) {
		StaticResource resource = new StaticResource();
		resource.file = file;
		resource.loadResource();
		return resource;
	}
	
	
	public String getResourceName() {
		return resourceName;
	}


	private StaticResource() {
	}
	
	private void loadResource() {
		try {
			this.lastModifyTime = this.file.lastModified();
			this.resourceName = this.file.getName();
			InputStream in = new FileInputStream(file);
			byte[] bs = new byte[in.available()];
			in.read(bs);
			this.content = new String(bs, this.charset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getContent() {
		long lastModified = this.file.lastModified();
		//閫氳繃淇敼鏃堕棿鍒ゆ柇鏂囦欢鏄惁闇�瑕侀噸鏂板姞杞�
		if(lastModified != this.lastModifyTime) {
			this.loadResource();
		}
		return content;
	}
	
}
