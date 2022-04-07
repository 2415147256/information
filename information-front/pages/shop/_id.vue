<template>
  <!--房源信息-->
  <div class="main-wrap">
    <!--房源题目-->
    <div class="house-title">
      <h1 class="c_333 f20 strongbox">{{ rentHouseInfo.title }}</h1>
      <p class="house-update-info c_888 f12">
        {{ this.rentReqiure.startTime }}&nbsp;&nbsp;&nbsp;&nbsp;
      </p>
    </div>

    <!--房源基本信息-->
    <div class="house-basic-info">
      <div class="house-basic-pic fl">
        <!-- todo 没有了原先的default样式 -->
        <div id="bigImg" class="basic-top-bigpic pr ">
          <el-carousel indicator-position="outside">
            <el-carousel-item v-for="item in this.houseImags" :key="item">
              <img :src="item.imageURL">
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
      <div class="house-basic-right fr">
        <div class="house-basic-desc">
          <div class="house-desc-item fl c_333">
            <div class="house-pay-way f16">
                            <span class="c_ff552e">
                                <b class="f36 strongbox">
                                    {{ rentHouseInfo.rent }}</b>
                                 元/月</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span class="instructions">面议</span>
              <!-- 增加 start -->
              <!-- 增加 end -->
            </div>
            <ul class="f14">
              <li><span class="c_888 mr_15">房屋类型：</span><span
                class="strongbox">{{ rentHouseInfo.rooms }}&nbsp;&nbsp;{{ rentHouseInfo.area }}&nbsp;&nbsp;</span>
              </li>
              <li>
                <span class="c_888 mr_15">朝向楼层：</span>
                <span class="strongbox">{{rentHouseInfo.floor}}</span>
              </li>
              <li>
                <span class="c_888 mr_15">所在小区：</span>
                <span>
                                    <a class="c_333 ah" href="https://nc.58.com/xq/2365715/" target="_blank"
                                       onclick="clickLog('from=fcpc_detail_nc_xiaoqu0')">{{ rentHouseInfo.cellName }}                                  </a>
                                </span>
              </li>
              <li><span class="c_888 mr_15">所属区域：</span><span>
                                        {{ rentHouseInfo.region }}
                                                                    </span>
              </li>
              <li>
                <span class="c_888 mr_15">详细地址：</span>
                <span class="dz">
                  {{ rentHouseInfo.detailedAddress }}
                </span>
              </li>
            </ul>
          </div>

          <div class="house-agent-info house-broker-info fr" id="vipAgent">
            <div class="agent-info-block basic-info">
              <!--头像-->
              <div class="agent-head-portrait pr" onclick="clickLog('from=fcpc_detail_nc_touxiang')">
                <a href="//houserent.58.com/landlord/center?infoId=2419897875176456&city=nc&cityId=669&brokerId=6668296"
                   target="_blank" rel="nofollow">
                  <img :src="this.loginVo.avatar" rel="nofollow">
                </a>
              </div>
              <!-- 姓名+认证 -->
              <p class="agent-name f16 pr">
                <a class="c_000"
                   href="//houserent.58.com/landlord/center?infoId=2419897875176456&city=nc&cityId=669&brokerId=6668296"
                   target="_blank" title="点击查看ta的信用" rel="nofollow"
                   onclick="clickLog('from=fcpc_detail_nc_xingming')">
                  {{ this.loginVo.username }} </a>
                <i class="icon pho-approve" title="手机已认证"></i>
              </p>
            </div>

            <div class="agent-info-block">
              <!-- 经纪人积分部分 --->
              <div class="agent-score">
                <div class="score-item">
                  <span class="score-result">极快</span>
                  <span class="score-title">微聊回复</span>
                </div>
                <div class="score-item">
                  <span class="score-result">较好</span>
                  <span class="score-title">房源质量</span>
                </div>
                <div class="score-item">
                  <span class="score-result">极佳</span>
                  <span class="score-title">服务水平</span>
                </div>
              </div>
            </div>

            <div class="agent-info-block">
              <div class="agent-infor-wrap">
              </div>
              <!-- 个人店铺 + 营业执照-->
              <div class="agent-infor-wrap">

              </div>
              <!-- 合肥核验码，按产品要求放在个人店铺+营业执照的下面 2022.1.11 产品：杨丹 https://ee.58corp.com/w/issue/JJRZL-2815 -->
            </div>
          </div>
          <!--end-->
        </div>

        <div class="house-fraud-tip">
          <div class="house-chat-cover-container">
            <div class="house-chat-cover-wrapper"
                 onmouseover="(typeof checkQRCode==='function')&&checkQRCode()">
              <i class="house-chat-icon house-chat-icon-guard"></i>
              <i class="house-chat-icon house-chat-icon-phone-white"></i>
              <span v-if="this.user === ''" class="house-chat-txt">登录查看电话</span>
              <span v-if="this.user != ''" class="house-chat-txt">{{this.loginVo.telephone}}</span>
            </div>
            <div class="phone-qr-tooltips" style="width: 240px;">
              <span class="tooltips-arrow tooltips-arrow-border"></span>
              <span class="tooltips-arrow tooltips-arrow-bg"></span>
              <div class="for-center"></div>
              <div class="phone-qr-code"></div>
            </div>
          </div>
          <span class="house-chat-im im-chat im-offline"
                data-im=""
                onclick="clickLog('from=pc_detail_im_fc_zf_click')">
                        <i class="im-icon"></i>微聊
                        </span>
          <span class="wx-wrap">
                        <i>扫一扫，微信咨询</i>
                    </span>

          <div id="report-bar" class="report-column pr">
            <i class="jianjiao icon"></i>

            <a class="ab" href="https://about.58.com/info/deleteinfo.aspx" target="_blank" rel="nofollow"
               onclick="clickLog('from=fcpc_detail_nc_jubao_dianhuamy')">电话被冒用</a>
            <a class="ab" rel="nofollow"
               href="https://rentercenter.58.com/report/detail_app_v2?infoId=2419897875176456&terminal=0&cateId=8&plat=1"
               onclick="clickLog('from=fcpc_detail_nc_jubao_xxxujia')" target="_blank">信息虚假</a>
            <a class="ab" href="https://110.58.com/?postId=49546402805561&category=1001" rel="nofollow"
               onclick="clickLog('from=fcpc_detail_nc_jubao_baoan')" target="_blank">我要报案</a>
          </div>
        </div>
      </div>
      <br class="clear"/>
    </div>

    <!--房源描述-->
    <div class="house-detail-desc">
      <!--主要描述-->
      <div class="main-detail-info">
        <ul class="nav-detail-bar">
          <li class="on" onclick="clickLog('from=fcpc_detail_nc_fymiaoshu')">
            <a id="housedetailNav" href="javascript:;" jumpto="housedetailNav">房源详情</a></li>
        </ul>
        <div class="report-text">
          签约前切勿付
          <span class="color">订金、押金、租金</span>
          等一切费用！务必
          <span class="color">实地看房，</span>
          查验房东和房屋证件！
        </div>
        <!--房屋配置-->
        <ul class="house-disposal">
          <li class="bed"><i class="icon"></i>床</li>
          <li class="chest"><i class="icon"></i>衣柜</li>
          <li class="sofa"><i class="icon"></i>沙发</li>
          <li class="telev"><i class="icon"></i>电视</li>
          <li class="icebox"><i class="icon"></i>冰箱</li>
          <li class="washer"><i class="icon"></i>洗衣机</li>
          <li class="air-condition"><i class="icon"></i>空调</li>
          <li class="water-heater"><i class="icon"></i>热水器</li>
          <li class="broadband"><i class="icon"></i>宽带</li>
          <li class="central-heater"><i class="icon"></i>暖气</li>
          <li class="fuel-gas"><i class="icon"></i>燃气灶</li>
          <li class="balcony"><i class="icon"></i>独立阳台</li>
          <li class="toilet"><i class="icon"></i>独立卫生间</li>
          <li class="smart-lock"><i class="icon"></i>智能门锁</li>
          <li class="smoke-exhaust"><i class="icon"></i>油烟机</li>
          <li class="cooker"><i class="icon"></i>可做饭</li>
        </ul>
        <div class="house-word-introduce f16 c_555">
          <!-- 增加 start -->
          <ul class='introduce-item'>
            <li>
              <span class='a1'>房屋亮点</span>
              <span class='a2'>
                   <em>{{ this.rentReqiure.sgin }}</em>
               </span>
            </li>
            <li>
              <span class='a1'>房屋亮点</span>
              <span class='a2'>
                   <em>{{ this.rentReqiure.landlordAsked }}</em>
               </span>
            </li>
            <li>
              <span class='a1'>房源描述</span>
              <span class='a2'>房源特色：<br>{{this.rentReqiure.content}}</span>
            </li>
            <li>
              <span class="a1">pinglun</span>
            </li>
          </ul>
          <!-- 增加 end -->
        </div>
      </div>
      <!--右侧推荐及广告-->
      <div class="side-right-info fr">
        <div id="agent-other-house" class="agent-other-house">

        </div>

        <!-- <div id="superApartment">
        </div> -->
      </div>
    </div>

  </div>
</template>

<script>
import user from '@/api/user'
import rent from '@/api/rent'
import cookie from 'js-cookie'

export default {
  asyncData({params, error}) {
    return {rentId: params.id}
  },
  data() {
    return {
      rentId: '',
      rentHouseInfo: {},
      userInfo: {},
      rentReqiure : {},
      user: '',
      loginVo: {},
      id: '',
      houseImags : {}
    }
  },
  created() {
    //获取路径中token的值
    //从cookie获取用户信息
    var userStr = cookie.get('user_center');
    //把字符串转换json对象
    //后端返回的数据为字符串：
    //"{'name':'lisi','age':'18'}" ==> {'name':'lisi','age':'18'}
    if (userStr) {
      this.user = JSON.parse(userStr);
      console.log(this.user)
    }
    this.getRentInfo()

  },
  methods: {
    getRentInfo() {
      rent.getRentInfoById(this.rentId).then(
        response => {
          this.rentHouseInfo = response.data.data.rentInfo
          this.id = this.rentHouseInfo.userId
          // TODO
          user.getUserInfo(this.id).then(
            response => {
              this.loginVo = response.data.data.userInfo
            }
          )
          // TODO
          rent.getRentReqiure(this.rentId).then(
            response => {
              this.rentReqiure =  response.data.data.rentRequire
              console.log(this.rentReqiure)
            }
          )
          // TODO
          rent.getRentImags(this.rentId).then(
            response => {
              this.houseImags = response.data.data.list
            }
          )
        }
      )
    },
  }
}
</script>

<style>
@import "@/assets/css/rent.css";
@import "@/assets/css/rent/index.css";

.el-carousel__item h3 {
  color: #475669;
  font-size: 18px;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #d3dce6;
}

</style>
