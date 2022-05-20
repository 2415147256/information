/**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PPromChannel.java
 * 模块说明:
 * 修改历史:
 * 2020年11月01日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.dao.channel;

import com.hd123.baas.sop.service.api.pms.channel.PromChannel;
import com.hd123.baas.sop.service.dao.PStandardEntity;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PPromChannel.TABLE_CAPTION, name = PPromChannel.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_channel_1", columnNames = { PPromChannel.TENANT})
})
public class PPromChannel extends PStandardEntity {

  public static final String TABLE_NAME = "sop_prom_channel";
  public static final String TABLE_CAPTION = "促销渠道";

  @Column(title = "租户", name = PPromChannel.TENANT, length = 38)
  public static final String TENANT = "tenant";
  @Column(title = "促销编号", name = PPromChannel.CODE, length = 38)
  public static final String CODE = "code";
  @Column(title = "名称", name = PPromChannel.NAME, length = 64)
  public static final String NAME = "name";
  @Column(title = "排序", name = PPromChannel.LINE_NO, fieldClass = Integer.class)
  public static final String LINE_NO = "lineNo";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          PStandardEntity.COLUMNS, TENANT, CODE, NAME, LINE_NO);

  public static Map<String, Object> toFieldValues(PromChannel entity) {
    Map<String, Object> fvm = Utils.toFieldValues(entity);
    fvm.put(TENANT, entity.getTenant());
    fvm.put(CODE, entity.getCode());
    fvm.put(NAME, entity.getName());
    fvm.put(LINE_NO, entity.getLineNo());
    return fvm;
  }

  public static PromChannel mapRow(ResultSet rs, int rowNum) throws SQLException {
    PromChannel target = new PromChannel();
    Utils.mapRow(rs, target);
    target.setTenant(rs.getString(TENANT));
    target.setCode(rs.getString(CODE));
    target.setName(rs.getString(NAME));
    target.setLineNo(rs.getInt(LINE_NO));
    return target;
  }
}
