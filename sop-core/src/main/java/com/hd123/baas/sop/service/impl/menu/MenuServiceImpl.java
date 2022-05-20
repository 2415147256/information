package com.hd123.baas.sop.service.impl.menu;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.MenuConfig;
import com.hd123.baas.sop.service.api.menu.Menu;
import com.hd123.baas.sop.service.api.menu.MenuService;
import com.hd123.baas.sop.service.api.menu.MenuType;
import com.hd123.baas.sop.service.dao.menu.MenuDaoBof;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

  @Autowired
  private MenuDaoBof menuDao;
  @Autowired
  private BaasConfigClient client;

  @Override
  @Tx
  public String saveNew(String tenant, Menu item) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(item, "item");
    Assert.notNull(item.getCode(), "item.code");

    Menu history = menuDao.getByCode(tenant, item.getCode());
    if (history != null) {
      throw new BaasException("code不能重复。");
    }

    menuDao.insert(tenant, item);
    return item.getUuid();
  }

  @Override
  @Tx
  public void saveModify(String tenant, Menu item) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(item, "item");
    Assert.notNull(item.getUuid(), "item.uuid");
    Assert.notNull(item.getCode(), "item.code");

    Menu history = menuDao.getByCode(tenant, item.getCode());
    if (history != null && !item.getCode().equals(history.getCode())) {
      throw new BaasException("code不能重复。");
    }

    menuDao.update(tenant, item);
  }

  @Override
  @Tx
  public void remove(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    Menu history = menuDao.get(tenant, uuid);
    if (history == null) {
      throw new BaasException("删除的对象不存在。");
    }

    menuDao.delete(tenant, uuid);
  }

  @Override
  @Tx
  public void reset(String tenant) throws BaasException {
    Assert.notNull(tenant, "tenant");
    // 读取配置
    MenuConfig config = client.getConfig(tenant, MenuConfig.class);
    if (config == null || StringUtils.isEmpty(config.getDefaultJson())) {
      log.info("查询的配置项为空.");
      return;
    }
    DefMenu1[] arrs = JsonUtil.jsonToObject(config.getDefaultJson(), DefMenu1[].class);
    if (arrs == null || arrs.length == 0) {
      log.info("查询的配置项的值为空.");
      return;
    }
    // 构建对象
    List<Menu> list = new ArrayList<>();
    int i = 0;
    for (DefMenu1 item : arrs) {
      i++;
      Menu menu = convert(tenant, item, i);
      list.add(menu);
      if (CollectionUtils.isNotEmpty(item.getChildren())) {
        for (DefMenu2 item2 : item.getChildren()) {
          i++;
          Menu sub = convert(tenant, item.getCode(), item2, i);
          list.add(sub);
        }
      }
    }
    // 删除
    menuDao.delete(tenant, MenuType.NORMAL);
    // 新增
    menuDao.batchInsert(tenant, list);
  }

  private Menu convert(String tenant, DefMenu1 item, int seq) {
    Menu menu = new Menu();
    menu.setUuid(IdGenUtils.buildIidAsString());
    menu.setTenant(tenant);
    menu.setType(MenuType.NORMAL);
    menu.setSequence(seq);
    menu.setCreateInfo(new OperateInfo());
    menu.setLastModifyInfo(new OperateInfo());

    menu.setCode(item.getCode());
    menu.setPath(item.getPath());
    menu.setTitle(item.getTitle());
    menu.setIcon(item.getIcon());
    return menu;
  }

  private Menu convert(String tenant, String upperCode, DefMenu2 item, int seq) {
    Menu menu = new Menu();
    menu.setUuid(IdGenUtils.buildIidAsString());
    menu.setTenant(tenant);
    menu.setType(MenuType.NORMAL);
    menu.setSequence(seq);
    menu.setCreateInfo(new OperateInfo());
    menu.setLastModifyInfo(new OperateInfo());

    menu.setUpperCode(upperCode);

    menu.setCode(item.getCode());
    menu.setPath(item.getPath());
    menu.setTitle(item.getTitle());
    menu.setIcon(item.getIcon());
    return menu;
  }

  @Override
  public Menu get(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    return menuDao.get(tenant, uuid);
  }

  @Override
  public List<Menu> getByUpperCode(String tenant, String upperCode) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(upperCode, "upperCode");
    return menuDao.getByUpperCode(tenant, upperCode);
  }

  @Override
  public List<Menu> listByParent(String tenant) {
    Assert.notNull(tenant, "tenant");
    return menuDao.getByUpperCode(tenant, null);
  }
}
