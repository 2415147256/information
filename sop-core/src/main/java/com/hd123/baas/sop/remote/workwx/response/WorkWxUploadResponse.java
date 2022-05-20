package com.hd123.baas.sop.remote.workwx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 企业微信上传回执
 */
@Setter
@Getter
public class WorkWxUploadResponse extends BaseWorkWxResponse {
  /** 媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file) */
  private String type;
  /** 媒体文件上传后获取的唯一标识，3天内有效 */
  @JsonProperty(value = "media_id")
  private String mediaId;
  /** 媒体文件上传时间戳 */
  @JsonProperty(value = "created_at")
  private String createdAt;
}
