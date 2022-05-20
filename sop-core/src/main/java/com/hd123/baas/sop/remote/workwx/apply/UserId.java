package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Y.U.A.N
 */
@Data
public class UserId {
  @JsonProperty("userid")
  private String userId;

  private Integer status;
}
