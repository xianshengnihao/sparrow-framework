package com.sina.sparrowframework.mybatis.internal;

import com.baomidou.mybatisplus.extension.service.IService;

public interface IBaseService<T> extends IService<T> {
    String TX_MANAGER = "txManager";

}
