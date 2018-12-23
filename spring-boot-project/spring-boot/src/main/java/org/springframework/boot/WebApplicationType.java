/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot;

import org.springframework.util.ClassUtils;

/**
 * web应用类型
 * An enumeration of possible types of web application.
 *
 * @author Andy Wilkinson
 * @author Brian Clozel
 * @since 2.0.0
 */
public enum WebApplicationType {

	/**
	 * 既不是一个web应用也不会启动一个内置web服务器。
	 * The application should not run as a web application and should not start an
	 * embedded web server.
	 */
	NONE,

	/**
	 * 作为一个基于servlet的web应用，并启动一个内置的servlet web服务器。
	 * The application should run as a servlet-based web application and should start an
	 * embedded servlet web server.
	 */
	SERVLET,

	/**
	 * 作为一个反应式web应用，并启动一个内置的反应式web服务器。
	 * The application should run as a reactive web application and should start an
	 * embedded reactive web server.
	 */
	REACTIVE;

	private static final String[] SERVLET_INDICATOR_CLASSES = { "javax.servlet.Servlet", "org.springframework.web.context.ConfigurableWebApplicationContext" };

	private static final String WEBMVC_INDICATOR_CLASS = "org.springframework.web.servlet.DispatcherServlet";

	private static final String WEBFLUX_INDICATOR_CLASS = "org.springframework.web.reactive.DispatcherHandler";

	private static final String JERSEY_INDICATOR_CLASS = "org.glassfish.jersey.servlet.ServletContainer";

	private static final String SERVLET_APPLICATION_CONTEXT_CLASS = "org.springframework.web.context.WebApplicationContext";

	private static final String REACTIVE_APPLICATION_CONTEXT_CLASS = "org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext";

	/**
	 * 从classpath推断应用类型
	 * @return 应用类型
	 */
	static WebApplicationType deduceFromClasspath() {
		// 如果仅有webflux，则为反应式web应用类型
		if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null)
				&& !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)
				&& !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
			return WebApplicationType.REACTIVE;
		}
		// 如果servlet相关类都能找到则为servlet web应用类型，否则为普通应用类型
		for (String className : SERVLET_INDICATOR_CLASSES) {
			if (!ClassUtils.isPresent(className, null)) {
				return WebApplicationType.NONE;
			}
		}
		return WebApplicationType.SERVLET;
	}

	static WebApplicationType deduceFromApplicationContext(Class<?> applicationContextClass) {
		if (isAssignable(SERVLET_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
			return WebApplicationType.SERVLET;
		}
		if (isAssignable(REACTIVE_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
			return WebApplicationType.REACTIVE;
		}
		return WebApplicationType.NONE;
	}

	private static boolean isAssignable(String target, Class<?> type) {
		try {
			return ClassUtils.resolveClassName(target, null).isAssignableFrom(type);
		}
		catch (Throwable ex) {
			return false;
		}
	}

}
