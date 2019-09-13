package cn.pipilu.tensquare.manager.zuul.filter;

import cn.pipilu.plus.common.util.JwtUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class ManagerZuulFilter extends ZuulFilter{
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${no.token.validate.urls}")
    private String noTokenUrls;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.err.println("经过 ManagerZuulFilter....");
        //当前上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();
        if (StringUtils.isNotBlank(noTokenUrls)){
            String[] urls = noTokenUrls.split(",");
            for (String url : urls) {
                if (Objects.equals(url, requestURI)){
                    return null;
                }
            }
        }

        String header = request.getHeader("Authorization");
        if (StringUtils.isBlank(header)){
            errorResponse(requestContext,"权限不足");
            return null;
        }
        if (!header.startsWith("Bearer ")){
            errorResponse(requestContext,"权限不足");
            return null;
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.parseJWT(token);
            String roles = (String) claims.get("roles");
            if (Objects.equals("admin",roles)){
                requestContext.addZuulRequestHeader("Authorization",header);
                return null;
            }
        }catch (Exception e){
            errorResponse(requestContext,"登录过期，请重新登录");
            return null;
        }

        errorResponse(requestContext,"权限不足");
        return null;
    }

    private void errorResponse(RequestContext requestContext,String msg){

        requestContext.setResponseStatusCode(401);
        requestContext.setSendZuulResponse(false);//终止运行
        requestContext.setResponseBody(msg);
        requestContext.getResponse().setContentType("text/html;charset=utf-8");
        // json 格式
        //requestContext.getResponse().setContentType("application/json;charset=utf-8");

    }
}
