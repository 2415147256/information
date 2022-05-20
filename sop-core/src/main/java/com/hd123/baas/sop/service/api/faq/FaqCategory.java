package com.hd123.baas.sop.service.api.faq;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.jdbc.annotation.ColumnName;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.MapToProperty;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuhaoxin
 */
@Getter
@Setter
public class FaqCategory extends Entity {

  public static final String[] FETCH_ALL = new String[] {};
  /**
   * 租户标识
   */
  private String tenant;
  /**
   * 分类id
   */
  private String categoryId;
  /**
   * 分类名称
   */
  private String categoryName;
  /**
   * 删除状态 0未删 1删除
   */
  private Integer deleted = 0;

  @QueryEntity(FaqCategory.class)
  public static class Queries extends QueryFactors.Entity {

    // 获取类中的字段名称
    public static final QueryFactorName PREFIX = QueryFactorName.prefix(FaqCategory.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String CATEGORY_NAME = PREFIX.nameOf("categoryName");
    @QueryField
    public static final String CATEGORY_ID = PREFIX.nameOf("categoryId");
    @QueryField
    public static final String DELETED = PREFIX.nameOf("deleted");

  }

  @SchemaMeta
  @MapToEntity(FaqCategory.class)
  public class FaqCategorySchema extends Schemas.Entity {

    @TableName
    public static final String TABLE_NAME = "faq_category";

    public static final String TABLE_ALIAS = "_faq_category";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    @MapToProperty(value = "categoryId")
    public static final String CATEGORY_ID = "category_id";
    @ColumnName
    @MapToProperty(value = "categoryName")
    public static final String CATEGORY_NAME = "category_name";
    @ColumnName
    public static final String DELETED = "deleted";
  }

}
