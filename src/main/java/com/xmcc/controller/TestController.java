package com.xmcc.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

   /* Logger logger = LoggerFactory.getLogger(TestController.class);*/
   /* Logger logger = LoggerFactory.getLogger(TestController.class);*/
    @GetMapping("/hello")
    public String test(){
        log.info("log -> {}","hello logback info slf4j");
        return "hello";
    }
}
