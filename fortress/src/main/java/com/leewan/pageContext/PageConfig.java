package com.leewan.pageContext;

import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
public class PageConfig {

	/**
	 * static resource path
	 */
	@Value("${page.rootPath:classpath:static}")
	private String rootPath;
	
	@Bean
	public PageContext pageContext() {
		if(rootPath.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
			String path = rootPath.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			if(ResourceUtils.isJarURL(url)) {
				return new ClassPathPageContext(path);
			}else {
				return new FilePageContext(rootPath);
			}
		}else {
			return new FilePageContext(rootPath);
		}
	}
}
