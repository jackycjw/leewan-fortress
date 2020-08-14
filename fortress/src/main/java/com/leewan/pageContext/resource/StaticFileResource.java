package com.leewan.pageContext.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import com.leewan.pageContext.except.ContextResourceException;

public class StaticFileResource extends StaticResource {

	private File file;
	
	private String charset = "utf-8";
	
	private String resourceName;

	public static StaticFileResource createResource(File file) {
		StaticFileResource resource = new StaticFileResource();
		resource.file = file;
		resource.resourceName = resource.file.getName();
		return resource;
	}
	
	
	
	public String getResourceName() {
		return resourceName;
	}


	private StaticFileResource() {
	}
	
	public long getFileModifyTime() {
		return this.file.lastModified();
	}
	
	protected String readContent() {
		try {
			this.lastModifyTime = this.file.lastModified();
			this.resourceName = this.file.getName();
			InputStream in = new FileInputStream(file);
			byte[] bs = new byte[in.available()];
			in.read(bs);
			return new String(bs, this.charset);
		} catch (Exception e) {
			throw new ContextResourceException(e.getMessage(), e);
		}
	}
	
	
}
