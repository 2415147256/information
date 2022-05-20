package com.hd123.baas.sop.service.api;

import com.hd123.rumba.commons.biz.entity.VersionedEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author W.J.H.7
 * @since 1.1.0
 **/
@Getter
@Setter
public class ThinEntity extends VersionedEntity {
  /** 创建时间 **/
  private Date created;
  /** 更新时间 **/
  private Date lastModified;

}
