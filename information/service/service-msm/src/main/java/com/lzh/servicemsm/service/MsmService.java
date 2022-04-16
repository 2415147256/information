package com.lzh.servicemsm.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MsmService {

    boolean send(Map<String, Object> param, String phone);
}
