package com.leewan.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leewan.util.IOUtils;
import com.leewan.util.mybatis.StringDateTypeHandler;

/**
 * :服务初始化
 * @author JackyCjw
 *
 */
@Component
public class InitializationService implements InitializingBean {

	@Autowired
	DataSource source;
	
	@Autowired
	SqlSessionFactory sqlSessionFactory;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			this.initService();
		} catch (Exception e) {
		}
		sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(StringDateTypeHandler.class);
	}
	
	
	
	private void initService() throws DocumentException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("service-init-sql.xml");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);
		Element root = doc.getRootElement();
		List<Element> es = root.elements("table");
		es.forEach(new Consumer<Element>() {
			@Override
			public void accept(Element t) {
				Connection connection = null;
				String sql = null;
				try {
					Element tableBuilding = t.element("tableBuilding");
					sql = tableBuilding.getText();
					connection = InitializationService.this.source.getConnection();
					final Statement statement = connection.createStatement();
					statement.execute(sql);
					
					Element elementSQLS = t.element("sqls");
					if(elementSQLS == null) {
						return;
					}
					List<Element> sqls = elementSQLS.elements("sql");
					if(sqls != null) {
						sqls.forEach(new Consumer<Element>() {
							@Override
							public void accept(Element sqlElement) {
								String sql = sqlElement.getText();
								try {
									statement.execute(sql);
								} catch (SQLException e) {
									System.out.println(sql);
									e.printStackTrace();
								}
							}
						});
					}
					
				} catch (Exception e) {
					
				} finally {
					IOUtils.close(connection);
				}
			}
		});
	}

}
