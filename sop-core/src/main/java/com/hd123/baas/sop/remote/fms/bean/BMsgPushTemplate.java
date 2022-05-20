package com.hd123.baas.sop.remote.fms.bean;

import com.hd123.baas.sop.service.api.voice.VoiceTemplate;
import com.hd123.baas.sop.service.api.voice.VoiceTemplateCode;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("MsgPushTemplate 实体")
@EqualsAndHashCode(callSuper = true)
public class BMsgPushTemplate extends Entity {

  private static final Converter<BMsgPushTemplate, VoiceTemplate> MSG_PUSH_TEMPLATE_TO_VOICE_MSG_PUSH_REQ_CONVERTER
      = ConverterBuilder.newBuilder(BMsgPushTemplate.class, VoiceTemplate.class)
      .map("code", EnumConverters.toEnum(VoiceTemplateCode.class))
      .map("context", "content")
      .build();

  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("代码")
  private String code;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("类型")
  private String type;
  @ApiModelProperty("内容")
  private String context;

  public VoiceTemplate toVoiceTemplate() {
    return MSG_PUSH_TEMPLATE_TO_VOICE_MSG_PUSH_REQ_CONVERTER.convert(this);
  }
}