package com.hd123.baas.sop.service.api.sysconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 督导巡检反馈设置
 *
 * @Author guyahui
 * @Since
 */
@Setter
@Getter
public class SupervisorFeedback {
  @ApiModelProperty("是否允许从相册选择图片")
  private boolean chosePicFromAlbum;
  @ApiModelProperty("是否允许从相册选择视频")
  private boolean choseVideoFromAlbum;
}
