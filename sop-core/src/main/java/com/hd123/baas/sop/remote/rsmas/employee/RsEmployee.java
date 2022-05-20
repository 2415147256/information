package com.hd123.baas.sop.remote.rsmas.employee;

import com.hd123.baas.sop.service.api.basedata.employee.Employee;
import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RsEmployee extends RsMasEntity {
  private static final long serialVersionUID = 768419595134960010L;

  /** 级联组织 */
  public static final String PART_ORGANIZATION = "organization";
  /** 级联上级 */
  public static final String PART_UPPER = "upper";
  /** 级联部门 */
  public static final String PART_DEPARTMENT = "departments";
  /** 级联岗位 */
  public static final String PART_POSITION = "positions";

  @ApiModelProperty(value = "组织类型", required = true)
  private String orgType;
  @ApiModelProperty(value = "组织ID", required = true)
  private String orgId;
  @ApiModelProperty(value = "组织", required = true)
  private RsOrg organization;
  @ApiModelProperty(value = "用户ID", required = true)
  private String userId;
  @ApiModelProperty(value = "是否已启用")
  private Boolean enabled = true;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "手机")
  private String mobile;
  @ApiModelProperty(value = "邮箱")
  private String email;
  @ApiModelProperty(value = "入职时间")
  private Date hireDate;
  @ApiModelProperty(value = "离职时间")
  private Date departureDate;
  @ApiModelProperty(value = "职位ID")
  private String jobId;
  @ApiModelProperty(value = "职位名称")
  private String jobName;
  @ApiModelProperty(value = "上级员工ID")
  private String upperId;
  @ApiModelProperty(value = "上级员工")
  private Employee upper;
  @ApiModelProperty(value = "员工部门")
  private List<RsEmployeeDepartment> departments = new ArrayList<RsEmployeeDepartment>();
  @ApiModelProperty(value = "员工岗位")
  private List<RsEmployeePosition> positions = new ArrayList<RsEmployeePosition>();
}
