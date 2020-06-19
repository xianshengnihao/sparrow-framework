package com.sina.sparrowframework.id.impl;


import com.sina.sparrowframework.id.AbstractIdProvider;
import com.sina.sparrowframework.id.Id;
import com.sina.sparrowframework.id.IdPopulator;
import com.sina.sparrowframework.id.IdType;

public class IdProvidermpl extends AbstractIdProvider {

    protected IdPopulator idPopulator;

    public IdProvidermpl() {
        super();
    }

    public IdProvidermpl(String type) {
        super(type);
    }

    public IdProvidermpl(long type) {
        super(type);
    }

    public IdProvidermpl(IdType type) {
        super(type);
    }

    @Override
    public void init() {
        super.init();
        initPopulator();
    }

    public void initPopulator() {
        idPopulator = new LockIdPopulator();
    }

    protected void populateId(Id id) {
        idPopulator.populateId(timer, id, idMeta);
    }

    public void setIdPopulator(IdPopulator idPopulator) {
        this.idPopulator = idPopulator;
    }
}
