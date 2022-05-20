package com.hd123.baas.sop.service.api.basedata.category;

import java.util.ArrayList;
import java.util.List;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "分类")
public class Category {

  /** 根路径ID */
  public static final String ROOT_ID = "-";
  /** 级联直接子类 */
  public static final String PART_CHILD = "child";
  /** 级联全部子类 */
  public static final String PART_CHILDREN = "children";

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "友好性文本")
  public String friendlyStr;
  @ApiModelProperty(value = "上级分类ID")
  public String upperId;
  @ApiModelProperty(value = "层级码")
  public int level;
  @ApiModelProperty("分类路径")
  private String path;
  @ApiModelProperty(value = "下级分类")
  public List<Category> categories = new ArrayList<Category>();

  @QueryEntity(Category.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Category.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String LEVEL = PREFIX + "level";
    @QueryField
    public static final String UPPER_ID = PREFIX + "upperId";
  }

}
