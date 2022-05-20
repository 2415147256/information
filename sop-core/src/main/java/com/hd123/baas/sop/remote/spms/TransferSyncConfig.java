package com.hd123.baas.sop.remote.spms;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Data;

@Data
@BcGroup(name = "促销价数据下发H6")
public class TransferSyncConfig {
  private static final String PREFIX = "spms.sync.";

  @BcKey(name = "目标租户ID")
  private String tenantId;
  @BcKey(name = "促销同步地址")
  private String url;
  @BcKey(name = "accessKeyId")
  private String accessKeyId;
  @BcKey(name = "accessKeySecret")
  private String accessKeySecret;
  @BcKey(name = "规格特价转换")
  private boolean convertQpcPrice;
  @BcKey(name = "需要转换cover qpc的促销类型")
  private String coverQpc;
  private String seqGroupMap = "1=100,2=200,3=300,4=400,5=500,6=600,7=700,8=800,9=900,999=999,0.5=50";
  private String typeSeqGroupMap = "scoreDeduct=990,baseScoreDeduct=990,acceScoreDeduct=990,voucherUsing=980,voucherDistribute=980";
  private String seqGroupConflictZeros = "100,200";
  @BcKey(name = "spms促销渠道")
  private String spmsPromChannels = "{\n" +
      "  \"pos\": [\n" + // 全部pos规则
      "    \"WxXiaoZhi\",\n" +
      "    \"AliXiaoZhi\",\n" +
      "    \"Cpos\",\n" +
      "    \"LdcXcx\"\n" +
      "  ],\n" +
      "  \"online_pos\": [\n" + // 仅自助POS规则
      "    \"WxXiaoZhi\",\n" +
      "    \"AliXiaoZhi\"\n" +
      "  ],\n" +
      "  \"*\": [\n" + // 默认规则
      "    \"WxXiaoZhi\",\n" +
      "    \"AliXiaoZhi\",\n" +
      "    \"Cpos\",\n" +
      "    \"LdcXcx\"\n" +
      "  ]\n" +
      "}";
}
