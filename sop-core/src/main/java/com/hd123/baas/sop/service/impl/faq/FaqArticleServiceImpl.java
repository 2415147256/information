package com.hd123.baas.sop.service.impl.faq;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.faq.FaqArticle;
import com.hd123.baas.sop.service.api.faq.FaqArticleRecord;
import com.hd123.baas.sop.service.api.faq.FaqArticleService;
import com.hd123.baas.sop.service.api.faq.FaqCategory;
import com.hd123.baas.sop.service.dao.faq.FaqArticleDaoBof;
import com.hd123.baas.sop.service.dao.faq.FaqArticleRecordDaoBof;
import com.hd123.baas.sop.service.dao.faq.FaqCategoryDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
@Service
public class FaqArticleServiceImpl implements FaqArticleService {
  @Autowired
  private FaqArticleDaoBof faqArticleDao;
  @Autowired
  private FaqCategoryDaoBof faqCategoryDao;
  @Autowired
  private FaqArticleRecordDaoBof faqArticleRecordDao;
  @Autowired
  private FaqBillNumberMgr faqBillNumberMgr;

  @Override
  @Tx
  public String saveNew(String tenant, FaqArticle faqArticle, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticle, "文章保存参数");
    Assert.notNull(faqArticle.getOwner());

    // 判断分类id是否存在
    FaqCategory faqCategory = faqCategoryDao.get(tenant, faqArticle.getOwner());
    if (Objects.isNull(faqCategory)) {
      throw new BaasException("分类不存在");
    }
    // 判断文章存在重复
    int count = faqArticleDao.countByArticleName(tenant, faqArticle.getArticleId(), faqArticle.getArticleName());
    // 文章标题不能重复
    if (count > 0) {
      // 文章标题重复
      throw new BaasException("文章存在重复");
    }
    faqArticle = buildAddFaqArticle(tenant, faqArticle, operateInfo);
    faqArticleDao.save(tenant, faqArticle);
    return faqArticle.getArticleId();
  }

  private FaqArticle buildAddFaqArticle(String tenant, FaqArticle faqArticle, OperateInfo operateInfo) {
    String uuid = UUID.randomUUID().toString();
    String result = faqBillNumberMgr.generateArticleFlowNo(tenant);
    // 保留五位流水 如果流水大于五位 向上累加
    String articleId;
    if (Integer.valueOf(result) < Integer.valueOf(99999)) {
      result = "00000" + result;
      articleId = result.substring(result.length() - 5);
    } else {
      articleId = result;
    }
    faqArticle.setTenant(tenant);
    faqArticle.setUuid(uuid);
    faqArticle.setArticleId(articleId);
    faqArticle.setDeleted(0);
    faqArticle.setCreateInfo(operateInfo);
    faqArticle.setLastModifyInfo(operateInfo);
    return faqArticle;
  }

  @Override
  @Tx
  public void delete(String tenant, String articleId) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "articleId");

    faqArticleDao.delete(tenant, articleId);
    // 删除文章点赞记录
    faqArticleRecordDao.deleteByOwner(tenant, articleId);
  }

  @Override
  @Tx
  public void saveModify(String tenant, FaqArticle faqArticle, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticle, "保存分类参数");
    Assert.notNull(faqArticle.getArticleId(), "articleId");
    // 判断分类id是否存在
    FaqCategory faqCategory = faqCategoryDao.get(tenant, faqArticle.getOwner());
    if (Objects.isNull(faqCategory)) {
      throw new BaasException("分类不存在");
    }
    // 判断文章存在重复
    int count = faqArticleDao.countByArticleName(tenant, faqArticle.getArticleId(), faqArticle.getArticleName());
    // 文章标题不能重复
    if (count > 0) {
      // 文章标题重复
      throw new BaasException("文章标题存在重复");
    }
    // 查询文章信息
    FaqArticle faqArticleTo = faqArticleDao.get(tenant, faqArticle.getArticleId());
    if (Objects.isNull(faqArticleTo)) {
      throw new BaasException("文章不存在，无法修改!");
    }
    faqArticleTo.setLastModifyInfo(operateInfo);
    faqArticleTo.setArticleName(faqArticle.getArticleName());
    faqArticleTo.setContent(faqArticle.getContent());
    faqArticleTo.setUrlJson(faqArticle.getUrlJson());
    faqArticleTo.setOwner(faqArticle.getOwner());
    faqArticleDao.modify(tenant, faqArticleTo);
  }

  private void judgeHelp(String tenant, String articleId, Integer isAgree, OperateInfo operateInfo)
      throws BaasException {
    /*
     * 判断状态:1赞同 0反对 1. 判断当前是否存在状态; 存在修改状态，不存在新增状态
     */
    FaqArticleRecord faqArticleRecord = faqArticleRecordDao.getByOwner(tenant, articleId,
        operateInfo.getOperator().getId());
    if (Objects.nonNull(faqArticleRecord)) {
      // 存在点赞数据 更新点赞状态
      faqArticleRecord.setIsHelp(isAgree);
      faqArticleRecordDao.modify(tenant, faqArticleRecord);
    } else {
      FaqArticleRecord buildRecord = buildAddRecord(tenant, articleId, isAgree, operateInfo);
      // 不存在点赞 保存点赞数据
      faqArticleRecordDao.save(tenant, buildRecord);
    }
  }

  @Override
  @Tx
  public void helpful(String tenant, String articleId, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "articleId");

    judgeHelp(tenant, articleId, 1, operateInfo);
  }

  @Override
  @Tx
  public void unhelpful(String tenant, String articleId, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "articleId");

    judgeHelp(tenant, articleId, 0, operateInfo);
  }

  @Override
  public QueryResult<FaqArticle> query(String tenant, QueryDefinition qd, OperateInfo operateInfo,
      String... fetchParts) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");

    QueryResult<FaqArticle> result = faqArticleDao.query(tenant, qd);
    // 判断是否存在文章
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      List<FaqArticle> faqArticles = result.getRecords();
      faqArticles.forEach(o -> {
        // 统计文章有无帮助数量,和同意状态
        String articleId = o.getArticleId();
        getArticleInfo(tenant, articleId, operateInfo, o);
      });
      // 分片查询结果
      fetchParts(tenant, faqArticles, fetchParts);
      result.setRecords(faqArticles);
    }
    return result;
  }

  private void fetchParts(String tenant, List<FaqArticle> faqArticles, String[] fetchParts) {
    List<String> owners = faqArticles.stream().map(FaqArticle::getOwner).collect(Collectors.toList());
    owners = owners.stream().distinct().collect(Collectors.toList());
    List<String> fetchPartList = Arrays.asList(fetchParts);

    if (fetchPartList.contains(FaqArticle.FETCH_CATEGORY)) {
      // 分类分片数据
      List<FaqCategory> categorys = faqCategoryDao.listById(tenant, owners);
      Map<String, List<FaqCategory>> categoryMap = categorys.stream()
          .collect(Collectors.groupingBy(FaqCategory::getCategoryId));
      faqArticles.forEach(o -> o.setCategory(categoryMap.get(o.getOwner()).get(0)));
    }
  }

  private FaqArticleRecord buildAddRecord(String tenant, String ownerId, Integer isAgree, OperateInfo operateInfo) {
    String recordId = UUID.randomUUID().toString();
    FaqArticleRecord record = new FaqArticleRecord();
    record.setTenant(tenant);
    record.setUuid(recordId);
    record.setOwner(ownerId);
    record.setIsHelp(isAgree);
    record.setDeleted(0);
    record.setOperator(operateInfo.getOperator().getId());
    return record;
  }

  @Override
  public void cancel(String tenant, String articleId, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "uuid");

    FaqArticleRecord faqArticleRecord = faqArticleRecordDao.getByOwner(tenant, articleId,
        operateInfo.getOperator().getId());
    if (Objects.isNull(faqArticleRecord)) {
      // 存在点赞数据
      throw new BaasException("不存在点赞数据,不可以取消点赞！");
    }
    // 删除点赞数据
    faqArticleRecordDao.delete(tenant, faqArticleRecord);
  }

  @Override
  public FaqArticle get(String tenant, String articleId, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "articleId");

    // 查询文章详情
    FaqArticle faqArticle = faqArticleDao.get(tenant, articleId);
    if (Objects.isNull(faqArticle)) {
      throw new BaasException("FAQ文章不存在!");
    }
    // 设置文章分类信息
    FaqCategory faqCategory = faqCategoryDao.get(tenant, faqArticle.getOwner());
    faqArticle.setCategory(faqCategory);
    getArticleInfo(tenant, articleId, operateInfo, faqArticle);
    return faqArticle;
  }

  private void getArticleInfo(String tenant, String articleId, OperateInfo operateInfo, FaqArticle faqArticle) {
    FaqArticleRecord faqArticleRecord = faqArticleRecordDao.getByOwner(tenant, articleId,
        operateInfo.getOperator().getId());
    // 设置文章状态
    if (!Objects.isNull(faqArticleRecord)) {
      faqArticle.setIsAgree(1 == faqArticleRecord.getIsHelp());
    }
    // 查询有帮助数量和无帮助数量
    int helpNum = faqArticleRecordDao.countByOwnerAndHelp(tenant, articleId, 1);
    int notHelpNum = faqArticleRecordDao.countByOwnerAndHelp(tenant, articleId, 0);
    faqArticle.setHelpNum(helpNum);
    faqArticle.setNoHelpNum(notHelpNum);
  }

}
