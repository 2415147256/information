package com.hd123.baas.sop.service.dao.skumgr;

import java.util.HashMap;
import java.util.Map;

import com.hd123.baas.sop.service.api.skumgr.DirectorySkuManager;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class PDirectoryManager extends PEntity {
  public static final String TABLE_NAME = "directory_sku_manager";
  public static final String TABLE_ALIAS = "_directory_sku_manager";

  public static final String TENANT = "tenant";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String SKU_QPC = "sku_qpc";
  public static final String SKU_GID = "sku_gid";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String ISSUE_DATE = "issue_date";
  public static final String CHANNEL_REQUIRED = "channel_required";
  public static final String DIRECTORY_REQUIRED = "directory_required";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, SKU_ID, SKU_CODE, SKU_NAME, SKU_QPC, SHOP, SHOP_CODE, SHOP_NAME,
        ISSUE_DATE, CHANNEL_REQUIRED, DIRECTORY_REQUIRED, SKU_GID);
  }

  public static Map<String, Object> getBizMap(String tenant, DirectorySkuManager directorySkuManager) {
    Map<String, Object> map = new HashMap<>();
    map.put(TENANT, tenant);
    map.put(UUID, IdGenUtils.buildRdUuid());
    map.put(SKU_ID, directorySkuManager.getSkuId());
    map.put(SKU_CODE, directorySkuManager.getSkuCode());
    map.put(SKU_NAME, directorySkuManager.getSkuName());
    map.put(SKU_QPC, directorySkuManager.getSkuQpc());
    map.put(SKU_GID, directorySkuManager.getSkuGid());
    map.put(SHOP, directorySkuManager.getShop());
    map.put(SHOP_CODE, directorySkuManager.getShopCode());
    map.put(SHOP_NAME, directorySkuManager.getShopName());
    map.put(ISSUE_DATE, directorySkuManager.getIssueDate());
    map.put(DIRECTORY_REQUIRED, directorySkuManager.isDirectoryRequired());
    map.put(CHANNEL_REQUIRED, directorySkuManager.isChannelRequired());
    return map;
  }
}
