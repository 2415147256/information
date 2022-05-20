/* 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 *
 * 项目名：	com.hd123.baas.sop.biz.basedata.service.impl.basedata
 * 文件名：	BaseServiceImpl
 * 模块说明：
 * 修改历史：
 * 2020/11/11 - 老娜 - 创建。
 */

package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.configuration.filter.AccessKey;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.service.api.basedata.area.Area;
import com.hd123.baas.sop.service.api.basedata.area.AreaService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.utils.ApplicationContextUtils;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hd123.baas.sop.config.BaasDeptAssignConfig.ASSIGN_CONFIG_KEY;

/**
 * @author lina
 */
@Service
public class BaseServiceImpl {

  @Autowired
  private AreaService areaService;
  @Resource(name = AccessKey.BEAN_NAME)
  protected AccessKey accessKey;

  protected RsMasClient getClient() {
    return ApplicationContextUtils.getBean(RsMasClient.class);
  }

  protected BaasResponse covertBaasResponse(RsMasResponse rsMasResponse) {
    BaasResponse response = new BaasResponse();
    response.setMsg(rsMasResponse.getEchoMessage());
    response.setCode(rsMasResponse.getEchoCode() == 0 ? 2000 : rsMasResponse.getEchoCode());
    response.setSuccess(rsMasResponse.isSuccess());
    response.setData(rsMasResponse.getData());
    return response;
  }

  protected BaasResponse covertBaasResponse(RsMasPageResponse rsMasPageResponse) {
    BaasResponse response = new BaasResponse();
    response.setMsg(rsMasPageResponse.getEchoMessage());
    response.setCode(rsMasPageResponse.getEchoCode() == 0 ? 2000 : rsMasPageResponse.getEchoCode());
    response.setSuccess(rsMasPageResponse.isSuccess());
    response.setTotal(rsMasPageResponse.getTotal());
    response.setData(rsMasPageResponse.getData());
    return response;
  }

  private void getStoreAddress(RsStore source, Store target) {
    if (source.getAddress() != null) {
      StringBuffer address = new StringBuffer();
      if (!StringUtil.isNullOrBlank(source.getAddress().getProvinceName())) {
        address.append(source.getAddress().getProvinceName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getCityName())) {
        address.append(source.getAddress().getCityName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getDistrictName())) {
        address.append(source.getAddress().getDistrictName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getStreetName())) {
        address.append(source.getAddress().getStreetName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getDetailAddress())) {
        address.append(source.getAddress().getDetailAddress());
      }
      if (!StringUtil.isNullOrBlank(address.toString())) {
        target.setAddress(address.toString());
      }
      if(!StringUtil.isNullOrBlank(source.getAddress().getLongitude())){
        target.setLongitude(source.getAddress().getLongitude());
      }
      if(!StringUtil.isNullOrBlank(source.getAddress().getLatitude())){
        target.setLatitude(source.getAddress().getLatitude());
      }
    }
  }

  protected List<Store> convertStore(String tenant, List<RsStore> rsStores)
      throws BaasException {
    QueryResult<Area> areaQueryResult = areaService.queryByMas(tenant, null);
    Map<String, Area> areaMap = areaQueryResult.getRecords().stream().collect(
        Collectors.toMap(Area::getCode, Function.identity(), (existing, replacement) -> existing));

    List<Store> storeList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(rsStores)) {
      for (RsStore source : rsStores) {
        Store target = new Store();
        BeanUtils.copyProperties(source, target);
        if(source.getState()!=null){
          target.setState(source.getState().name());
        }
        if(source.getBusinessState()!=null){
          target.setBusinessState(source.getBusinessState().name());
        }
        UCN area = new UCN(null, source.getArea(), null);
        if (areaMap.containsKey(source.getArea())) {
          area.setUuid(areaMap.get(source.getArea()).getId());
          area.setName(areaMap.get(source.getArea()).getName());
        }
        target.setTelephone(source.getContact() == null ? "" : source.getContact().getTelephone());
        target.setContactMan(source.getContact() == null ? "" : source.getContact().getName());
        target.setArea(area);
        getStoreAddress(source, target);
        storeList.add(target);
      }
    }
    return storeList;
  }

  protected String buildOperator() {
    String operator = "sop-service";
    if (!StringUtil.isNullOrBlank(accessKey.getUserName())) {
      operator = accessKey.getUserName();
    }
    return operator;
  }

  protected String buildUserId() {
    String operator = "sop-userId";
    if (!StringUtil.isNullOrBlank(accessKey.getUserId())) {
      operator = accessKey.getUserId();
    }
    return operator;
  }

  protected String buildKey(String orgId, String scope) {
    return ASSIGN_CONFIG_KEY + "." + orgId + "." + scope;
  }


}
