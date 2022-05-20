package com.hd123.baas.sop.evcall;

import java.beans.Introspector;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveReportV2EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveV2EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSubmittedEvCalExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoCreateEvCallEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanAutoOffEvCallEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sku.publishplan.SkuPublishPlanToH6EvCallExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.hd123.baas.sop.evcall.exector.announcement.AnnouncementEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.assignable.AssignableTaskPlanPublishEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosivePrepareOnOffEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveAutoStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fcf.H6ProcessEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fcf.ProcessEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fcf.UnfreezeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackApprovalEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackCreateAndSubmitEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackFetchEvCalExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackPushEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackRejectEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.feedback.FeedbackSaveApplyReasonEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.h6task.H6TaskDeliveredEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.message.MessageReadEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.platshopcategory.PlatShopCategoryExecutor;
import com.hd123.baas.sop.evcall.exector.price.AdjustmentLineWeekQtyEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.PricePromotionAuditedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.PricePromotionAutoAuditEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.PricePromotionTerminateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceCalculateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceFinishedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.TempPriceAdjustmentExecutor;
import com.hd123.baas.sop.evcall.exector.price.tempshop.TempShopPriceStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenEffectedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenTerminateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.BathBindCollocationGroupExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.BathBindTagExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.BathDisableExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.BathEnableExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.BathRemoveExecutor;
import com.hd123.baas.sop.evcall.exector.shopsku.ShopSkuExecutor;
import com.hd123.baas.sop.evcall.exector.shopskuinvcrule.BathUpdateInvcExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.PlanStateChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.shoptask.ShopTaskSummaryEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skumgr.DirectorySkuFinishedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skumgr.DirectorySkuStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skumgr.ShopSkuEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.ShopTagUploadEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.SkuTagChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.TagDeleteEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.TagSaveEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.ActivityRelationEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.ActivityRelationToH6EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.ActivityTerminatedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.ExceptionPlanEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.PlanPushEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.RelationEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.SubsidyPlanCallExecutor;
import com.hd123.baas.sop.evcall.exector.subsidyplan.TerminatedEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sysConfig.SysConfigChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.taskgroup.TaskGroupCreateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.timedjob.TimeJobEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.voice.ActivityVoiceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.voice.VoiceCallEvCallExecutor;
import com.hd123.rumba.evcall.EvCallManagerFactoryBean;
import com.hd123.rumba.evcall.EvCallManagerProperties;

@Configuration
public class EvCallConfiguration {

  @Resource(name = "sop-service.dataSource")
  private DataSource dataSource;
  @Value("${rumba-evcall.normal.executorThreads:6}")
  private int normalExecutorThreads;
  @Value("${rumba-evcall.normal.maxQueueSize:100}")
  private int normalMaxQueueSize;
  @Value("${rumba-evcall.normal.traceLog:true}")
  private boolean normalTraceLog;
  @Value("${rumba-evcall.normal.maxRetryInterval:86400000}") // 86400000 = 1天
  private long normalMaxRetryInterval;

  @Bean
  public EvCallManagerProperties evCallManagerProperties() {
    EvCallManagerProperties properties = new EvCallManagerProperties();
    properties.setName("normal");
    properties.setExecutorThreads(normalExecutorThreads);
    properties.setMaxQueueSize(normalMaxQueueSize);
    properties.setTraceLog(normalTraceLog);
    properties.setMaxRetryInterval(normalMaxRetryInterval);
    return properties;
  }

  @Bean
  public EvCallManagerFactoryBean evCallManager(@Autowired EvCallManagerProperties properties) {
    EvCallManagerFactoryBean bean = new EvCallManagerFactoryBean();
    bean.setDataSource(dataSource);
    bean.setProperties(properties);
    bean.addExecutorByBeanName(getDefaultBeanName(TaskGroupCreateEvCallExecutor.class),
        TaskGroupCreateEvCallExecutor.TASK_GROUP_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TimeJobEvCallExecutor.class),
        TimeJobEvCallExecutor.TIME_JOB_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackRejectEvCallExecutor.class),
        FeedbackRejectEvCallExecutor.FEEDBACK_REJECT_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackApprovalEvCallExecutor.class),
        FeedbackApprovalEvCallExecutor.FEEDBACK_APPROVAL_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackCreateAndSubmitEvCallExecutor.class),
        FeedbackCreateAndSubmitEvCallExecutor.FEEDBACK_CREATE_AND_SUBMIT_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackSaveApplyReasonEvCallExecutor.class),
        FeedbackSaveApplyReasonEvCallExecutor.FEEDBACK_SAVE_APPLY_REASON_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackFetchEvCalExecutor.class),
        FeedbackFetchEvCalExecutor.FEEDBACK_FETCH_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackSubmittedEvCalExecutor.class),
        FeedbackSubmittedEvCalExecutor.FEEDBACK_SUBMITTED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FeedbackPushEvCallExecutor.class),
        FeedbackPushEvCallExecutor.FEEDBACK_PUSH_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopPriceEvCallExecutor.class),
        ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopPriceStartEvCallExecutor.class),
        ShopPriceStartEvCallExecutor.EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopPriceCalculateEvCallExecutor.class),
        ShopPriceCalculateEvCallExecutor.SHOP_PRICE_CALCULATE_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(SysConfigChangeEvCallExecutor.class),
        SysConfigChangeEvCallExecutor.SYSCONFIG_CHANGE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(AnnouncementEvCallExecutor.class),
        AnnouncementEvCallExecutor.ANNOUNCEMENT_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(MessageReadEvCallExecutor.class),
        MessageReadEvCallExecutor.MESSAGE_READ_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopPriceFinishedEvCallExecutor.class),
        ShopPriceFinishedEvCallExecutor.SHOP_PRICE_FINISHED_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(H6TaskDeliveredEvCallExecutor.class),
        H6TaskDeliveredEvCallExecutor.EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(FmsSendEvCallExecutor.class),
        FmsSendEvCallExecutor.FMS_SEND_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TempPriceAdjustmentExecutor.class),
        TempPriceAdjustmentExecutor.TEMP_SHOP_PRICE_ADJUSTMENT_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopSkuExecutor.class), ShopSkuExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(DirectorySkuFinishedEvCallExecutor.class),
        DirectorySkuFinishedEvCallExecutor.SHOP_SKU_FINISHED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(DirectorySkuStartEvCallExecutor.class),
        DirectorySkuStartEvCallExecutor.EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopSkuEvCallExecutor.class),
        ShopSkuEvCallExecutor.SHOP_SKU_TASK_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(VoiceCallEvCallExecutor.class),
        VoiceCallEvCallExecutor.VOICE_CALL_MSG_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopTaskSummaryEvCallExecutor.class),
        ShopTaskSummaryEvCallExecutor.SHOP_TASK_SUMMARY_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PlanStateChangeEvCallExecutor.class),
        PlanStateChangeEvCallExecutor.PLAN_STATE_CHANGE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ExplosivePrepareOnOffEvCallExecutor.class),
        ExplosivePrepareOnOffEvCallExecutor.EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ExplosiveAutoStartEvCallExecutor.class),
        ExplosiveAutoStartEvCallExecutor.EXPLOSIVE_AUTO_START_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ActivityVoiceEvCallExecutor.class),
        ActivityVoiceEvCallExecutor.ACTIVITY_VOICE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(AssignableTaskPlanPublishEvCallExecutor.class),
        AssignableTaskPlanPublishEvCallExecutor.ASSIGNABLE_TASK_PLAN_PUBLISH_EXECUTOR_ID);

    bean.addExecutorByBeanName(getDefaultBeanName(UnfreezeEvCallExecutor.class),
        UnfreezeEvCallExecutor.UNFREEZE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ProcessEvCallExecutor.class),
        ProcessEvCallExecutor.PROCESS_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(H6ProcessEvCallExecutor.class),
        H6ProcessEvCallExecutor.PROCESS_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(AdjustmentLineWeekQtyEvCallExecutor.class),
        AdjustmentLineWeekQtyEvCallExecutor.ADJUSTMENT_LINE_WEEK_QTY_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PricePromotionAuditedEvCallExecutor.class),
        PricePromotionAuditedEvCallExecutor.PRICE_PROMOTION_AUDITED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PricePromotionTerminateEvCallExecutor.class),
        PricePromotionTerminateEvCallExecutor.PRICE_PROMOTION_TERMINATE_EXECUTOR_ID);
    // 添加subsidy执行器
    bean.addExecutorByBeanName(getDefaultBeanName(ActivityRelationEvCallExecutor.class),
        ActivityRelationEvCallExecutor.ACTIVITY_RELATION_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(RelationEvCallExecutor.class),
        RelationEvCallExecutor.RELATION_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ActivityRelationToH6EvCallExecutor.class),
        ActivityRelationToH6EvCallExecutor.ACTIVITY_RELATION_TOH6_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ActivityTerminatedEvCallExecutor.class),
        ActivityTerminatedEvCallExecutor.ACTIVITY_TERMINATED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(ExceptionPlanEvCallExecutor.class),
        ExceptionPlanEvCallExecutor.PLAN_EXCEPTION_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PlanPushEvCallExecutor.class),
        PlanPushEvCallExecutor.PLAN_PUSH_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TerminatedEvCallExecutor.class),
        TerminatedEvCallExecutor.TERMINATED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(SubsidyPlanCallExecutor.class),
        SubsidyPlanCallExecutor.SUBSIDY_PLAN_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PricePromotionAutoAuditEvCallExecutor.class),
        PricePromotionAutoAuditEvCallExecutor.PRICE_PROMOTION_AUTO_AUDIT_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PlatShopCategoryExecutor.class),
        PlatShopCategoryExecutor.PLAT_SHOP_CATEGORY_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TempShopPriceStartEvCallExecutor.class),
        TempShopPriceStartEvCallExecutor.TEMP_SHOP_PRICE_START_EV_CALL);
    bean.addExecutorByBeanName(getDefaultBeanName(ShopTagUploadEvCallExecutor.class),
        ShopTagUploadEvCallExecutor.SHOP_TAG_UPLOAD_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TagDeleteEvCallExecutor.class),
        TagDeleteEvCallExecutor.TAG_DELETE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(TagSaveEvCallExecutor.class),
        TagSaveEvCallExecutor.TAG_SAVE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(SkuTagChangeEvCallExecutor.class),
        SkuTagChangeEvCallExecutor.SKU_TAG_CHANGE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PriceScreenEffectedEvCallExecutor.class),
        PriceScreenEffectedEvCallExecutor.PRICE_SCREEN_EFFECTED_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(PriceScreenTerminateEvCallExecutor.class),
        PriceScreenTerminateEvCallExecutor.PRICE_SCREEN_TERMINATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathBindCollocationGroupExecutor.class),
        BathBindCollocationGroupExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathBindTagExecutor.class), BathBindTagExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathDisableExecutor.class), BathDisableExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathEnableExecutor.class), BathEnableExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathRemoveExecutor.class), BathRemoveExecutor.SHOP_SKU_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(BathUpdateInvcExecutor.class),
        BathUpdateInvcExecutor.SHOP_SKU_EXECUTOR_ID);
    //ExplosiveSignV2监听器
    bean.addExecutorByBeanName(getDefaultBeanName(ExplosiveReportV2EvCallExecutor.class),
        ExplosiveReportV2EvCallExecutor.EXPLOSIVE_REPORT_V2_EXECUTOR_ID);
    // 商品上下架推送
    bean.addExecutorByBeanName(getDefaultBeanName(SkuPublishPlanToH6EvCallExecutor.class),
        SkuPublishPlanToH6EvCallExecutor.SKU_PUBLISH_PLAN_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(SkuPublishPlanAutoCreateEvCallEvCallExecutor.class),
        SkuPublishPlanAutoCreateEvCallEvCallExecutor.SKU_PUBLISH_PLAN_AUTO_CREATE_EXECUTOR_ID);
    bean.addExecutorByBeanName(getDefaultBeanName(SkuPublishPlanAutoOffEvCallEvCallExecutor.class),
        SkuPublishPlanAutoOffEvCallEvCallExecutor.SKU_PUBLISH_PLAN_AUTO_OFF_EXECUTOR_ID);
    ////// ====定义=======//////
    bean.addExecutorByBeanName(getDefaultBeanName(ExplosiveV2EvCallExecutor.class),
        ExplosiveV2EvCallExecutor.EXPLOSIVE_V2_EXECUTOR_ID);
    return bean;
  }

  private String getDefaultBeanName(Class cls) {
    // 参考 AnnotationBeanNameGenerator；
    String shortClassName = ClassUtils.getShortName(cls);
    String beanName = Introspector.decapitalize(shortClassName);
    return beanName;
  }

}
