package com.fjd.app.controller;

import com.fjd.app.service.OrderService;
import com.fjd.spring.annotation.FAutowired;
import com.fjd.spring.annotation.FController;
import com.fjd.spring.annotation.FRequestMapping;
import com.fjd.spring.annotation.FRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@FController
@FRequestMapping("/Orders")
public class OrderController {
    @FAutowired("orderservice")
    private OrderService orderService;

    @FRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @FRequestParam("name") String name, @FRequestParam("age") String age) {
        try {
            PrintWriter writer = response.getWriter();
            String result = orderService.query(name, age);
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
