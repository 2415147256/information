package com.lzh.servicerent.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.commonutils.T;
import com.lzh.servicebase.entity.User;
import com.lzh.servicerent.entity.RentHouseInfo;
import com.lzh.servicerent.entity.vo.RentHouseInfoVo;
import com.lzh.servicerent.entity.vo.RentHouseVo;
import com.lzh.servicerent.service.RentHouseInfoService;
import com.lzh.servicerent.user.UcenterClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-31
 */
@RestController
@RequestMapping("/service-rent/rent-house-info")
@CrossOrigin
public class RentHouseInfoController {

    @Autowired
    private RentHouseInfoService rentHouseInfoService;

    @Autowired
    private UcenterClient ucenterClient;


    /**
     * 查询所有的租房信息
     * @return
     */
    @GetMapping("search")
    public T searchRent(){
        List<RentHouseInfo> lists = rentHouseInfoService.list(null);
        ArrayList<RentHouseVo> rentHouseVos = new ArrayList<>();
        for(RentHouseInfo rentHouseInfo : lists){
            RentHouseVo rentHouseVo = new RentHouseVo();
            BeanUtils.copyProperties(rentHouseInfo , rentHouseVo);
            String userId = rentHouseInfo.getUserId();
            User userInfo = ucenterClient.getUserInfo(userId);
            rentHouseVo.setUserName(userInfo.getUsername());
        }
        return T.ok().data("list",rentHouseVos);
    }


    /**
     * 根据id来查询租房的基本信息
     * @param rentId
     * @return
     */
    @GetMapping("getRentInfoById/{rentId}")
    public T getInfoById(@PathVariable String rentId){
        RentHouseInfo rentInfo = rentHouseInfoService.getById(rentId);
        return T.ok().data("rentInfo" , rentInfo);
    }

    /**
     * 修改租房的信息
     * @param rent
     * @return
     */
    @PostMapping("updateRentInfo")
    public T updateRent(@RequestBody RentHouseInfo rent){
        boolean save = rentHouseInfoService.save(rent);
        if(save){
            return T.ok();
        }else{
            return T.error().message("修改失败");
        }
    }

    @PostMapping("condition/{page}/{limit}")
    public T getRentCondition(@PathVariable long limit, @PathVariable long page , @RequestBody(required = false) RentHouseInfoVo rentHouseInfoVo){

        Page<RentHouseInfo> rentPage = new Page<>(page , limit);
        String rent = rentHouseInfoVo.getRent();
        String rooms = rentHouseInfoVo.getRooms();

        LambdaQueryWrapper<RentHouseInfo> rentHouseInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(rent)){
            if(rent.contains("元以下")){
                rent = rent.replaceAll("元以下","");
                rentHouseInfoLambdaQueryWrapper.le(RentHouseInfo::getRent , Integer.parseInt(rent));
            }
            if(rent.contains("元以上")){
                rent = rent.replaceAll("元以上","");
                rentHouseInfoLambdaQueryWrapper.ge(RentHouseInfo::getRent , Integer.parseInt(rent));
            }
            if(rent.contains("-")){
                rent = rent.replaceAll("元" ,"");
                String[] split = rent.split("-");
                rentHouseInfoLambdaQueryWrapper.ge(RentHouseInfo::getRent , Integer.parseInt(split[0]));
                rentHouseInfoLambdaQueryWrapper.le(RentHouseInfo::getRent , Integer.parseInt(split[1]));
            }
        }

        if(!StringUtils.isEmpty(rooms)){
            if(rooms.contains("以上")){
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms,"一室");
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms,"二室");
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms,"三室");
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms,"四室");
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms,"不限");
            }else if(rooms.contains("不限")){
                rentHouseInfoLambdaQueryWrapper.notLike(RentHouseInfo::getRooms , rooms);
            }else {
                rentHouseInfoLambdaQueryWrapper.eq(RentHouseInfo::getRooms , rooms);
            }
        }
        rentHouseInfoService.page(rentPage , rentHouseInfoLambdaQueryWrapper);
        List<RentHouseInfo> records = rentPage.getRecords();
        ArrayList<RentHouseVo> rentHouseVos = new ArrayList<>();

        for(RentHouseInfo rentHouseInfo : records){
            RentHouseVo rentHouseVo = new RentHouseVo();
            BeanUtils.copyProperties(rentHouseInfo , rentHouseVo);
            String userId = rentHouseInfo.getUserId();
            User userInfo = ucenterClient.getUserInfo(userId);
            if(userInfo.getIsDelete().equals(Integer.valueOf(0))){
                rentHouseVo.setUserName(userInfo.getUsername());
                rentHouseVos.add(rentHouseVo);
            }
        }

        long total = rentPage.getTotal();
        return T.ok().data("records" , rentHouseVos).data("total" , total);
    }


    /**
     * 查询热门的出租屋信息
     * @return
     */
    @GetMapping("getHotHouseInfo")
    public T getHotHouseInfo(){
        LambdaQueryWrapper<RentHouseInfo> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.orderByDesc(RentHouseInfo::getHouseId);
        LambdaQueryWrapper.last("limit  4");
        List<RentHouseInfo> shopProducts= rentHouseInfoService.list(LambdaQueryWrapper);
        return T.ok().data("shopProducts",shopProducts);
    }
}

