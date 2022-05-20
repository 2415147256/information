package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lins
 */
@Getter
@Setter
public class RsInvSyncReq {

    private List<RsInvSync> lines;
    private String requestId;
}
