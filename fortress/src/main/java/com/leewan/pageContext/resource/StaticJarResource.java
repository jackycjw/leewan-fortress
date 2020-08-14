package com.leewan.pageContext.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.exception.ContextedException;

import com.leewan.pageContext.except.ContextResourceException;

public class StaticJarResource extends StaticResource {

	private JarEntry file;
	
	private JarFile jarFile;
	
	private long lastModifyTime;
	
	private SoftReference<String> content;
	
	private String charset = "utf-8";
	
	private String resourceName;

	public static StaticJarResource createResource(JarEntry file, JarFile jarFile) {
		StaticJarResource resource = new StaticJarResource();
		resource.file = file;
		resource.jarFile = jarFile;
		resource.resourceName = resource.file.getName();
		return resource;
	}
	
	
	
	public String getResourceName() {
		return resourceName;
	}


	private StaticJarResource() {
	}
	
	public long getFileModifyTime() {
		return this.file.getLastModifiedTime().toMillis();
	}
	
	
	protected String readContent() {
		try {
			this.lastModifyTime = this.file.getLastModifiedTime().toMillis();
			String name = this.file.getName();
			int index = name.lastIndexOf("/");
			this.resourceName = name.substring(index+1);
			InputStream in = this.jarFile.getInputStream(file);
			byte[] bs = new byte[in.available()];
			in.read(bs);
			return new String(bs, this.charset);
		} catch (Exception e) {
			throw new ContextResourceException(e.getMessage(), e);
		}
	}
	
	
}
