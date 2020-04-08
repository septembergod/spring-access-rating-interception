package org.zspace.common.security.rating.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class HttpResponseUtil {
    public static final  void writeToResponse(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(obj));
        out.flush();
        out.close();
    }

    public static final Object frequentlyResponse(){
        Map<String, Object> resBody = Maps.newHashMap();
        resBody.put("code", 1000);
        resBody.put("msg", "访问太频繁");
        return resBody;
    }
    
}
