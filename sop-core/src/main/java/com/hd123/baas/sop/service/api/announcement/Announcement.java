package com.hd123.baas.sop.service.api.announcement;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/7.
 * 
 *         公告
 * 
 */
@Getter
@Setter
public class Announcement extends TenantStandardEntity {

  // 标题
  private String title;
  // 是否全门店
  private boolean allShops;
  // 是否发布到收银机
  private boolean sendPos;
  // 内容
  private String content;
  // 图片
  private String image;
  // 外部链接
  private String url;
  // 组织id
  private String orgId;
  // 公告类型
  private AnnouncementTargetType targetType = AnnouncementTargetType.SHOP;

  // 状态
  private AnnouncementState state = AnnouncementState.UNPUBLISHED;
  // 进度、门店完成状态
  private AnnouncementProgress progress = AnnouncementProgress.UNFINISHED;

  private List<AnnouncementShop> shops = new ArrayList<>();

  @QueryEntity(Announcement.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = Announcement.class.getName() + "::";

    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String PROGRESS = PREFIX + "progress";
    @QueryField
    public static final String TITLE = PREFIX + "title";

  }

}
