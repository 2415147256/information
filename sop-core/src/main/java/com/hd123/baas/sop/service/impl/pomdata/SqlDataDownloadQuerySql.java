package com.hd123.baas.sop.service.impl.pomdata;

public class SqlDataDownloadQuerySql {
  private static final String PREFIX = "sqlData.download.querySql.";

  public static final String PomChangeToScore = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       remark,\n" +
      "       i.document\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       exists(select 1\n" +
      "              from h5cpromotionbilljoin j\n" +
      "              where b.uuid = j.billUuid\n" +
      "                and j.joinOrgUuid = '{joinOrgUuid}'))\n" +
      "  and b.type = 'coinsToScore'";

  public static final String PomChangeToScoreIndex = "select i.uuid                      as itemUuid,\n" +
      "       CONCAT(b.execSeqNumber, '') as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'coinsToScore'";

  public static final String PomScoreDeduct = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish               as ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity,\n" +
      "       b.type                  as billtype,\n" +
      "       i.limitType2,\n" +
      "       i.limitMode2,\n" +
      "       i.limitQuantity2\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type in ('baseScoreDeduct', 'acceScoreDeduct', 'scoreDeduct')";

  public static final String PomScoreDeductIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type in ('baseScoreDeduct', 'acceScoreDeduct', 'scoreDeduct')";

  public static final String PomPresent = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.execSeqNumber,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish               as ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity,\n" +
      "       b.type                  as billtype\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'memberGiftware'";

  public static final String PomPresentIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'memberGiftware'";

  public static final String PomSingleForm = "select s.uuid,\n" +
      "       s.type,\n" +
      "       s.entityUuid,\n" +
      "       s.entityCode,\n" +
      "       s.qpc,\n" +
      "       s.stepType,\n" +
      "       s.stepOperator,\n" +
      "       s.stepValue,\n" +
      "       s.form,\n" +
      "       s.promValue,\n" +
      "       s.itemUuid,\n" +
      "       s.fstart  as fstart,\n" +
      "       s.ffinish as ffinish,\n" +
      "       b.auditTime\n" +
      "from h5csingleproduct s,\n" +
      "     h5cpromotionitem t,\n" +
      "     h5cpromotionbill b,\n" +
      "     h5cpromotionbilljoin j\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and b.channel != 'online_pos'\n" +
      "  and s.itemuuid = t.uuid\n" +
      "  and t.billuuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and j.billuuid = b.uuid\n" +
      "  and j.joinOrgUuid = '{joinOrgUuid}'\n" +
      "  and b.type in ('retail', 'member')";

  public static final String PomSingleIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and b.channel != 'online_pos'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'retail'\n" +
      "  and b.opportunity = 'enter'";

  public static final String PomItemIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.channel != 'online_pos'\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type in ('retail', 'member')";

  public static final String PomItemGroup = "select s.type, s.groupNumber, s.execSeqOrder, s.inpolicy, s.outpolicy, s.giftCondition\n" +
      "from H5CExecSeqGroup s\n" +
      "where s.tenantId = '{tenant}'";

  public static final String PomItem = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.execSeqNumber,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish               as ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity,\n" +
      "       i.limitType2,\n" +
      "       i.limitMode2,\n" +
      "       i.limitQuantity2,\n" +
      "       i.limitFavAmount,\n" +
      "       b.field11 as billCls,\n" +
      "       b.label\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.channel != 'online_pos'\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type in ('retail', 'member')";

  public static final String PomSingle = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.execSeqNumber,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish               as ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity,\n" +
      "       i.limitType2,\n" +
      "       i.limitMode2,\n" +
      "       i.limitQuantity2,\n" +
      "       i.limitFavAmount,\n" +
      "       b.label\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.channel != 'online_pos'\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'retail'\n" +
      "  and b.opportunity = 'enter'";

  public static final String PomScore = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'score'";

  public static final String PomScoreIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'score'";

  public static final String PomSaleCard = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.execSeqNumber,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish               as ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'tradeSaleCard'";

  public static final String PomSaleCardIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'tradeSaleCard'";

  public static final String PomSendVoucher = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'voucherDistribute'";

  public static final String PomSendVoucherIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'voucherDistribute'";

  public static final String PomUseVoucher = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish                  ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       activityDesc            as remark,\n" +
      "       i.document\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'voucherUsing'";

  public static final String PomUseVoucherIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'voucherUsing'";

  public static final String PomCustomerSaleCard = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.execSeqNumber,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish                  ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       remark,\n" +
      "       i.document,\n" +
      "       i.limitType,\n" +
      "       i.limitMode,\n" +
      "       i.limitQuantity\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'customerSaleCard'";

  public static final String PomCustomerSaleCardIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'customerSaleCard'";

  public static final String PomReissueCard = "select i.uuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       billNumber,\n" +
      "       auditTime,\n" +
      "       starterOrgUuid,\n" +
      "       b.fstart                as fstart,\n" +
      "       b.ffinish                  ffinish,\n" +
      "       conflictGroup,\n" +
      "       conflictMutexName,\n" +
      "       b.conflictSeqGroups,\n" +
      "       remark,\n" +
      "       i.document\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and b.ffinish > now()\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'tradeReissueCard'";

  public static final String PomReissueCardIndex = "select i.uuid                  as itemUuid,\n" +
      "       CONCAT(b.execSeqGroup) as itemGroupUuid,\n" +
      "       b.fstart,\n" +
      "       b.ffinish,\n" +
      "       i.productCondDoc,\n" +
      "       i.productExpression,\n" +
      "       i.productUuid,\n" +
      "       i.qpc\n" +
      "from H5CPromotionItem i,\n" +
      "     H5CPromotionBill b\n" +
      "where b.tenantId = '{tenant}'\n" +
      "  and i.billUuid = b.uuid\n" +
      "  and b.state = 'audited'\n" +
      "  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\n" +
      "  and (b.allorg = 1 or\n" +
      "       (exists(select 1\n" +
      "               from h5cpromotionbilljoin j\n" +
      "               where b.uuid = j.billUuid\n" +
      "                 and j.joinOrgUuid = '{joinOrgUuid}')))\n" +
      "  and b.type = 'tradeReissueCard'";

}
