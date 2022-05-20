package com.hd123.baas.sop.service.impl.electric;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboard;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboardHotKey;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboardService;
import com.hd123.baas.sop.service.dao.electricscale.ElectronicScaleKeyboardDaoBof;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElecScaleKeyboardServiceImpl implements ElecScaleKeyboardService {

  @Autowired
  ElectronicScaleKeyboardDaoBof keyboardDao;

  @Override
  public ElecScaleKeyboard get(String tenant, String elecScaleKeyBoard) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(elecScaleKeyBoard, "elecScaleKeyBoard");
    ElecScaleKeyboard keyboard = keyboardDao.get(tenant, elecScaleKeyBoard);
    if (keyboard == null) {
      throw new BaasException("键盘不存在");
    }
    List<ElecScaleKeyboardHotKey> hotKeys = keyboardDao.getHotKeys(tenant, elecScaleKeyBoard);
    keyboard.setHotKeys(hotKeys);
    return keyboard;
  }
}
