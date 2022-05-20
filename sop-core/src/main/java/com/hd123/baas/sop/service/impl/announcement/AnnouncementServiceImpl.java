package com.hd123.baas.sop.service.impl.announcement;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.service.api.announcement.Announcement;
import com.hd123.baas.sop.service.api.announcement.AnnouncementProgress;
import com.hd123.baas.sop.service.api.announcement.AnnouncementService;
import com.hd123.baas.sop.service.api.announcement.AnnouncementShop;
import com.hd123.baas.sop.service.api.announcement.AnnouncementState;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.dao.announcement.AnnouncementDaoBof;
import com.hd123.baas.sop.service.dao.announcement.AnnouncementShopDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.announcement.AnnouncementEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.announcement.AnnouncementMsg;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.BAppMessage;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

  @Autowired
  private AnnouncementDaoBof dao;
  @Autowired
  private AnnouncementShopDaoBof shopDao;
  @Autowired
  private StoreService storeService;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private MessageService messageService;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FmsClient fmsClient;

  @Override
  public QueryResult<Announcement> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return dao.query(tenant, qd);
  }

  @Override
  public Announcement get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  @Tx
  public String saveNew(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(announcement, "announcement");
    Assert.notNull(operateInfo, "operateInfo");
    announcement.setState(AnnouncementState.UNPUBLISHED);
    String uuid = dao.insert(tenant, announcement, operateInfo);
    List<AnnouncementShop> shops = announcement.getShops();
    if (CollectionUtils.isNotEmpty(shops)) {
      shopDao.batchInsert(tenant, uuid, shops);
    }

    return uuid;
  }

  @Override
  @Tx
  public void saveModify(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(announcement, "announcement");
    Assert.notNull(operateInfo, "operateInfo");
    Announcement ever = this.get(tenant, announcement.getUuid());
    if (ever == null) {
      throw new BaasException("公告不存在");
    }
    if (ever.getState() == AnnouncementState.PUBLISHED) {
      throw new BaasException("公告已发布");
    }
    shopDao.deleteByOwner(tenant, announcement.getUuid());
    List<AnnouncementShop> shops = announcement.getShops();
    if (CollectionUtils.isNotEmpty(shops)) {
      shopDao.batchInsert(tenant, announcement.getUuid(), shops);
    }
    announcement.setState(AnnouncementState.UNPUBLISHED);
    dao.update(tenant, announcement, operateInfo);
  }

  @Override
  @Tx
  public void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(operateInfo, "operateInfo");
    Announcement announcement = this.get(tenant, uuid);
    if (announcement == null) {
      throw new BaasException("公告不存在");
    }
    if (announcement.getState() == AnnouncementState.PUBLISHED) {
      log.info("公告 {} 已发布", uuid);
      return;
    }

    // 关联所有门店
    if (announcement.isAllShops()) {
      StoreFilter filter = new StoreFilter();
      filter.setOrgIdEq(announcement.getOrgId());
      QueryResult<Store> storeQueryResult = storeService.query(tenant, filter);
      List<AnnouncementShop> announcementShops = new ArrayList<>();
      for (Store store : storeQueryResult.getRecords()) {
        AnnouncementShop announcementShop = new AnnouncementShop();
        announcementShop.setTenant(tenant);
        announcementShop.setOwner(uuid);
        announcementShop.setShop(store.getId());
        announcementShop.setShopCode(store.getCode());
        announcementShop.setShopName(store.getName());
        announcementShops.add(announcementShop);
      }
      shopDao.deleteByOwner(tenant, uuid);
      shopDao.batchInsert(tenant, uuid, announcementShops);
    } else {
      if (shopDao.queryCount(tenant, uuid, new QueryDefinition()) <= 0) {
        throw new BaasException("至少关联一个门店");
      }
    }

    dao.changeState(tenant, uuid, AnnouncementState.PUBLISHED, operateInfo);

    AnnouncementMsg msg = new AnnouncementMsg();
    msg.setTenant(tenant);
    msg.setPk(uuid);
    msg.setOrgId(announcement.getOrgId());
    msg.setTraceId(MDC.get("trace_id"));
    log.info("发送公告发布事件");
    publisher.publishForNormal(AnnouncementEvCallExecutor.ANNOUNCEMENT_CREATE_EXECUTOR_ID, msg);
  }

  @Override
  @Tx
  public void batchPublish(String tenant, Collection<String> uuids, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    for (String uuid : uuids) {
      this.publish(tenant, uuid, operateInfo);
    }
  }

  @Override
  @Tx
  public void delete(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Announcement announcement = this.get(tenant, uuid);
    if (announcement == null) {
      return;
    }
    if (AnnouncementState.PUBLISHED.equals(announcement.getState())) {
      throw new BaasException("公告已发布");
    }
    dao.delete(tenant, uuid);
    shopDao.deleteByOwner(tenant, uuid);
  }

  @Override
  @Tx
  public void batchDelete(String tenant, Collection<String> uuids) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    List<Announcement> announcements = dao.list(tenant, uuids);
    Announcement notAllow = announcements.stream()
        .filter(a -> AnnouncementState.PUBLISHED.equals(a.getState()))
        .findFirst()
        .orElse(null);
    if (notAllow != null) {
      throw new BaasException("存在已发布的公告");
    }
    dao.batchDelete(tenant, uuids);
    shopDao.deleteByOwners(tenant, uuids);
  }

  @Override
  @Tx
  public void relateShops(String tenant, String uuid, List<String> shopIds) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    Assert.notNull(shopIds);
    if (CollectionUtils.isEmpty(shopIds)) {
      shopDao.deleteByOwner(tenant, uuid);
      return;
    }
    Announcement announcement = this.get(tenant, uuid);
    if (announcement == null) {
      throw new BaasException("公告不存在");
    }
    if (AnnouncementState.PUBLISHED.equals(announcement.getState())) {
      throw new BaasException("公告已发布");
    }

    StoreFilter filter = new StoreFilter();
    filter.setIdIn(shopIds);
    QueryResult<Store> storeResult = storeService.query(tenant, filter);
    Map<String, Store> storeMap = storeResult.getRecords().stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
    List<AnnouncementShop> announcementShops = new ArrayList<>();
    for (String shopId : shopIds) {
      AnnouncementShop announcementShop = new AnnouncementShop();
      announcementShop.setTenant(tenant);
      announcementShop.setOwner(uuid);
      Store store = storeMap.get(shopId);
      if (store == null) {
        throw new BaasException("门店<{0}>不存在", shopId);
      }
      announcementShop.setShop(shopId);
      announcementShop.setShopCode(store.getCode());
      announcementShop.setShopName(store.getName());
      announcementShops.add(announcementShop);
    }
    shopDao.deleteByOwner(tenant, uuid);
    shopDao.batchInsert(tenant, uuid, announcementShops);
  }

  @Override
  public List<AnnouncementShop> listShops(String tenant, String uuid) {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    return shopDao.list(tenant, uuid);
  }

  @Override
  public List<Announcement> listByIds(String tenant, List<String> uuids) {
    Assert.notNull(tenant);
    Assert.notEmpty(uuids, "uuids");
    return shopDao.listByIds(tenant, uuids);
  }

  @Override
  public void noticeRead(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    List<Message> result;
    MessageConfig config = baasConfigClient.getConfig(tenant, MessageConfig.class);
    if (MessageConfig.FMS.equals(config.getAppMessageVendor())) {
      QueryRequest request = new QueryRequest();
      ArrayList<FilterParam> filters = new ArrayList<>();

      FilterParam param = new FilterParam();
      param.setProperty("source:=");
      param.setValue(uuid);
      filters.add(param);

      FilterParam param2 = new FilterParam();
      param2.setProperty("read:=");
      param2.setValue(false);
      filters.add(param2);

      request.setFilters(filters);
      BaasResponse<List<BAppMessage>> query = fmsClient.query(tenant, request);
      if (query.isSuccess()) {
        result = ConverterUtil.convert(query.getData(), MessageConvertUtil.APP_MESSAGE_TO_MESSAGE);
      } else {
        throw new BaasException("请求fms异常；code：{}，msg：{}", query.getCode(), query.getMsg());
      }
    } else {
      QueryDefinition messageQd = new QueryDefinition();
      messageQd.addByField(Message.Queries.SOURCE, Cop.EQUALS, uuid);
      messageQd.addByField(Message.Queries.READ, Cop.EQUALS, false);
      result = messageService.query(tenant, messageQd).getRecords();
    }
    if (result.isEmpty()) {
      dao.changeProgress(tenant, uuid, AnnouncementProgress.FINISHED, operateInfo);
    } else {
      dao.changeProgress(tenant, uuid, AnnouncementProgress.PART_FINISHED, operateInfo);
    }
  }
}
