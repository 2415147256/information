package com.hd123.baas.sop.service.impl.skutag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.skutag.Tag;
import com.hd123.baas.sop.service.api.skutag.TagService;
import com.hd123.baas.sop.service.dao.skutag.ShopTagDaoBof;
import com.hd123.baas.sop.service.dao.skutag.TagDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.skutag.TagDeleteEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.TagDeleteMsg;
import com.hd123.baas.sop.evcall.exector.skutag.TagSaveEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.TagSaveMsg;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {
  @Autowired
  private TagDaoBof tagDao;
  @Autowired
  private ShopTagDaoBof shopTagDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public Integer saveNew(String tenant, Tag tag) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(tag.getOrgId(), "orgId");
    Assert.notNull(tag.getName(), "name");
    List<Tag> tagDaoByName = tagDao.getByName(tenant, tag.getOrgId(), tag.getName());
    if (CollectionUtils.isNotEmpty(tagDaoByName)) {
      throw new BaasException("该标签已存在，请修改后重试");
    }
    tagDao.insert(tenant, tag);
    List<Tag> result = tagDao.getByName(tenant, tag.getOrgId(), tag.getName());
    Integer uuid = result.get(0).getUuid();
    sendTagSaveMsg(tenant, uuid);
    return uuid;
  }

  @Override
  @Tx
  public void saveModify(String tenant, Tag tag) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(tag.getUuid(), "uuid");
    Tag t = tagDao.get(tenant, tag.getUuid());
    if (t == null) {
      throw new BaasException("修改标签不存在");
    }
    List<Tag> tagDaoByNames = tagDao.getByName(tenant, t.getOrgId(), tag.getName());
    if (CollectionUtils.isNotEmpty(tagDaoByNames)) {
      boolean b = tagDaoByNames.stream().anyMatch(s -> s.getUuid() != tag.getUuid());
      if (b) {
        throw new BaasException("该标签已存在，请修改后重试");
      }
    }
    tagDao.update(tenant, tag);
    sendTagSaveMsg(tenant, tag.getUuid());
  }

  @Override
  @Tx
  public void batchSave(String tenant, List<Tag> tags) throws BaasException {
    Assert.notNull(tenant, "tenant");
    if (CollectionUtils.isEmpty(tags)) {
      log.info("请求的数据为空，忽略。");
      return;
    }
    // 查找已存在的
    List<String> sourceIds = tags.stream()
        .map(Tag::getSourceId)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.toList());
    List<Tag> hasList = tagDao.listBySourceIds(tenant, sourceIds);
    Map<String, Tag> hasMap = hasList.stream()
        .collect(Collectors.toMap(Tag::getSourceId, i -> i));
    List<Tag> batchUpdates = buildUpdates(tags, hasMap);
    List<Tag> batchInserts = buildInserts(tags, hasMap);
    if (CollectionUtils.isNotEmpty(batchInserts)) {
      log.info("待新增的标签，sourceIds={}", batchUpdates.stream().map(Tag::getSourceId).collect(Collectors.toList()));
      tagDao.batchInsert(tenant, batchInserts);
    }
    if (CollectionUtils.isNotEmpty(batchUpdates)) {
      log.info("待更新的标签，uuids={}", batchUpdates.stream().map(Tag::getUuid).collect(Collectors.toList()));
      tagDao.batchUpdate(tenant, batchUpdates);
    }

  }


  @Override
  @Tx
  public void delete(String tenant, Integer uuid) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Tag tag = tagDao.get(tenant, uuid);
    if (tag == null) {
      throw new BaasException("标签不存在");
    }
    boolean existTag = shopTagDao.isExistTag(tenant, tag.getOrgId(), uuid + "");
    if (existTag) {
      throw new BaasException("该标签已被使用");
    }
    tagDao.delete(tenant, uuid);
    sendTagDeleteMsg(tenant, uuid);
  }

  @Override
  public Tag get(String tenant, Integer uuid) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    return tagDao.get(tenant, uuid);
  }

  @Override
  public List<Tag> list(String tenant,String source, List<String> orgIds) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgIds, "orgId");
    return tagDao.list(tenant,source, orgIds);
  }

  @Override
  public List<Tag> list(String tenant) {
    Assert.notNull(tenant, "tenant");
    return tagDao.list(tenant);
  }

  @Override
  public List<Tag> listByUuids(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuids, "uuids");
    return tagDao.listByUuids(tenant, uuids);
  }

  private void sendTagSaveMsg(String tenant, Integer tagId) {
    TagSaveMsg msg = new TagSaveMsg();
    msg.setTenant(tenant);
    msg.setUuid(tagId);
    msg.setTraceId(UUID.randomUUID().toString());
    publisher.publishForNormal(TagSaveEvCallExecutor.TAG_SAVE_EXECUTOR_ID, msg);
  }

  private void sendTagDeleteMsg(String tenant, Integer tagId) {
    TagDeleteMsg msg = new TagDeleteMsg();
    msg.setTenant(tenant);
    msg.setUuid(tagId);
    msg.setTraceId(UUID.randomUUID().toString());
    publisher.publishForNormal(TagDeleteEvCallExecutor.TAG_DELETE_EXECUTOR_ID, msg);
  }

  private List<Tag> buildUpdates(List<Tag> tags, Map<String, Tag> hasMap) {
    List<Tag> list = new ArrayList<>();
    tags.forEach(i -> {
      if (StringUtils.isNotEmpty(i.getSourceId()) && hasMap.containsKey(i.getSourceId())) {
        Tag item = hasMap.get(i.getSourceId());
        // 不相等才更新
        if (!item.getName().equals(i.getName())) {
          item.setName(i.getName());
          list.add(item);
        }
      }
    });
    return list;
  }

  private List<Tag> buildInserts(List<Tag> tags, Map<String, Tag> hasMap) {
    List<Tag> list = new ArrayList<>();
    tags.forEach(i -> {
      if (StringUtils.isEmpty(i.getSourceId()) || !hasMap.containsKey(i.getSourceId())) {
        list.add(i);
      }
    });
    return list;
  }
}
