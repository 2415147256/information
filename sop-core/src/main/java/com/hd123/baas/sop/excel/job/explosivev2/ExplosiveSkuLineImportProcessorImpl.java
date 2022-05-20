package com.hd123.baas.sop.excel.job.explosivev2;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hd123.baas.sop.excel.common.ImportExcelListener;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.excel.job.explosive.ExplosiveSkuImportProcessor;
import com.hd123.baas.sop.utils.ImportResult;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Component("ExplosiveSkuLineImportProcessorImpl")
public class ExplosiveSkuLineImportProcessorImpl implements ExplosiveSkuImportProcessor {

  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private SkuService skuService;


  @Override
  public ImportResult<List<Sku>> doImport(String tenantId, String orgId, MultipartFile file, Params param) {
    ImportResult result = new ImportResult();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImportProcessData processData = new ImportProcessData();
      BeanUtils.copyProperties(param, processData);
      EasyExcel.read(file.getInputStream(), ExcelInputRow.class, newExcelListener(tenantId, orgId, processData)).sheet().headRowNumber(3).doRead();
      result.setIgnoreCount(processData.getIgnoreCount());
      result.setFailCount(processData.getFailedCount());
      result.setSuccessCount(processData.getSkuList().size());
      result.setSuccessList(processData.getSkuList());

      if (!processData.getErrorRowData().isEmpty()) {
        EasyExcel.write(outputStream, ErrorOutputRow.class)
            .useDefaultStyle(false)
            .sheet().doWrite(processData.getErrorRowData());
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
          String path = "sop/tempOf1Day/" + tenantId + "/importResult/sku/" + UUID.randomUUID().toString() + "/" + "促销商品导入失败明细.xlsx";
          bucket.put(path, inputStream);
          result.setBackUrl(bucket.getUrl(path, Bucket.CONTENT_TYPE_OF_WILDCARD));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public ImportExcelListener newExcelListener(String tenant, String orgId, ImportProcessData processData) {
    return new ImportExcelListener<ExcelInputRow>() {
      @Override
      public void processData(List<ExcelInputRow> excelRows) throws BaasException {
        List<Sku> skus = querySkus(tenant, orgId, excelRows);

        Map<String, Sku> skuMap = new HashMap<>();
        for (Sku sku : skus) {
          skuMap.put(MessageFormat.format("{0},{1}", sku.getCode().toUpperCase(), sku.getQpc().stripTrailingZeros().toPlainString()), sku);
        }

        List<ErrorOutputRow> errorOutputRows = new ArrayList<>();
        int failedCount = 0;
        int ignoreCount = 0;
        List<ExplosiveV2Line> result = new ArrayList<>();
        for (ExcelInputRow row : excelRows) {
          if (StringUtils.isBlank(row.getCode())) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "商品代码不能为空"));
            failedCount++;
            continue;
          }
          BigDecimal qpc = new BigDecimal("1");
          Sku sku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), qpc.stripTrailingZeros().toPlainString()));
          if (sku == null) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "指定规格为1*1的商品不存在或不可用"));
            failedCount++;
            continue;
          }
          BigDecimal skuQpc;
          try {
            skuQpc = new BigDecimal(row.getSkuQpc());
          } catch (Exception e) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "订货规格解析失败，注意1*1请填写为1"));
            failedCount++;
            continue;
          }
          Sku aclSku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), skuQpc.stripTrailingZeros().toPlainString()));
          if (aclSku == null) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "指定订货的商品不存在或不可用"));
            failedCount++;
            continue;
          }
          String skuKey = MessageFormat.format("{0},{1}", sku.getGoodsGid(), sku.getQpc().stripTrailingZeros().toPlainString());
          if (processData.getSelectedList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "忽略", "指定的商品已选中"));
            ignoreCount++;
          } else if (processData.getExcludeList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "指定的商品此处不被允许"));
            failedCount++;
          } else if (processData.getSkuKeys().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "忽略", "指定的商品重复导入"));
            ignoreCount++;
          } else {
            processData.getSkuKeys().add(skuKey);
            ExplosiveV2Line item = new ExplosiveV2Line();
            item.setSkuGid(sku.getGoodsGid());
            item.setSkuId(sku.getId());
            item.setSkuCode(sku.getCode());
            item.setSkuName(sku.getName());
            item.setSkuUnit(aclSku.getUnit());
            item.setSkuQpc(skuQpc);
            BigDecimal inPrice;
            BigDecimal minQty;
            BigDecimal limitQty;
            try {
              inPrice = new BigDecimal(row.getInPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价解析失败"));
              failedCount++;
              continue;
            }
            if (inPrice.compareTo(BigDecimal.ZERO) <= 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价必须大于0"));
              failedCount++;
              continue;
            } else if (inPrice.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }

            try {
              minQty = new BigDecimal(row.getMinQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货量解析失败"));
              failedCount++;
              continue;
            }
            if (minQty.compareTo(BigDecimal.ZERO) < 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货量必须大于等于0"));
              failedCount++;
              continue;
            } else if (minQty.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货量超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }
            if (row.getIsLimit() == null) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "爆品是否限量必填"));
              failedCount++;
              continue;
            }
            if (!"是".equals(row.getIsLimit()) && !"否".equals(row.getIsLimit())) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "爆品是否限量必须为 是  或  否"));
              failedCount++;
              continue;
            }
            try {
              if ("是".equals(row.getIsLimit())) {
                limitQty = new BigDecimal(row.getLimitQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (limitQty.compareTo(BigDecimal.ZERO) <= 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货量必须大于0"));
                  failedCount++;
                  continue;
                } else if (limitQty.compareTo(minQty) < 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货量不能小于每门店最小订货量"));
                  failedCount++;
                  continue;
                } else if (limitQty.compareTo(new BigDecimal("99999999.99")) > 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货量超过最大值(99999999.99)"));
                  failedCount++;
                  continue;
                }
              } else {
                limitQty = BigDecimal.ZERO;
              }
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货量解析失败"));
              failedCount++;
              continue;
            }
            item.setInPrice(inPrice);
            item.setMinQty(minQty);
            item.setLimitQty(limitQty);
            item.setRemark(row.getRemark());
            result.add(item);
          }
        }

        processData.getSkuList().addAll(result);
        processData.getErrorRowData().addAll(errorOutputRows);
        processData.setFailedCount(processData.getFailedCount() + failedCount);
        processData.setIgnoreCount(processData.getIgnoreCount() + ignoreCount);
        excelRows.clear();
      }

      /**
       * 查询sku
       */
      private List<Sku> querySkus(String tenant, String orgId, List<ExcelInputRow> excelRows) throws BaasException {
        List<String> codeIn = excelRows.stream().map(ExcelInputRow::getCode).collect(Collectors.toList());
        SkuFilter filter = new SkuFilter();
        filter.setCodeIn(codeIn);
        filter.setOrgIdEq(orgId);
        filter.setPage(0);
        filter.setPageSize(0);
        filter.setFetchParts("category");
        return skuService.query(tenant, filter).getRecords();
      }
    };
  }

  @Data
  public static class ImportProcessData {
    private List skuList = new ArrayList<>();
    private List<String> skuKeys = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();
    private List<String> excludeList = new ArrayList<>();
    private int failedCount;
    private int ignoreCount;
    private List<ErrorOutputRow> errorRowData = new ArrayList<>();
    private boolean prmGoods;
  }

  /**
   * @author shenmin
   **/
  @Data
  public static class ExcelInputRow {
    @ExcelProperty("商品代码（必填）")
    public String code;
    @ExcelProperty("商品规格（必填）")
    public String skuQpc;
    @ExcelProperty("单位（必填）")
    public String skuUnit;
    @ExcelProperty("活动订货价（元）（必填）")
    public String inPrice;
    @ExcelProperty("是否限量（必填）")
    public String isLimit;
    @ExcelProperty("活动总数量报名上限")
    public String limitQty;
    @ExcelProperty("单个门店最低报名量（必填）")
    public String minQty;
    @ExcelProperty("备注")
    public String remark;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorOutputRow {
    @ExcelProperty("商品代码（必填）")
    public String code;
    @ExcelProperty("商品规格（必填）")
    public String skuQpc;
    @ExcelProperty("单位（必填）")
    public String skuUnit;
    @ExcelProperty("活动订货价（元）（必填）")
    public String inPrice;
    @ExcelProperty("是否限量（必填）")
    public String isLimit;
    @ExcelProperty("活动总数量报名上限")
    public String limitQty;
    @ExcelProperty("单个门店最低报名量（必填）")
    public String minQty;
    @ExcelProperty("备注")
    public String remark;
    @ExcelProperty("状态")
    private String state;
    @ExcelProperty("说明")
    private String message;

    public ErrorOutputRow(ExcelInputRow source, String state, String message) {
      BeanUtils.copyProperties(source, this);
      this.state = state;
      this.message = message;
    }
  }
}
