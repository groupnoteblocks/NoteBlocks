package com.example.demo.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlApi {
    @GetMapping("")
    public String index(){
        return "index";
    }
    @GetMapping("/share")
    public String share(){
        return "share";
    }

}
