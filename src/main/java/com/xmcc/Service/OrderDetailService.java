package com.xmcc.Service;

import com.xmcc.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    //批量插入
    void batchInsert(List<OrderDetail> list);
}
