package com.xmcc.Service.Impl;

import com.xmcc.Service.ProductInfoService;
import com.xmcc.common.ProductEnums;
import com.xmcc.common.ResultEnums;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.ProductCategoryDto;
import com.xmcc.dto.ProductInfoDto;
import com.xmcc.entity.ProductCategory;
import com.xmcc.entity.ProductInfo;
import com.xmcc.repository.ProductCategoryRepository;
import com.xmcc.repository.ProductInfoRepoisitory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductInfoRepoisitory productInfoRepoisitory;

    @Override
    public ResultResponse queryList() {
        //查询所有分类
        List<ProductCategory> all = productCategoryRepository.findAll();

        List<ProductCategoryDto> productCategoryDtoList
                = all.stream().map(productCategory -> ProductCategoryDto.build(productCategory)).collect(Collectors.toList());


        if(CollectionUtils.isEmpty(all)){
            return ResultResponse.fail();
        }
        //获取所有类目编号集合
        List<Integer> typeList
                = productCategoryDtoList.stream().map(productCategoryDto -> productCategoryDto.getCategoryType()).collect(Collectors.toList());
        //根据typeList 查询商品列表
        List<ProductInfo> productInfoList
                = productInfoRepoisitory.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(), typeList);

        //对目标集合进行遍历 取出每个商品的类目编号 设置到对应的目录中
        //将productInfo设置到foods中
        //过滤：不同的type 进行不同的封装
        //将productInfo 转成Dto
        List<ProductCategoryDto> productCategoryDtos = productCategoryDtoList.parallelStream().map(productCategoryDto -> {
            productCategoryDto.setProductInfoDtoList(productInfoList.stream()
                    .filter(productInfo -> productInfo.getCategoryType() == productCategoryDto.getCategoryType())
                    .map(productInfo -> ProductInfoDto.build(productInfo)).collect(Collectors.toList()));
            return productCategoryDto;
        }).collect(Collectors.toList());
        return ResultResponse.success(productCategoryDtos);
    }

    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
        //参数异常
        if(StringUtils.isBlank(productId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        //查找商品,用操作类包装
        Optional<ProductInfo> byId = productInfoRepoisitory.findById(productId);

        //调用操作类方法，检查商品是否存在
        if(!byId.isPresent()){
            return ResultResponse.fail(ResultEnums.NOT_EXITS.getMsg());
        }

        //用操作类获取商品
        ProductInfo productInfo = byId.get();

        //判断商品状态，是否下架
        if(productInfo.getProductStatus() == ResultEnums.PRODUCT_NOT_ENOUGH.getCode()){
            return ResultResponse.fail(ResultEnums.PRODUCT_NOT_ENOUGH.getMsg());
        }


        return ResultResponse.success(productInfo);
    }

    @Override
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepoisitory.save(productInfo);
    }
}
