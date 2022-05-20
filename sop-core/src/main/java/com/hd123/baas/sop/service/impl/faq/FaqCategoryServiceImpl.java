package com.hd123.baas.sop.service.impl.faq;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.faq.FaqArticle;
import com.hd123.baas.sop.service.api.faq.FaqCategory;
import com.hd123.baas.sop.service.api.faq.FaqCategoryService;
import com.hd123.baas.sop.service.dao.faq.FaqArticleDaoBof;
import com.hd123.baas.sop.service.dao.faq.FaqArticleRecordDaoBof;
import com.hd123.baas.sop.service.dao.faq.FaqCategoryDaoBof;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
@Service
public class FaqCategoryServiceImpl implements FaqCategoryService {

  @Autowired
  private FaqCategoryDaoBof faqCategoryDao;
  @Autowired
  private FaqArticleDaoBof faqArticleDao;
  @Autowired
  private FaqArticleRecordDaoBof faqArticleRecordDao;
  @Autowired
  private FaqBillNumberMgr faqBillNumberMgr;

  @Override
  @Tx
  public String saveNew(String tenant, FaqCategory faqCategory) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqCategory, "分类保存参数");

    // 保证分类名称不能重复
    List<FaqCategory> list = faqCategoryDao.listByName(tenant, faqCategory.getCategoryName());
    if (!list.isEmpty()) {
      // 存在重复分类名称
      throw new BaasException("分类名称存在重复");
    }
    String uuid = UUID.randomUUID().toString();
    FaqCategory category = buildCategory(tenant, uuid, faqCategory);
    faqCategoryDao.save(tenant, category);
    return category.getCategoryId();
  }

  @Override
  @Tx
  public void delete(String tenant, String categoryId) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(categoryId, "categoryId");

    // 获取分类信息
    FaqCategory faqCategory = faqCategoryDao.get(tenant, categoryId);
    if (Objects.isNull(faqCategory)) {
      throw new BaasException("不存在分类信息");
    }
    faqCategory.setDeleted(1);
    faqCategory.setTenant(tenant);
    faqCategoryDao.delete(tenant, faqCategory);
    // 查询文章点赞记录
    QueryDefinition articleQd = new QueryDefinition();
    articleQd.addByField(FaqArticle.Queries.OWNER, Cop.EQUALS, categoryId);
    QueryResult<FaqArticle> articleResult = faqArticleDao.query(tenant, articleQd);
    if (!articleResult.getRecords().isEmpty()) {
      // 删除点赞记录
      List<String> owners = articleResult.getRecords()
          .stream()
          .map(FaqArticle::getArticleId)
          .collect(Collectors.toList());
      faqArticleRecordDao.deleteByOwners(tenant, owners);
    }
    // 批量删除文章
    faqArticleDao.deleteByOwner(tenant, faqCategory.getCategoryId());
  }

  @Override
  @Tx
  public void saveModify(String tenant, FaqCategory category) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(category, "保存分类参数");
    Assert.notNull(category.getCategoryId(), "categoryId");

    FaqCategory faqCategory = faqCategoryDao.get(tenant, category.getCategoryId());
    faqCategory.setCategoryName(category.getCategoryName());
    // 保证分类名称不能重复
    List<FaqCategory> list = faqCategoryDao.listByName(tenant, category.getCategoryName());
    if (!list.isEmpty()) {
      // 过滤掉自己的分类的uuid
      list = list.stream()
          .filter(o -> !o.getCategoryId().equals(category.getCategoryId()))
          .collect(Collectors.toList());
      if (list.size() > 0) {
        // 存在重复分类名称
        throw new BaasException("分类名称存在重复");
      }
    }
    faqCategoryDao.modify(tenant, faqCategory);
  }

  @Override
  public QueryResult<FaqCategory> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    QueryResult<FaqCategory> queryResult = faqCategoryDao.query(tenant, qd);
    return queryResult;
  }

  private FaqCategory buildCategory(String tenant, String uuid, FaqCategory faqCategory) {
    String result = faqBillNumberMgr.generateCategoryFlowNo(tenant);
    // 保留四位 大于9999 向上继续累加
    String categoryId;
    if (Integer.valueOf(result) < Integer.valueOf(9999)) {
      result = "0000" + result;
      categoryId = result.substring(result.length() - 4);
    } else {
      categoryId = result;
    }
    faqCategory.setUuid(uuid);
    faqCategory.setTenant(tenant);
    faqCategory.setCategoryId(categoryId);
    return faqCategory;
  }
}
