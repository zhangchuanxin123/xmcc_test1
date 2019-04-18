package com.xmcc.Service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.Service.OrderDetailService;
import com.xmcc.Service.OrderMasterService;
import com.xmcc.Service.ProductInfoService;
import com.xmcc.common.*;
import com.xmcc.dto.OrderDetailDto;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderDetail;
import com.xmcc.entity.OrderMaster;
import com.xmcc.entity.ProductInfo;
import com.xmcc.exception.CustomException;
import com.xmcc.repository.OrderMasterRepository;
import com.xmcc.util.BigDecimalUtil;
import com.xmcc.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService{

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Override
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {

        /***
         * @Valid: 用于配合JSR303注解验证参数，只能在controller层进行验证
         * validetor:在service层验证
         */
        //取出订单项
        List<OrderDetailDto> items = orderMasterDto.getItems();

        //创建集合来存储Orderdetail
        List<OrderDetail> orderDetailList = Lists.newArrayList();

        //初始化订单的总金额
        BigDecimal totalPrice = new BigDecimal("0");

        //遍历订单项，获取商品详情
        for(OrderDetailDto dto:items){
            //查询商品
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(dto.getProductId());

            //判断ResultResponse的code即可判断查询是否成功
            if(resultResponse.getCode() == ResultEnums.FAIL.getCode()){
                throw  new CustomException(resultResponse.getMsg());
            }

            //得到商品
            ProductInfo productInfo = resultResponse.getData();

            //比较库存
            if(productInfo.getProductStock() < dto.getProductQuantity()){
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //将前台传入的订单项DTO与数据库查询到的 商品数据组装成OrderDetail 放入集合中  @builder
            OrderDetail orderDetail = OrderDetail.builder().detailId(IDUtils.createIdbyUUID()).productIcon(productInfo.getProductIcon())
                    .productId(dto.getProductId()).productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice()).productQuantity(dto.getProductQuantity())
                    .build();
            orderDetailList.add(orderDetail);

            //减少库存
            productInfo.setProductStock(productInfo.getProductStock()-dto.getProductQuantity());
            //更新商品数据
            productInfoService.updateProduct(productInfo);

            //计算价格
            totalPrice = BigDecimalUtil.add(totalPrice,BigDecimalUtil.multi(productInfo.getProductPrice(),dto.getProductQuantity()));
        }

        //生成订单id
        String orderId = IDUtils.createIdbyUUID();
        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder().buyerAddress(orderMasterDto.getAddress()).buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid()).orderStatus(OrderEnums.NEW.getCode())
                .payStatus(PayEnums.WAIT.getCode()).buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId).orderAmount(totalPrice).build();

        //将订单id设置到顶单项中
        List<OrderDetail> orderDetails = orderDetailList.stream().map(orderDetail -> {
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());

        //批量插入订单项
        orderDetailService.batchInsert(orderDetails);
        //插入订单
        orderMasterRepository.save(orderMaster);

        HashMap<String,String> map = Maps.newHashMap();
        map.put("orderId", orderId);
        return ResultResponse.success(map);
    }
}
