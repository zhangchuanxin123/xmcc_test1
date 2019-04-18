package com.xmcc.dao.Impl;

import com.xmcc.dao.BatchDao;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class BatchDaoImpl<T> implements BatchDao<T> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void batchInsert(List<T> list) {
        int size = list.size();
        //循环存入缓存区
        for (int i = 0; i < size; i++) {
            em.persist(list.get(i));

            //每100条写入数据库，如果不足100条，接直接将全部数据写入数据库
            if(i % 100 == 0 || i == size-1){
                em.flush();
                em.clear();
            }
        }
    }
}
