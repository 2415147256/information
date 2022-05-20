package com.hd123.baas.sop.service.api.promotion;

import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import com.hd123.baas.sop.service.api.promotion.condition.StepCondition;
import com.hd123.baas.sop.service.api.promotion.execution.SpecialPriceExecution;
import com.qianfan123.baas.common.BaasException;
import org.springframework.util.CollectionUtils;

/**
 * @author zhuangwenting
 * @since 1.0
 */
public class PromotionValidator {
  public static void validate(Promotion target, boolean required) throws BaasException {
    if (target.getPromotionType() == PromotionType.price) {
      if (target.getProductCondition() == null || target.getProductCondition().getEntityType() != EntityType.product) {
        if (required) {
          throw new BaasException("请求参数不正确，请检查");
        }
        return;
      }
      if (CollectionUtils.isEmpty(target.getProductCondition().getItems())) {
        throw new BaasException("商品列表不能为空");
      }
      for (ProductCondition.Item item : target.getProductCondition().getItems()) {
        if (item.getPrmPrice() == null) {
          throw new BaasException("指定商品" + item.getName() + "[" + item.getCode() + "]未填写促销价");
        }
      }
    } else if (target.getPromotionType() == PromotionType.gdSpecialPrice) {
      validateSpecialPriceExecution(target.getExecutionSet() != null ? target.getExecutionSet().getSpecialPriceExecution() : null, required);
    } else if (target.getPromotionType() == PromotionType.specialPrice) {
      if (target.getStepCondition() == null || CollectionUtils.isEmpty(target.getStepCondition().getStepCases())) {
        if (required) {
          throw new BaasException("请求参数不正确，请检查");
        }
        return;
      }
      for (StepCondition.StepCase stepCase : target.getStepCondition().getStepCases()) {
        validateSpecialPriceExecution(stepCase.getExecutionSet() != null ? stepCase.getExecutionSet().getSpecialPriceExecution() : null, required);
      }
    }
    if (target.getStepCondition() != null) {
      if (target.getStepCondition().getPriceType() != StepCondition.PriceType.actualPrice) {
        throw new BaasException("阶梯价格类型错误，当前仅支持actualPrice");
      }
    }
  }

  public static void validateSpecialPriceExecution(SpecialPriceExecution specialPriceExecution, boolean required) throws BaasException {
    if (specialPriceExecution == null) {
      if (required) {
        throw new BaasException("请求参数不正确，请检查");
      }
      return;
    }
    if (specialPriceExecution.getItems().isEmpty()) {
      throw new BaasException("商品列表不能为空");
    }
    for (SpecialPriceExecution.Item item : specialPriceExecution.getItems()) {
      if (item.getValue() == null) {
        String type = item.getForm() == SpecialPriceExecution.Form.discount ? "折扣" : "换购价";
        throw new BaasException("指定商品" + item.getEntity().getName() + "[" + item.getEntity().getCode() + "]未填写" + type);
      }
    }
  }
}
