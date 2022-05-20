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
public class FaqArticleRecord extends Entity {
  /**
   * 租户
   */
  private String tenant;
  /**
   * 文章id
   */
  private String owner;
  /**
   * 文章id
   */
  private Integer isHelp;
  /**
   * 删除标记 0未删 1删除
   */
  private Integer deleted = 0;
  /**
   * 操作人
   */
  private String operator;

  @QueryEntity(FaqArticleRecord.class)
  public static class Queries extends QueryFactors.Entity {

    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(FaqArticleRecord.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String OPERATOR = PREFIX.nameOf("operator");
    @QueryField
    public static final String DELETED = PREFIX.nameOf("deleted");
    @QueryField
    public static final String IS_HELP = PREFIX.nameOf("isHelp");
  }

  @SchemaMeta
  @MapToEntity(FaqArticleRecord.class)
  public class FaqArticleRecordSchema extends Schemas.Entity {

    @TableName
    public static final String TABLE_NAME = "faq_article_record";

    public static final String TABLE_ALIAS = "_faq_article_record";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty("isHelp")
    public static final String IS_HELP = "is_help";
    @ColumnName
    public static final String OPERATOR = "operator";
    @ColumnName
    public static final String DELETED = "deleted";
  }
}
