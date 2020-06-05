package com.leewan.pageContext.resource;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class StaticResource {

	public abstract String getContent();
	
	public abstract String getResourceName();
	
	
	public static StaticResource createResource(File file) {
		return StaticFileResource.createResource(file);
	}
	
	public static StaticResource createResource(JarEntry file, JarFile jarFile)  {
		return StaticJarResource.createResource(file, jarFile);
	}
}
