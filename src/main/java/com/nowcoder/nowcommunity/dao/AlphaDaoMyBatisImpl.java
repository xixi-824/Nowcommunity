package com.nowcoder.nowcommunity.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author lei
 * @date 2020/7/21 22:42
 */

@Repository
@Primary
public class AlphaDaoMyBatisImpl implements IAlphaDao {

    @Override
    public String select() {
        return "MyBatis";
    }
}
