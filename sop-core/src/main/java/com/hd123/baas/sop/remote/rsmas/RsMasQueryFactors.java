/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-client-api
 * 文件名：	MasQueryFactors.java
 * 模块说明：	
 * 修改历史：
 * 2019年3月4日 - __Silent - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryFieldPurpose;

/**
 * @author __Silent
 *
 */
public class RsMasQueryFactors extends QueryFactors {

  public static abstract class RsMasEntity extends StandardEntity {

    private static final String PREFIX = RsMasEntity.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField(fieldType = Boolean.class)
    public static final String DELETED = PREFIX + "deleted";
    @QueryField
    public static final String LAST_MODIFIED = PREFIX + "lastModifyInfo.time";
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";

    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String UUID_ORDER = "uuid";
    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String ID_ORDER = "id";
    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String LAST_MODIFIED_ORDER = "lastModifyInfo.time";
    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String CREATED_ORDER = "createInfo.time";
  }
}
