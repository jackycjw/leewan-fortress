package com.leewan.pageContext.resource;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class StaticResource {
	
	protected long lastModifyTime;
	
	protected SoftReference<String> content;
	
	public abstract long getFileModifyTime();

	public abstract String getResourceName();
	
	
	public static StaticResource createResource(File file) {
		return StaticFileResource.createResource(file);
	}
	
	public static StaticResource createResource(JarEntry file, JarFile jarFile)  {
		return StaticJarResource.createResource(file, jarFile);
	}
	
	protected abstract String readContent();
	
	public String getContent() {
		String content = null;
		if(this.content == null) {
			//初始化
			content = readContent();
			this.content = new SoftReference<String>(content);
		} else {
			content = this.content.get();
			if(content == null) {
				//软连接已被移除
				content = readContent();
				this.content = new SoftReference<String>(content);
			} else {
				long lastModified = getFileModifyTime();
				if(lastModified != this.lastModifyTime) {
					//原始文件被改动过
					content = readContent();
					this.content = new SoftReference<String>(content);
				}
			}
		}
		return content;
	}
}
