package com.leewan.pageContext.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StaticJarResource extends StaticResource {

	private JarEntry file;
	
	private JarFile jarFile;
	
	private long lastModifyTime;
	
	private String content;
	
	private String charset = "utf-8";
	
	private String resourceName;

	public static StaticJarResource createResource(JarEntry file, JarFile jarFile) {
		StaticJarResource resource = new StaticJarResource();
		resource.file = file;
		resource.jarFile = jarFile;
		resource.loadResource();
		return resource;
	}
	
	
	
	public String getResourceName() {
		return resourceName;
	}


	private StaticJarResource() {
	}
	
	private void loadResource() {
		try {
			this.lastModifyTime = this.file.getLastModifiedTime().toMillis();
			String name = this.file.getName();
			int index = name.lastIndexOf("/");
			this.resourceName = name.substring(index+1);
			InputStream in = this.jarFile.getInputStream(file);
			byte[] bs = new byte[in.available()];
			in.read(bs);
			this.content = new String(bs, this.charset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getContent() {
		long lastModified = this.file.getLastModifiedTime().toMillis();
		if(lastModified != this.lastModifyTime) {
			this.loadResource();
		}
		return content;
	}
	
}
