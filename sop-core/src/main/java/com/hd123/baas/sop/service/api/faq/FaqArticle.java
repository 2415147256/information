package com.hd123.baas.sop.service.api.faq;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
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
public class FaqArticle extends StandardEntity {

  public static final String FETCH_ARTICLE = "part_article";
  public static final String FETCH_CATEGORY = "part_categofy";

  public static final String[] FETCH_ALL = new String[] {
      FETCH_ARTICLE, FETCH_CATEGORY };
  /**
   * 租户标识
   */
  private String tenant;
  /**
   * 分类id
   */
  private String owner;
  /**
   * 文章id
   */
  private String articleId;
  /**
   * 文章名称
   */
  private String articleName;
  /**
   * 文章内容
   */
  private String content;
  /**
   * 扩展信息
   */
  private String urlJson;
  /**
   * 删除标记
   */
  private Integer deleted = 0;
  /**
   * 分类信息
   */
  private FaqCategory category;
  /**
   * 是否点赞 0 同意 1反对
   */
  private Boolean isAgree;
  /**
   * 有帮助数量
   */
  private Integer helpNum = Integer.valueOf(0);
  /**
   * 无帮助数量
   */
  private Integer noHelpNum = Integer.valueOf(0);

  @QueryEntity(FaqArticle.class)
  public static class Queries extends QueryFactors.StandardEntity {

    // 获取类中的字段名称
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(FaqArticle.class);

    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ARTICLE_NAME = PREFIX.nameOf("articleName");
    @QueryField
    public static final String ARTICLE_ID = PREFIX.nameOf("articleId");
    @QueryField
    public static final String CONTENT = PREFIX.nameOf("content");
    @QueryField
    public static final String OWNER = PREFIX.nameOf("owner");
    @QueryField
    public static final String DELETED = PREFIX.nameOf("deleted");
    @QueryOperation
    public static final String ARTICLE_KEYWORD_LIKE = PREFIX.nameOf("skuKeyword like");
    @QueryOperation
    public static final String CREATOR_KEYWORD_LIKE = PREFIX.nameOf("creatorKeyword like");
    @QueryOperation
    public static final String ARTICLE_XCX_KEYWORD_LIKE = PREFIX.nameOf("articleXcxKeyword like");
  }

  @SchemaMeta
  @MapToEntity(FaqArticle.class)
  public class FaqArticleSchema extends Schemas.StandardEntity {

    @TableName
    public static final String TABLE_NAME = "faq_article";

    public static final String TABLE_ALIAS = "_faq_article";

    @ColumnName
    public static final String TENANT = "tenant";
    @ColumnName
    public static final String OWNER = "owner";
    @ColumnName
    @MapToProperty(value = "articleId")
    public static final String ARTICLE_ID = "article_id";
    @ColumnName
    @MapToProperty(value = "articleName")
    public static final String ARTICLE_NAME = "article_name";
    @ColumnName
    @MapToProperty(value = "content")
    public static final String CONTENT = "content";
    @ColumnName
    @MapToProperty(value = "urlJson")
    public static final String URL_JSON = "url_json";
    @ColumnName
    public static final String DELETED = "deleted";
  }

}
