package com.xmcc.Service;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;

public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);
}
