package com.hd123.baas.sop.service.impl.option;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.option.Option;
import com.hd123.baas.sop.service.api.option.OptionService;
import com.hd123.baas.sop.service.api.option.OptionType;
import com.hd123.baas.sop.service.dao.option.OptionDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OptionServiceImpl  implements OptionService {
  @Autowired
  private OptionDao dao;

  @Override
  public List<Option> list(String tenant, OptionType type, List<String> keys) {

    Assert.hasText(tenant);
    Assert.notEmpty(keys);

    return dao.list(tenant, type.name(), keys);
  }

  @Override
  public QueryResult<Option> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant);
    Assert.notNull(qd);

    return dao.query(tenant, qd);
  }

  @Override
  public void batchSaveNew(String tenant, List<Option> options, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notEmpty(options);
    Assert.notNull(operateInfo);

    options.forEach(i->{
      i.setTenant(tenant);
      buildForSaveNew(i, operateInfo);
    });

    dao.batchSaveNew(options);

  }

  @Override
  public void batchSaveModify(String tenant, List<Option> options, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notEmpty(options);
    Assert.notNull(operateInfo);

    dao.batchUpdate(tenant, options, operateInfo);
  }

  @Override
  @Tx
  public void save(String tenant, List<Option> options, OperateInfo operateInfo) {
    Assert.hasText(tenant);
    Assert.notEmpty(options);
    Assert.notNull(operateInfo);

    Map<String, List<Option>> typeMap = options.stream().collect(Collectors.groupingBy(i->i.getType().name()));
    List<Option> inserts = new ArrayList<>();
    List<Option> updates = new ArrayList<>();
    for (String optionType : typeMap.keySet()) {
      List<Option> optionsList = typeMap.get(optionType);
      List<Option> records = dao.list(tenant, optionType, optionsList.stream().map(Option::getOpKey).collect(Collectors.toList()));
      Map<String, Option> recordMap = records.stream().collect(Collectors.toMap(Option::getOpKey, i -> i, (e1, e2) -> e1));
      for (Option option : optionsList) {
        if (recordMap.containsKey(option.getOpKey())) {
          // 修改
          Option record = recordMap.get(option.getOpKey());
          record.setOpValue(option.getOpValue());
          buildForUpdate(record, operateInfo);
          updates.add(record);
          continue;
        }

        // 新增
        option.setTenant(tenant);
        buildForSaveNew(option, operateInfo);
        inserts.add(option);
      }
    }

    if (CollectionUtils.isNotEmpty(inserts)) {
      dao.batchSaveNew(inserts);
    }

    if (CollectionUtils.isNotEmpty(updates)) {
      dao.batchUpdate(updates);
    }



  }

  private void buildForUpdate(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(entity.getVersion() + 1);
    entity.setLastModifyInfo(operateInfo);
  }

  private void buildForSaveNew(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(0);
    entity.setCreateInfo(operateInfo);
    entity.setLastModifyInfo(operateInfo);
  }
}
