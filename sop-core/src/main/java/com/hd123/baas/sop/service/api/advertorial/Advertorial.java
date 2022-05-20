package com.hd123.baas.sop.service.api.advertorial;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Advertorial extends TenantStandardEntity {
  private String title;
  private String content;
  private String thUri;
  private String qrUrl;

  @QueryEntity(Advertorial.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = Advertorial.class.getName() + "::";
    @QueryField
    public static final String TITLE = PREFIX + "title";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryOperation
    public static final String KEYWORD = PREFIX + "keyword";

  }
}
