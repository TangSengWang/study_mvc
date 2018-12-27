package com.tangseng.Controller;



import com.tangseng.Annotation.Autowired;
import com.tangseng.Annotation.Controller;
import com.tangseng.Annotation.RequestMapping;
import com.tangseng.Annotation.RequestParam;
import com.tangseng.Service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TestController {

    @Autowired
    private TestService testService;


    @RequestMapping("text")
    public void Test(HttpServletRequest request, HttpServletResponse response,@RequestParam("param") String param){
        System.out.println(param);
        testService.run();
        try {
            response.getWriter().write("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
