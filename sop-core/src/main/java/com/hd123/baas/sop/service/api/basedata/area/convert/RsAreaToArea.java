package com.hd123.baas.sop.service.api.basedata.area.convert;


import com.hd123.baas.sop.service.api.basedata.area.Area;
import com.hd123.baas.sop.remote.rsmas.area.RsArea;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author shenmin
 */
public class RsAreaToArea implements Converter<RsArea, Area> {

  @Override
  public Area convert(RsArea entity) throws ConversionException {
    Assert.notNull(entity);
    Area area = new Area();
    area.setId(entity.getId());
    area.setCode(entity.getCode());
    area.setName(entity.getName());
    area.setUuid(entity.getUuid());
    return area;
  }

}
