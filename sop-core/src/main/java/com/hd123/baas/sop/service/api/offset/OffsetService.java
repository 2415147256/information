package com.hd123.baas.sop.service.api.offset;

/**
 * @author zhengzewang on 2020/11/17.
 */
public interface OffsetService {

  /**
   * 
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @param spec
   *          tenant+type下的第三维度
   * @return Offset
   */
  Offset get(String tenant, OffsetType type, String spec);

  /**
   *
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @param spec
   *          tenant+type下的第三维度
   * @return Offset
   */
  Offset getWithLock(String tenant, OffsetType type, String spec);

  /**
   * spec = {@link Offset#DEF}
   * 
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @return 响应值
   */
  Offset get(String tenant, OffsetType type);

  /**
   * spec = {@link Offset#DEF}
   *
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @return 响应值
   */
  Offset getWithLock(String tenant, OffsetType type);

  /**
   * 
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @param spec
   *          tenant+type下的第三维度
   * @param seq
   *          序列
   */
  void save(String tenant, OffsetType type, String spec, Long seq);

  /**
   * spec = {@link Offset#DEF}
   *
   * @param tenant
   *          租户
   * @param type
   *          类型
   * @param seq
   *          序列
   */
  void save(String tenant, OffsetType type, Long seq);

}
