package com.sina.sparrowframework.id;

import java.util.Date;


public interface IdProvider {

    long genId();

    Id expId(long id);

    long makeId(long time, long seq);

    long makeId(long time, long seq, long machine);

    long makeId(long genMethod, long time, long seq, long machine);

    long makeId(long type, long genMethod, long time,
                long seq, long machine);

    long makeId(long version, long type, long genMethod,
                long time, long seq, long machine);

    Date transTime(long time);

}
