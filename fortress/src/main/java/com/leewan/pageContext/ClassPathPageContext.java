package com.leewan.pageContext;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;

import com.leewan.pageContext.except.ContextException;
import com.leewan.pageContext.resource.StaticResource;

public class ClassPathPageContext implements PageContext {

	private String rootPath;
	
	private Map<String, StaticResource> resource = new ConcurrentHashMap<String, StaticResource>();

	public ClassPathPageContext(String rootPath) {
		this.rootPath = rootPath;
		this.loadResource();
	}
	
	
	private boolean isStaticResource(File file) {
		String name = file.getName();
		if(name.endsWith(ContextConstant.HTML) || name.endsWith(ContextConstant.JS)
				|| name.endsWith(ContextConstant.CSS)) {
			return true;
		}
		return false;
	}
	
	private String getJSResource(String name) {
		String jsName = name + ContextConstant.JS;
		StaticResource jsRs = this.resource.get(jsName);
		if(null == jsRs) {
			return "";
		}
		StringBuffer rs = new StringBuffer();
		rs.append("<script type=\"text/javascript\">");
		rs.append(SystemConstant.CRLF);
		rs.append(jsRs.getContent());
		rs.append(SystemConstant.CRLF);
		rs.append("</script>");
		return rs.toString();
	}
	
	
	private String getCSSResource(String name) {
		String cssName = name + ContextConstant.CSS;
		StaticResource cssRs = this.resource.get(cssName);
		if(null == cssRs) {
			return "";
		}
		StringBuffer rs = new StringBuffer();
		rs.append("<style>");
		rs.append(SystemConstant.CRLF);
		rs.append(cssRs.getContent());
		rs.append(SystemConstant.CRLF);
		rs.append("</style>");
		return rs.toString();
	}
	
	//--对外开放
	
	//获取组件内容
	public String getContent(String name) {
		StringBuffer rs = new StringBuffer();
		String htmlName = name + ContextConstant.HTML;
		if(!this.resource.containsKey(htmlName)) {
			this.loadResource();
		}
		StaticResource htmlRs = this.resource.get(htmlName);
		if(htmlRs == null) {
			throw new ContextException("resource: "+htmlName + " not found");
		}
		rs.append(htmlRs.getContent());
		
		String jsRs = this.getJSResource(name);
		rs.append(jsRs);
		
		String cssRs = this.getCSSResource(name);
		rs.append(cssRs);
		return rs.toString();
	}

	private void loadResource() {
		try {
			ClassLoader cassLoader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> urls = cassLoader.getResources(rootPath);
			
			while(urls.hasMoreElements()) {
				URL url = urls.nextElement();
				findStaticResource(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String FILE_PROTOCOL = "file";
	
	public static void main(String[] args) {
		String filePath = "file:/springcloud/oauth-server-1.0.0.jar";
		if(filePath.startsWith(FILE_PROTOCOL)){
			filePath = filePath.substring(FILE_PROTOCOL.length() + 1);
		}
		System.out.println(filePath);
		
	}
	private void findStaticResource(URL url) throws IOException {
		
		
		String path = url.getPath();
		int index = path.indexOf("!");
		String filePath = path.substring(0, index);
		String jarPath = path.substring(index+2);
		jarPath = jarPath.replaceAll("!", "");
		
		System.out.println("filePath:" + filePath);
		if(filePath.startsWith(FILE_PROTOCOL)){
			filePath = filePath.substring(FILE_PROTOCOL.length() + 1);
		}
		JarFile file = new JarFile(filePath);
		
		System.out.println(jarPath);
		
		Enumeration<JarEntry> entries = file.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			if(isStaticResource(entry.getName()) && entry.getName().startsWith(jarPath)) {
				StaticResource rs = StaticResource.createResource(entry, file);
				this.resource.put(rs.getResourceName(), rs);
			}
			
		}
		
	}
	
	private static boolean isStaticResource(String name) {
		if(name.endsWith(ContextConstant.HTML) || name.endsWith(ContextConstant.JS)
				|| name.endsWith(ContextConstant.CSS)) {
			return true;
		}
		return false;
	}
	
}
