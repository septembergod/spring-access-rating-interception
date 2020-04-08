package org.zspace.common.security.rating.web.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SpringRatingFilter extends GenericFilterBean {

    private static final ThreadLocal<RequestResponseBean> reqResBody = new InheritableThreadLocal<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        reqResBody.set(new RequestResponseBean(req, res));
        chain.doFilter(req, res);
    }

    public static final RequestResponseBean get(){
        return reqResBody.get();
    }
}