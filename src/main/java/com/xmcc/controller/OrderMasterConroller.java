package com.xmcc.controller;

import com.google.common.collect.Maps;
import com.xmcc.Service.OrderMasterService;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/order")
@Api(description = "订单相关接口")
public class OrderMasterConroller {

    @Autowired
    private OrderMasterService orderMasterService;

    @PostMapping("/create")
    @ApiOperation(value = "创建订单接口",httpMethod = "POST",response = ResultResponse.class)
    //@Valid配合刚才在DTO上的JSR303注解完成校验
    //注意：JSR303的注解默认是在Contorller层进行校验
    //如果想在service层进行校验 需要使用javax.validation.Validator  也就是上个项目用到的工具
    public ResultResponse create(@Valid @ApiParam(name = "订单对象",value = "传入json格式",required = true)
                                             OrderMasterDto orderMasterDto, BindingResult bindingResult){
        Map<String,String> map = Maps.newHashMap();
        //判断是否有参数校验问题
        if (bindingResult.hasErrors()){
            List<String> errList = bindingResult.getFieldErrors().stream().map(err ->
                    err.getDefaultMessage()).collect(Collectors.toList());
            map.put("参数校验错误", JsonUtil.object2string(errList));
            //将参数校验的错误信息返回给前台
            return ResultResponse.fail(map);
        }
        return orderMasterService.insertOrder(orderMasterDto);
    }

}
