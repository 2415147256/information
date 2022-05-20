package com.hd123.baas.sop.service.impl.approval;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.WorkWxConfig;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.configuration.oss.OssSignature;
import com.hd123.baas.sop.remote.uas.BUser;
import com.hd123.baas.sop.remote.uas.UasClient;
import com.hd123.baas.sop.remote.workwx.WorkWxClient;
import com.hd123.baas.sop.remote.workwx.apply.ApplyData;
import com.hd123.baas.sop.remote.workwx.apply.ApplyDataContent;
import com.hd123.baas.sop.remote.workwx.apply.TableChildren;
import com.hd123.baas.sop.remote.workwx.apply.TableChildrenDetail;
import com.hd123.baas.sop.remote.workwx.apply.TemplateContent;
import com.hd123.baas.sop.remote.workwx.apply.TemplateContentControl;
import com.hd123.baas.sop.remote.workwx.apply.TemplateControlConfig;
import com.hd123.baas.sop.remote.workwx.apply.TemplateControlProperty;
import com.hd123.baas.sop.remote.workwx.apply.TemplateTableChildren;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.DateFormControl;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.DateFormControlData;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.FileData;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.FileFormControl;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.TableFormControl;
import com.hd123.baas.sop.remote.workwx.apply.formcontrol.TextFormControl;
import com.hd123.baas.sop.remote.workwx.request.MobileWorkWxReq;
import com.hd123.baas.sop.remote.workwx.request.TemplateWorkWxReq;
import com.hd123.baas.sop.remote.workwx.request.WorkWxApprovalApply;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyTemplateResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxUploadResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxUserResponse;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackImage;
import com.hd123.baas.sop.service.api.feedback.FeedbackType;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author W.J.H.7
 */
@Service
@Slf4j
public class FeedbackApprovalByWorkWxService extends AApprovalByWorkWxService<Feedback> {
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private UasClient uasClient;
  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private OssSignature ossSignature;

  public static final String TEMPLATE_TYPE = "feedback";
  

  @Override
  protected WorkWxApprovalApply build(Feedback source) throws Exception {
    String tenant = source.getTenant();
    // 获取配置
    WorkWxConfig feedbackConfig = baasConfigClient.getConfig(tenant, WorkWxConfig.class);
    String templateId = getFeedBackTemplateId(feedbackConfig.getApprovalTemplateJson());
    if (StringUtil.isNullOrBlank(templateId)) {
      log.warn("请配置企业微信质量反馈审批模板ID");
      return null;
    }

    WorkWxClient wxClient = feignClientMgr.getClient(tenant, null, WorkWxClient.class);
    WorkApplyTemplateResponse templateDetailRsp = wxClient.getTemplateDetail(tenant,
        new TemplateWorkWxReq(templateId));
    if (!templateDetailRsp.success()) {
      throw new BaasException("获取企业微信审批模板错误{},模板ID:{}", templateDetailRsp.getErrMsg(),
          templateId);
    }
    TemplateContent content = templateDetailRsp.getContent();
    if (content == null) {
      throw new BaasException("找不到审批模板{}", templateId);
    }

    WorkWxApprovalApply applyReq = new WorkWxApprovalApply();

    if (feedbackConfig.getApprovalUserIdStrategy().equals(WorkWxConfig.FIX)) {
      // 固定
      if (StringUtil.isNullOrBlank(feedbackConfig.getApprovalUserId())) {
        log.warn("审批创建人策略为FIX,但是没有配置userId");
        return null;
      }
      applyReq.setCreatorUserid(feedbackConfig.getApprovalUserId());
    } else if (feedbackConfig.getApprovalUserIdStrategy().equals(WorkWxConfig.EXCHANGE)) {

      String userId = source.getSubmitterId();
      String mobile = getMobile(tenant, source.getOrgId(), userId);
      if (StringUtil.isNullOrBlank(mobile)) {
        throw new BaasException("用户手机号不存在");
      }
      MobileWorkWxReq mobileWorkWxReq = new MobileWorkWxReq();
      mobileWorkWxReq.setMobile(mobile);
      WorkWxUserResponse userByMobileRsp = wxClient.getUserByMobile(tenant, mobileWorkWxReq);
      if (!userByMobileRsp.success() || StringUtil.isNullOrBlank(userByMobileRsp.getUserId())) {
        throw new BaasException("用户手机号{}找不到对应的企业微信用户信息", mobile);
      }
      applyReq.setCreatorUserid(userByMobileRsp.getUserId());
    }

    applyReq.setTemplateId(templateId);

    ApplyData applyData = new ApplyData();

    List<FileData> medias = convertToMedias(tenant,source.getImages(), wxClient);

    for (TemplateContentControl control : content.getControls()) {
      TemplateControlProperty property = control.getProperty();
      String itemName = property.getTitle().get(0).getText();
      TemplateControlConfig config = control.getConfig();
      ApplyDataContent applyDataContent = new ApplyDataContent();

      applyDataContent.setControl(property.getControl());
      applyDataContent.setId(property.getId());
      if (itemName.contains("门店名称")) {
        TextFormControl textFormControl = new TextFormControl();
        textFormControl.setText(source.getShopName() + "(" + source.getShop() + ")");
        applyDataContent.setValue(textFormControl);
      } else if (itemName.contains("业务员")) {
        TextFormControl textFormControl = new TextFormControl();
        // TODO
        textFormControl.setText(source.getSubmitterName());
        applyDataContent.setValue(textFormControl);
      } else if (itemName.contains("到货时间")) {
        // TODO
        DateFormControlData dateFormControlData = new DateFormControlData();
        dateFormControlData.setType(config.getDate().getType());
        dateFormControlData.setTimestamp(getTimestamp(source.getDeliveryTime()));

        DateFormControl dateFormControl = new DateFormControl();
        dateFormControl.setDate(dateFormControlData);
        applyDataContent.setValue(dateFormControl);

      } else if (itemName.contains("退货时间")) {
        // TODO
        DateFormControlData dateFormControlData = new DateFormControlData();
        dateFormControlData.setType(config.getDate().getType());
        dateFormControlData.setTimestamp(getTimestamp(new Date()));

        DateFormControl dateFormControl = new DateFormControl();
        dateFormControl.setDate(dateFormControlData);
        applyDataContent.setValue(dateFormControl);

      } else if (property.getControl().equals("Table")) {
        // 表格
        TableFormControl tableFormControl = new TableFormControl();
        List<TableChildren> childrenList = new ArrayList<>();
        if (config != null && config.getTable() != null) {
          TableChildren tableChildren = new TableChildren();
          for (TemplateTableChildren child : config.getTable().getChildren()) {
            TemplateControlProperty childProperty = child.getProperty();
            String tableItemName = childProperty.getTitle().get(0).getText();
            TableChildrenDetail tableChildrenDetail = new TableChildrenDetail();
            tableChildrenDetail.setControl(childProperty.getControl());
            tableChildrenDetail.setId(childProperty.getId());
            tableChildrenDetail.setTitle(childProperty.getTitle());

            if (tableItemName.equals("问题货品")) {
              TextFormControl textFormControl = new TextFormControl();
              textFormControl.setText(source.getGdName() + "_" + source.getGdCode() + "_" + source.getQpc());
              tableChildrenDetail.setValue(textFormControl);
            } else if (tableItemName.equals("问题货品照片")) {

              if (CollectionUtils.isNotEmpty(medias)) {
                FileFormControl fileFormControl = new FileFormControl();
                fileFormControl.setFiles(medias);
                tableChildrenDetail.setValue(fileFormControl);
              }
            } else if (tableItemName.equals("当次到货总数")) {
              TextFormControl textFormControl = new TextFormControl();
              textFormControl.setText(source.getReceiptQty().toString());
              tableChildrenDetail.setValue(textFormControl);
            }else if (tableItemName.equals("当次问题货品数量")) {
              TextFormControl textFormControl = new TextFormControl();
              textFormControl.setText(source.getQty().toString());
              tableChildrenDetail.setValue(textFormControl);
            }else if (tableItemName.equals("申请原因说明")) {
              TextFormControl textFormControl = new TextFormControl();
              textFormControl.setText(source.getApplyReason() + ";" + source.getApplyNote());
              tableChildrenDetail.setValue(textFormControl);
            }
            tableChildren.getList().add(tableChildrenDetail);
          }

          childrenList.add(tableChildren);
        }

        tableFormControl.setChildren(childrenList);

        applyDataContent.setValue(tableFormControl);
      }
      applyData.getContents().add(applyDataContent);
    }
    applyReq.setApplyData(applyData);
    applyReq.setSummaryList(new ArrayList<>());


    return applyReq;
  }

  private String getTimestamp(Date date) {
    return String.valueOf(date.getTime()/1000);
  }

  /**
   * 将OSS的url转化成企业微信的临时素材ID
   *
   * @param tenant
   * @param images
   * @param wxClient
   * @return
   */
  private List<FileData> convertToMedias(String tenant, List<FeedbackImage> images, WorkWxClient wxClient) {
    List<FileData> fileDataList = new ArrayList<>();
    if (CollectionUtils.isEmpty(images)) {
      return fileDataList;

    }

    List<String> imageUrls = images.stream().map(FeedbackImage::getUrl)
        .filter(i -> !StringUtil.isNullOrBlank(i)).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(imageUrls)) {
      return fileDataList;
    }

    try {
      for (String imageUrl : imageUrls) {
        String key = getImageKey(imageUrl);
        InputStream inputStream = bucket.get(URLDecoder.decode(key, "utf-8"));
        if (inputStream == null) {
          continue;
        }
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(getFileItem(inputStream,key));

        WorkWxUploadResponse wxUploadResponse = wxClient.upload(tenant, WorkWxClient.IMAGE, multipartFile);
        if (wxUploadResponse.success() && !StringUtil.isNullOrBlank(wxUploadResponse.getMediaId())) {
          FileData fileData = new FileData();
          fileData.setFileId(wxUploadResponse.getMediaId());
          fileDataList.add(fileData);
        }

      }
    } catch (Exception e) {
      log.warn("oss图片转化企业微信临时素材错误{0}",e);
    }
    return fileDataList;
  }

  private DiskFileItem getFileItem(InputStream inputStream, String key) {

    DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
        MediaType.MULTIPART_FORM_DATA.getType(), false, getNameByKey(key));

    try {
      IOUtils.copy(inputStream, fileItem.getOutputStream());
    } catch (Exception e) {
      log.error("拷贝素材异常:{0}", e);
    }

    return fileItem;
  }

  private String getNameByKey(String key) {
    String name = IdGenUtils.buildIidAsString();
    try {
      name = URLDecoder.decode(key.substring(key.lastIndexOf("/") + 1), "utf-8");
    } catch (UnsupportedEncodingException e) {
      log.warn("从图片URL中获取名称发生异常{}", e.getMessage());
    }
    // key=sop/files/2022/0424/437268226657746944_11QQ%E6%88%AA%E5%9B%BE20220424002227.png
    return name;
  }



  private String getImageKey(String imageUrl) throws Exception {

    Map<String, Object> signature = ossSignature.signature("-");
    String[] splits = imageUrl.split(String.valueOf(signature.get("host")) + "/");
    if (splits.length != 2) {
      return null;
    }

    return splits[1];
  }

  private String getFeedBackTemplateId(String approvalTemplateJson) {
    if (!StringUtil.isNullOrBlank(approvalTemplateJson)) {
      List<TemplateConfigDef> templateConfigList = JsonUtil.jsonToList(approvalTemplateJson, TemplateConfigDef.class);
      TemplateConfigDef feedBackTemplate = templateConfigList.stream().filter(i -> i.getApprovalType().equals(TEMPLATE_TYPE)).findFirst().orElse(null);
      return feedBackTemplate == null ? null : feedBackTemplate.getApprovalTemplateId();

    }
    return null;
    
  }

  private String getValueByType(FeedbackType type) {
    if (type == FeedbackType.excepted) {
      return "异常质量反馈";
    }
    return "收货质量反馈";
  }


  private String getMobile(String tenant, String orgId, String loginId) throws BaasException {

    Assert.hasText(tenant);
    Assert.hasText(loginId);

    QueryRequest queryRequest = new QueryRequest();
    FilterParam filterParam = new FilterParam();
    filterParam.setProperty("loginId:in");
    filterParam.setValue(Collections.singleton(loginId));
    queryRequest.getFilters().add(filterParam);
    List<BUser> users = uasClient.queryByLoginIds(tenant, queryRequest).getData();

    if (CollectionUtils.isNotEmpty(users)) {
      return users.get(0).getMobile();
    }
    return null;
  }

}
