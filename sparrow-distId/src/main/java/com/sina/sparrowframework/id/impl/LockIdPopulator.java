package com.sina.sparrowframework.id.impl;


import com.sina.sparrowframework.id.BasePopulator;
import com.sina.sparrowframework.id.Id;
import com.sina.sparrowframework.id.IdMeta;
import com.sina.sparrowframework.id.Timer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockIdPopulator extends BasePopulator {

    private Lock lock = new ReentrantLock();

    public LockIdPopulator() {
        super();
    }

    public void populateId(Timer timer, Id id, IdMeta idMeta) {
        lock.lock();
        try {
            super.populateId(timer, id, idMeta);
        } finally {
            lock.unlock();
        }
    }

}
