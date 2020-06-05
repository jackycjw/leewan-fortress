package com.leewan.aop;

import java.lang.annotation.*;

/**
 * :权限拦截，
 * @author JackyCjw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OperateAuth {

	String[] value() default Authority.QUERY;
	
	/**
	 * :逻辑符号 
	 * @return
	 * 
	 */
	LogicalSymbol logic() default LogicalSymbol.OR;
}
