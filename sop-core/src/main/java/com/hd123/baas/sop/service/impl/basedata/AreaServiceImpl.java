package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.area.Area;
import com.hd123.baas.sop.service.api.basedata.area.AreaFilter;
import com.hd123.baas.sop.service.api.basedata.area.AreaService;
import com.hd123.baas.sop.service.api.basedata.area.convert.RsAreaToArea;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.area.RsArea;
import com.hd123.baas.sop.remote.rsmas.area.RsAreaFilter;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.mpa.api.common.JSONUtil;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 **/
@Service
@Slf4j
public class AreaServiceImpl extends BaseServiceImpl implements AreaService {

  @Autowired
  private RsMasClient rsMasClient;

  public static final RsAreaToArea RS_TO = new RsAreaToArea();

  @Override
  public QueryResult<Area> query(String tenant, AreaFilter filter) throws BaasException {
    List<Area> areas = new ArrayList<>();
    areas = JsonUtil.jsonToList("[{\"tenant\":\"mkhtest\",\n" +
      "        \"uuid\":\"-\",\n" +
      "        \"id\":\"-\",\n" +
      "        \"code\":\"-\",\n" +
      "        \"name\":\"默认\",\n" +
      "    },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DB001\",\n" +
      "          \"id\":\"DB001\",\n" +
      "          \"code\":\"DB001\",\n" +
      "          \"name\":\"东部一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DB002\",\n" +
      "          \"id\":\"DB002\",\n" +
      "          \"code\":\"DB002\",\n" +
      "          \"name\":\"东部二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DB003\",\n" +
      "          \"id\":\"DB003\",\n" +
      "          \"code\":\"DB003\",\n" +
      "          \"name\":\"东部三区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DB004\",\n" +
      "          \"id\":\"DB004\",\n" +
      "          \"code\":\"DB004\",\n" +
      "          \"name\":\"东部四区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DB005\",\n" +
      "          \"id\":\"DB005\",\n" +
      "          \"code\":\"DB005\",\n" +
      "          \"name\":\"东部五区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DN001\",\n" +
      "          \"id\":\"DN001\",\n" +
      "          \"code\":\"DN001\",\n" +
      "          \"name\":\"杭州东南一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DN002\",\n" +
      "          \"id\":\"DN002\",\n" +
      "          \"code\":\"DN002\",\n" +
      "          \"name\":\"杭州东南二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DN004\",\n" +
      "          \"id\":\"DN004\",\n" +
      "          \"code\":\"DN004\",\n" +
      "          \"name\":\"杭州东南四区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"DN005\",\n" +
      "          \"id\":\"DN005\",\n" +
      "          \"code\":\"DN005\",\n" +
      "          \"name\":\"杭州东南五区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"JM001\",\n" +
      "          \"id\":\"JM001\",\n" +
      "          \"code\":\"JM001\",\n" +
      "          \"name\":\"浙江加盟店\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"SX001\",\n" +
      "          \"id\":\"SX001\",\n" +
      "          \"code\":\"SX001\",\n" +
      "          \"name\":\"绍兴一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"SX002\",\n" +
      "          \"id\":\"SX002\",\n" +
      "          \"code\":\"SX002\",\n" +
      "          \"name\":\"绍兴二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"XB001\",\n" +
      "          \"id\":\"XB001\",\n" +
      "          \"code\":\"XB001\",\n" +
      "          \"name\":\"西部一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"XB002\",\n" +
      "          \"id\":\"XB002\",\n" +
      "          \"code\":\"XB002\",\n" +
      "          \"name\":\"西部二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"XB003\",\n" +
      "          \"id\":\"XB003\",\n" +
      "          \"code\":\"XB003\",\n" +
      "          \"name\":\"西部三区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"XB004\",\n" +
      "          \"id\":\"XB004\",\n" +
      "          \"code\":\"XB004\",\n" +
      "          \"name\":\"西部四区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"XB005\",\n" +
      "          \"id\":\"XB005\",\n" +
      "          \"code\":\"XB005\",\n" +
      "          \"name\":\"西部五区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"ZB001\",\n" +
      "          \"id\":\"ZB001\",\n" +
      "          \"code\":\"ZB001\",\n" +
      "          \"name\":\"中部一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"ZB002\",\n" +
      "          \"id\":\"ZB002\",\n" +
      "          \"code\":\"ZB002\",\n" +
      "          \"name\":\"中部二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"ZB003\",\n" +
      "          \"id\":\"ZB003\",\n" +
      "          \"code\":\"ZB003\",\n" +
      "          \"name\":\"中部三区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"JY001\",\n" +
      "          \"id\":\"JY001\",\n" +
      "          \"code\":\"JY001\",\n" +
      "          \"name\":\"教育园一区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"JY002\",\n" +
      "          \"id\":\"JY002\",\n" +
      "          \"code\":\"JY002\",\n" +
      "          \"name\":\"教育园二区\"\n" +
      ",\n" +
      "      },{\n" +
      "        \"tenant\":\"mkhtest\",\n" +
      "          \"uuid\":\"JY003\",\n" +
      "          \"id\":\"JY003\",\n" +
      "          \"code\":\"JY003\",\n" +
      "          \"name\":\"教育园三区\"\n" +
      "      }]", Area.class);

    QueryResult<Area> result = new QueryResult<Area>();
    result.setRecords(areas);
    result.setMore(false);
    result.setPage(0);
    result.setPageSize(0);
    return result;
  }

  @Override
  public QueryResult<Area> queryByMas(String tenant, AreaFilter filter) throws BaasException {
    List<Area> areas = new ArrayList<>();
    try {
      RsAreaFilter rsAreaFilter = new RsAreaFilter();
      if (filter == null) {
        filter = new AreaFilter();
      }
      BeanUtils.copyProperties(filter, rsAreaFilter);
      log.info("query area by mas, filter={}", JSONUtil.safeToJson(rsAreaFilter));
      RsMasResponse<List<RsArea>> rsMasResponse = rsMasClient.queryArea(tenant, rsAreaFilter);
      QueryResult<Area> result = new QueryResult<>();
      areas = rsMasResponse.getData().isEmpty() ? new ArrayList<>() : ConverterUtil.convert(rsMasResponse.getData(), RS_TO);
      log.info("area queryResult={}", JSONUtil.safeToJson(areas));
      result.setRecords(areas);
      result.setMore(false);
      result.setPage(0);
      result.setPageSize(0);
      return result;
    } catch (Exception e) {
      throw new BaasException("query Area By mas error", e.getMessage());
    }
  }

}
