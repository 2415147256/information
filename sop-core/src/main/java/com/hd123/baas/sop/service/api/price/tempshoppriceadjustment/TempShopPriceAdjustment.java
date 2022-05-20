package com.hd123.baas.sop.service.api.price.tempshoppriceadjustment;

import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.*;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempShopPriceAdjustment extends StandardEntity {
  private String tenant;
  private String orgId;
  private String flowNo;
  private TempShopPriceAdjustmentState state = TempShopPriceAdjustmentState.INIT;
  private Date effectiveStartDate;
  private String reason;
  private String file;
  private List<TempShopPriceAdjustmentLine> lines;

  @SchemaMeta
  @MapToEntity(TempShopPriceAdjustment.class)
  public static class Schema extends Schemas.StandardEntity {
    @TableName
    public static final String TABLE_NAME = "temp_shop_price_adjustment";

    public static final String TABLE_ALIAS = "_temp_shop_price_adjustment";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "orgId")
    public static final String ORG_ID = "org_id";
    @ColumnName
    @MapToProperty(value = "flowNo")
    public static final String FLOW_NO = "flow_no";
    @ColumnName
    public static final String STATE = "state";
    @ColumnName
    @MapToProperty(value = "effectiveStartDate")
    public static final String EFFECTIVE_START_DATE = "effective_start_date";
    @ColumnName
    public static final String REASON = "reason";
    @ColumnName
    public static final String FILE = "file";
  }

  @QueryEntity(TempShopPriceAdjustment.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = TempShopPriceAdjustment.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String EFFECTIVE_START_DATE = PREFIX + "effectiveStartDate";
    @QueryField
    public static final String FLOW_NO = PREFIX + "flowNo";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
  }
}
