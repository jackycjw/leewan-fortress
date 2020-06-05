package com.leewan.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.alibaba.druid.pool.DruidDataSource;
import com.leewan.ds.InstableDataSource;
import com.leewan.filter.AuthFilter;
import com.leewan.filter.CharEncodeFilter;
import com.leewan.util.interceptor.PageInterceptor;

@Configuration
public class ContextConfig {

	@Bean
	@ConfigurationProperties(prefix="spring.druid")
	public DataSource druidDataSource() {
		return new InstableDataSource();
	}
	
	@Value("${uncheckURI}")
	private String uncheckURI;
	
	@Bean
	public FilterRegistrationBean charEncodeFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new CharEncodeFilter());
		bean.addUrlPatterns("/*");
		bean.setOrder(1);
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean authFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new AuthFilter(uncheckURI));
		bean.addUrlPatterns("/*");
		bean.setOrder(10);
		return bean;
	}
	
	
	@Bean
	public PageInterceptor getMybatisInterceptor() {
		return new PageInterceptor();
	}
	
	
	
	
	@Bean  
    public ServerEndpointExporter serverEndpointExporter() {  
        return new ServerEndpointExporter();  
    } 
}
