package com.example.demo.Controller;

import com.example.demo.Remote;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class HelloController {
    @ResponseBody
    //通过返回ModelAndView方式实现
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }
    @RequestMapping("/add")
    public ModelAndView add(Integer a,Integer b ){
        System.out.println("后端开始计算数据 a--->"+a+", b--->"+b);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sum");
        modelAndView.addObject("result",a+b);
        return modelAndView;
    }
    @RequestMapping("/demo")
    public ModelAndView submit() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submit");
        return modelAndView;
    }
    @RequestMapping("/return")
    public ModelAndView service(String cmd) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        List<String> remoteString = Remote.connect(cmd);
        modelAndView.setViewName("command");
        modelAndView.addObject("results", remoteString);
        System.out.println(remoteString);
        return modelAndView;
    }
}
