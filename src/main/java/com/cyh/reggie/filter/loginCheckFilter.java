package com.cyh.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.cyh.reggie.common.BaseContext;
import com.cyh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录，防止未登录直接进后台
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class loginCheckFilter implements Filter {

    //路径匹配器·支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse respond = (HttpServletResponse) servletResponse;


        //拦截逻辑
        //1.获取本次请求的URi
        String requestURI = request.getRequestURI();

        log.info("拦截到请求{}",requestURI);

        String[] urls = new String[]{
                //不需要处理的请求
                "/employee/login",
                "/employee/logout",
                "/backend/**", //其中"/backend/index.html"，路径不匹配
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，直接放行
        if (check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request, respond);
            return;
        }

        //4-1.判断登陆状态，如果已经登陆，直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已经登陆，用户ID为{}",request.getSession().getAttribute("employee"));

            /*
            设置登录用户ID，方便MyMetaObjectHandler中通过ThreaLocal调用获取
             */
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, respond);
            return;
        }

        //4-2.判断移动端用户登陆状态，如果已经登陆，直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已经登陆，用户ID为{}",request.getSession().getAttribute("user"));

            /*
            设置登录用户ID，方便MyMetaObjectHandler中通过ThreaLocal调用获取
             */
            Long useId   = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(useId);

            filterChain.doFilter(request, respond);
            return;
        }


        log.info("用户未登录" );
        //5.如果未登录返回未登录结果，像前端写数据，在request.js中的53行接收到就会自动进行跳转，通过输出流的方式向客户端页面响应数据
        respond.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls       放行的数组
     * @param requestURI 请求的uri
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match)
                return true;
        }
        return false;
    }
}
