package com.hd123.baas.sop.service.api.sysconfig;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class SysConfig implements Serializable {

  /** 店务设置 */
  /** 收货质量反馈截止时间 **/
  public static final String KEY_FEEDBACK_ENDTIME = "feedbackEndTime";
  /** 费用生成时间 **/
  public static final String KEY_FEE_GENERATE_TIME = "feeGenerateTime";
  /** 反馈时间设置（确认收货后X天可反馈） **/
  public static final String KEY_FEEDBACK_DAYS = "feedbackDays";
  /** 门店调拨自动审核 **/
  public static final String KEY_AUTO_AUDITOF_INV_XF = "autoAuditOfInvXF";
  /** 门店要货时间范围 开始 **/
  public static final String SHOP_GET_GOOD_TIME_AROUND_START = "getGoodStartTime";
  /** 门店要货时间范围 结束 **/
  public static final String SHOP_GET_GOOD_TIME_AROUND_END = "getGoodEndTime";
  /** 门店要货时间偏移量 **/
  public static final String SHOP_GET_GOOD_OFF_SET_DAY = "getGoodOffsetDays";
  /** 原因维护 加单要货原因 **/
  public static final String SHOP_REASON_ADD = "shopReasonAddGood";
  /** 收货单实收是否允许大于实配数 默认值：否 **/
  public static final String RECEIVE_NOTE_MORE_REAL_COUNT = "isMoreRealCount";
  /** 原因维护 报损原因 **/
  public static final String SHOP_REASON_DAMAGES = "shopReasonReportDamages";
  /** 原因维护 报溢原因 **/
  public static final String SHOP_REASON_MORE = "shopReasonReportMore";
  /** 门店公告 存在未读消息是否必读 **/
  public static final String MESSAGE_MUST_READ = "messageMustRead";
  /** 质量反馈时是否可以选择图片 */
  public static final String ENABLE_CHOOSE_IMAGE = "enableChooseImage";
  /** 质量反馈可反馈时间限制模式 */
  public static final String TIME_LMT_MODE = "timeLmtMode";
  /** 自动审核设置 */
  public static final String FEEDBACK_AUTO_AUDIT = "feedbackAutoAudit";
  /** 质量反馈等级 */
  public static final String FEEDBACK_GRADES = "feedbackGrades";

  /** 门店促销价是否联动售价 **/
  public static final String KEY_CHANGE_PRICE_BY_PROMT = "changePriceByPromt";

  /** 爆品活动设置 **/
  public static final String KEY_EXPLOSIVE_ACTIVITY = "explosiveActivity";
  /** 促销原因设置 **/
  public static final String EXPLOSIVE_ACTIVITY_REASON_MARKET = "reasonMarket";

  public static final String DATE_FORMAT = "HH:mm";


  /** 爆品活动 语音提醒提前时间（min） **/
  public static final String EXPLOSIVE_ACTIVITY_VOICE_TIME = "explosiveActivityVoiceTime";

  /** 罚息规则 **/
  public static final String FINE_RULE = "fineRule";

  /** 门店停止叫货时间端 **/
  public static final String SHOP_ORDER_STOP_DATE = "shopOrderStopDate";

  /** 督导可设置的最低折扣 **/
  public static final String SUPER_LOWEST_DISCOUNT = "superLowestDiscount";

  /** 门店额度最低限制 **/
  public static final String SHOP_LOWEST_AMOUNT_LIMIT = "shopLowestAmountLimit";

  /** 督导岗位设置 */
  public static final String SUPER_POSITIONS = "superPositions";

  /** 图片轮播间隔时间（秒） **/
  public static final String SCREEN_PICTURE_CAROUSEL_SECONDS = "screenPictureCarouselSeconds";
  /** 图片轮播图片 **/
  public static final String SCREEN_PICTURE_URLS = "screenPictureUrls";

  /** 广告语间隔时间（秒） **/
  public static final String SCREEN_TIP_CAROUSEL_SECONDS = "screenTipCarouselSeconds";
  /** 广告语 **/
  public static final String SCREEN_TIPS = "screenTips";

  /** 价格表轮播间隔时间（秒） **/
  public static final String SCREEN_PRICE_CAROUSEL_SECONDS = "screenPriceCarouselSeconds";

  /** 督导巡检的图片、视频反馈设置 **/
  public static final String SUPERVISOR_FEEDBACK = "supervisorFeedback";

  /** Apos手工折扣原因 */
  public static final String APOS_HANDWORK_REASON = "aposHandworkReason";
  /** 老门店判断天数限制 */
  public static final String OLD_SHOP_DAYS = "oldShopDays";
  /** 质量反馈商品选择范围配置 */
  public static final String FEEDBACK_GOODS_SCOPE = "feedbackGoodsScope";
  /** 排除门店集合对象 */
  public static final String GET_GOOD_NOT_LIMIT_STORES = "getGoodNotLimitStores";

  /** 督导银行预扣额度 */
  public static final String DDYH_PROMOTION_TARGET_DEF = "ddyh_promotion_target_def";

  /** key **/
  private String tenant;
  /** spec **/
  private String spec = "def";
  /** key **/
  private String cfKey;
  /** value **/
  private String cfValue;
}
