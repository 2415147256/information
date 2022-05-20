package com.hd123.baas.sop.evcall.exector.fcf;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import com.hd123.baas.sop.remote.rsh6sop.fcf.H6ProcessOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class H6ProcessOrderMsg extends AbstractTenantEvCallMessage {
    private String uuid;
    private String StoreGid;
    private Date ocrTime;
    private List<H6ProcessOrder.H6FreshGoods> details =new ArrayList<>();
}
