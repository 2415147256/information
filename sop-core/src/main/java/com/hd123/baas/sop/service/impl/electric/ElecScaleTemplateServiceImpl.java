package com.hd123.baas.sop.service.impl.electric;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.electricscale.ElecScale;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboard;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleTemplate;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleTemplateService;
import com.hd123.baas.sop.service.dao.electricscale.ElecScaleDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ElecScaleTemplateDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ElectronicScaleKeyboardDaoBof;
import com.hd123.baas.sop.service.dao.electricscale.ShopElecScaleTemplateDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElecScaleTemplateServiceImpl implements ElecScaleTemplateService {

  @Autowired
  ElectronicScaleKeyboardDaoBof keyboardDao;
  @Autowired
  ElecScaleTemplateDaoBof templateDao;
  @Autowired
  ShopElecScaleTemplateDaoBof shopElecScaleTemplateDao;
  @Autowired
  ElecScaleDaoBof elecScaleDao;

  @Tx
  @Override
  public String create(String tenant, ElecScaleTemplate template, ElecScaleKeyboard keyboard, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(template, "template");
    Assert.notNull(template.getName(), "name");
    Assert.notNull(template.getElectronicScale(), "elecScale");
    Assert.notNull(keyboard, "keyboard");
    // 保存热键
    String keyboardUuid = this.insertElecScaleKeyboard(tenant, keyboard);
    template.setElecScaleKeyBoard(keyboardUuid);
    return templateDao.insert(tenant, template, operateInfo);
  }

  @Tx
  @Override
  public String modify(String tenant, ElecScaleTemplate template, ElecScaleKeyboard keyboard, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyboard, "keyboard");
    Assert.notNull(template.getName(), "name");
    Assert.notNull(template.getElectronicScale(), "elecScale");
    ElecScaleTemplate oldTemplate = templateDao.get(tenant, template.getUuid());
    if (oldTemplate == null) {
      throw new BaasException("原电子秤模板不存在");
    }
    ElecScale elecScale = elecScaleDao.get(tenant, template.getElectronicScale());
    if (elecScale == null) {
      throw new BaasException("电子秤不存在");
    }
    // 先删除原模板
    this.delete(tenant, template.getUuid());
    String keyboardUuid = this.insertElecScaleKeyboard(tenant, keyboard);
    template.setElecScaleKeyBoard(keyboardUuid);
    return templateDao.insert(tenant, template, operateInfo);
  }

  @Tx
  @Override
  public void bindShop(String tenant, String keyBoardTemUuid, boolean allShop, List<UCN> shops)
      throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyBoardTemUuid, "模板id");
    ElecScaleTemplate template = templateDao.get(tenant, keyBoardTemUuid);
    if (template == null) {
      throw new BaasException("模板不存在");
    }
    // 解绑门店
    shopElecScaleTemplateDao.deleteByTemplate(tenant, keyBoardTemUuid);
    if (!allShop && CollectionUtils.isEmpty(shops)) {
      return;
    }
    if (allShop) {
      shopElecScaleTemplateDao.bindAllShop(tenant, keyBoardTemUuid);
    } else {
      shopElecScaleTemplateDao.bindShops(tenant, keyBoardTemUuid, shops);
    }
  }

  @Tx
  @Override
  public void delete(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    ElecScaleTemplate template = templateDao.get(tenant, uuid);
    if (template == null) {
      throw new BaasException("模板不存在");
    }
    // 删除门店电子秤模板
    shopElecScaleTemplateDao.deleteByTemplate(tenant, uuid);
    ElecScaleKeyboard keyboard = keyboardDao.get(tenant, template.getElecScaleKeyBoard());
    // 删除键盘
    keyboardDao.deleteHotKeyByKeyboard(tenant, keyboard.getUuid());
    keyboardDao.delete(tenant, keyboard.getUuid());
    templateDao.delete(tenant, uuid);
  }

  @Override
  public QueryResult<ElecScaleTemplate> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    return templateDao.query(tenant, qd);
  }

  @Override
  public List<ElecScaleTemplate> listByUuid(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids, "uuids");
    return templateDao.listByUuid(tenant, uuids);
  }

  private String insertElecScaleKeyboard(String tenant, ElecScaleKeyboard keyboard) {
    Assert.notNull(keyboard, "keyboard");
    // 保存热键
    String uuid = keyboardDao.insert(tenant, keyboard);
    keyboardDao.saveHotKeys(tenant, keyboard.getHotKeys());
    return uuid;
  }
}
