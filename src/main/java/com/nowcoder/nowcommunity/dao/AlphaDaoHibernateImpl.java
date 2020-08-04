package com.nowcoder.nowcommunity.dao;

import org.springframework.stereotype.Repository;

/**
 * @author lei
 * @date 2020/7/21 22:35
 */

@Repository("AlphaDaoHibernate")
public class AlphaDaoHibernateImpl implements IAlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
