package com.tangseng.Controller;



import com.tangseng.Annotation.Autowired;
import com.tangseng.Annotation.Controller;
import com.tangseng.Annotation.RequestMapping;
import com.tangseng.Annotation.RequestParam;
import com.tangseng.HttpServlet.MyRequest;
import com.tangseng.HttpServlet.MyResponse;
import com.tangseng.Service.TestService;

import java.io.IOException;


@Controller
public class TestController {

    @Autowired
    private TestService testService;


    @RequestMapping("text")
    public void Test(MyRequest request, MyResponse response, @RequestParam("param") String param){
        System.out.println(param);
        testService.run();
        try {
            response.write("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
