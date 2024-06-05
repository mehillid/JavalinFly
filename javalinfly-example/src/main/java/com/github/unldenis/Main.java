package com.github.unldenis;

import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.Get;
import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.Response;

@Controller(path = "/hello")
public class Main {

    @Get(roles = {"user", "admin"})
    public void main() {
        System.out.println("Hello world!");

    }


}