package com.fjd.app.service;

import com.fjd.spring.annotation.FService;

@FService("orderservice")
public class OrderService {
    public String query(String name, String age) {
        return "{ name: " + name + ", age: " + age + " }";
    }
}
