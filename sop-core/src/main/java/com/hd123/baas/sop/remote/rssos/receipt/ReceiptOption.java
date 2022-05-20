package com.hd123.baas.sop.remote.rssos.receipt;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "收货单实收是否允许大于实配数")
@Getter
@Setter
public class ReceiptOption {
    private Boolean receiptQtyAllowExcAlloc;
}
