package com.itecheasy.ph3.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public class DomainFilter implements Filter {
	
	protected String newSiteHome;
	
	protected String relevantRedirectSiteName;
	
	private static final String[] DEFAULT_STATUS_ENABLED_ON_HOSTS = WebConfig.getInstance().get("not_filter_host").split(",");
	
	@Override
	public void destroy() {
		
	}
	
	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		String serverName = request.getServerName();
		String serverNameSwitch = serverName.split("\\.")[0];
		String contextPath = request.getContextPath();
		if (!contextPath.endsWith("/"))
			contextPath += "/";
		
		for (String string : DEFAULT_STATUS_ENABLED_ON_HOSTS) {
			if(StringUtils.equalsIgnoreCase(string, serverName)){
				chain.doFilter(request, response);
				return;
			}
		}
		
		if(!serverName.equals(newSiteHome)){
			
			String url = generateURL(request.getScheme(), newSiteHome, request.getServerPort(), request.getRequestURI()) ;
			String queryString = request.getQueryString();
			if (StringUtils.isNotEmpty(queryString)) {
				url  += queryString;
			}
//			System.out.println(url);
			response.setStatus(301);
			response.setHeader("Location", url);
			response.setHeader( "Connection", "close");
		}else{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		newSiteHome = config.getInitParameter("newSiteHome");
		relevantRedirectSiteName =config.getInitParameter("relevantRedirectSiteName");
	}
	
	/**
	 * generate("https","www.pandahallstock.com","9090","/ph3") --&gt; https://www.pandahallstock.com:9090/ph3 <br>
	 * generate("http","www.pandahallstock.com","80","/ph3") --&gt; http://www.pandahallstock.com/ph3 <br>
	 * 
	 * @param scheme
	 * @param serverName
	 * @param port
	 * @param url
	 * @return
	 */
	public static String generateURL(String scheme,String serverName,int port,String url){  
		StringBuilder sb = new StringBuilder(scheme).append("://").append(serverName);
		if(port == 80){
			sb.append(url);   
		}else{
			sb.append(":").append(port).append(url);
		}
		return sb.toString();
	}
}
