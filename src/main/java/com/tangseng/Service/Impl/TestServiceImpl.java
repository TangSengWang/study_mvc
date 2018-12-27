package com.tangseng.Service.Impl;

import com.tangseng.Annotation.Service;
import com.tangseng.Service.TestService;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public void run() {
        System.out.println("serviceImpl");
    }
}
