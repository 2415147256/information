package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalComment {
  private UserId commentUserInfo;

  @JsonProperty("commenttime")
  private Long commentTime;

  @JsonProperty("commentcontent")
  private String commentContent;

  @JsonProperty("commentid")
  private String commentId;

  @JsonProperty("media_id")
  private List<String> mediaIds;
}
