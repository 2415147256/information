package com.hd123.baas.sop.service.impl.price.shopprice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.position.SkuPositionService;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceGradeService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceGrade;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPriceGradeDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/16.
 */
@Service
public class ShopPriceGradeServiceImpl implements ShopPriceGradeService {

  @Autowired
  private ShopPriceGradeDaoBof gradeDao;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private SkuPositionService skuPositionService;
  @Autowired
  private PriceGradeService priceGradeService;

  @Override
  public QueryResult<ShopPriceGrade> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    QueryResult<ShopPriceGrade> result = gradeDao.query(tenant, qd);
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      String orgId = result.getRecords().get(0).getOrgId();
      Map<String, String> groupMap = skuGroupService.list(tenant, orgId)
          .stream()
          .collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), SkuGroup::getName));
      Map<String, String> positionMap = skuPositionService.list(tenant, orgId)
          .stream()
          .collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), SkuPosition::getName));
      Map<String, String> gradeMap = priceGradeService.list(tenant, orgId)
          .stream()
          .collect(Collectors.toMap(s -> String.valueOf(s.getUuid()), PriceGrade::getName));
      result.getRecords().forEach(s -> {
        s.setSkuGroupName(groupMap.get(s.getSkuGroup()));
        s.setSkuPositionName(positionMap.get(s.getSkuPosition()));
        s.setPriceGradeName(gradeMap.get(s.getPriceGrade()));
      });
    }
    return result;
  }

  @Override
  public ShopPriceGrade get(String tenant, String shop, String skuGroup, String skuPosition) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.hasText(skuGroup, "skuGroup");
    Assert.hasText(skuPosition, "skuPosition");
    return gradeDao.getByShopAndGroupAndPosition(tenant, shop, skuGroup, skuPosition);
  }

  @Override
  public List<ShopPriceGrade> listByShop(String tenant, String shop) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    return gradeDao.listByShop(tenant, shop);
  }

  @Override
  @Tx
  public void batchSave(String tenant, Collection<ShopPriceGrade> grades) throws BaasException {
    // TODO 可不可以在dao层写on duplicate key update?用rumba
    List<ShopPriceGrade> inserts = new ArrayList<>();
    List<ShopPriceGrade> updates = new ArrayList<>();
    for (ShopPriceGrade priceGrade : grades) {
      ShopPriceGrade ever = gradeDao.getByShopAndGroupAndPosition(tenant, priceGrade.getShop(),
          priceGrade.getSkuGroup(), priceGrade.getSkuPosition());
      if (ever != null) {
        ever.setPriceGrade(priceGrade.getPriceGrade());
        ever.setSourceCreateTime(priceGrade.getSourceCreateTime());
        ever.setSource(priceGrade.getSource());
        updates.add(ever);
      } else {
        inserts.add(priceGrade);
      }
    }
    gradeDao.batchInsert(tenant, inserts);
    gradeDao.batchUpdate(tenant, updates);
  }
}
