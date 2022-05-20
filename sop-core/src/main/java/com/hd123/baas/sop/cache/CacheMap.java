package com.hd123.baas.sop.cache;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: maodapeng
 * @Date: 2020/12/5 11:53
 */
@Slf4j
@Component
public class CacheMap {
  private static final String CACHE_NAME = "SOP_CACHE";
  @Autowired
  private CacheManager cacheManager;
  /**
   * @desction: 使用google guava缓存处理
   */
  /**
   * @desction: 获取缓存
   */
  public  Object get(String key) {
    Cache cache =  cacheManager.getCache(CACHE_NAME);
    Cache.ValueWrapper valueWrapper = cache.get(key);
    if (valueWrapper == null){
      return null;
    }
    return valueWrapper.get();
  }

  /**
   * @desction: 放入缓存
   */
  public void put(String key, Object value) {
    if (StringUtils.isNotEmpty(key) && value != null) {
      Cache cache = cacheManager.getCache(CACHE_NAME);
      cache.put(key,value);
    }
  }

  /**
   * @desction: 移除缓存
   */
  public  void remove(String key) {
    if (StringUtils.isNotEmpty(key)) {
      Cache cache =  cacheManager.getCache(CACHE_NAME);
      cache.evict(key);
    }
  }

  /**
   * @desction: 批量删除缓存
   */
  public void remove(List<String> keys) {
    if (keys != null && keys.size() > 0) {
      Cache cache = cacheManager.getCache(CACHE_NAME);
      for (String key : keys) {
        cache.evict(key);
      }
    }
  }
}