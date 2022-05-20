package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Applyer {
  @JsonProperty("userid")
  private String userId;

  @JsonProperty("partyid")
  private String partyId;
}
