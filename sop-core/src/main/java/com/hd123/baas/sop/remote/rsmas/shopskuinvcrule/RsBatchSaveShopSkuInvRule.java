package com.hd123.baas.sop.remote.rsmas.shopskuinvcrule;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 门店商品库存规则
 * 
 * @author qyh
 *
 */
@Setter
@Getter
@ApiModel("批量保存门店商品库存规则")
public class RsBatchSaveShopSkuInvRule extends RsMasEntity {

	private static final long serialVersionUID = -3508483408565234344L;
	private List<RsShopSkuInvRule> items;

}
