package com.hd123.baas.sop.remote.workwx.apply;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hd123.baas.sop.remote.workwx.deserializer.TableFormControlDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TableChildren {
  @JsonDeserialize(using = TableFormControlDeserializer.class)
  private List<TableChildrenDetail> list = new ArrayList<>();
}
