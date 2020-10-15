package com.sina.sparrowframework.mybatis.internal;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sina.sparrowframework.tools.struct.QueryRequest;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class BaseServiceImpl<M extends IBaseDao<T>, T> extends ServiceImpl<M , T> implements IBaseService<T> ,EnvironmentAware {

    public Environment env;


    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public Page<T> limit(QueryRequest vo) {
        return new Page<T>(vo.getPageNum(), vo.getPageSize());
    }

    public IPage<T> selectPage(QueryRequest vo, Wrapper<T> wrapper) {
        Page<T> page = limit(vo);
        return baseMapper.selectPage(page, wrapper);
    }

    public List<T> selectPageData(QueryRequest vo, Wrapper<T> wrapper) {
        Page<T> page = limit(vo);
        return baseMapper.selectPage(page, wrapper).getRecords();
    }

    public T selectOne(Wrapper<T> wrapper) {
        return baseMapper.selectOne(wrapper);
    }

    public IPage<Map<String, Object>> selectMapPage(QueryRequest vo, Wrapper<T> wrapper) {
        Page<T> page = limit(vo);
        return baseMapper.selectMapsPage(page, wrapper);
    }

    public List<Map<String, Object>> selectMapPageData(QueryRequest vo, Wrapper<T> wrapper) {
        Page<T> page = limit(vo);
        return baseMapper.selectMapsPage(page, wrapper).getRecords();
    }
}
