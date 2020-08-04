package com.nowcoder.nowcommunity.dao;

import com.nowcoder.nowcommunity.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lei
 * @date 2020/7/27 10:30
 */


@Mapper
public interface DiscussPostMapper {

    // 动态SQL

    /**
     * 查询Id对应发的帖子数，跳过offset条数据，查询limit条数据(如果有这么多)
     * @param userId：用户名
     * @param offset：跳过offset条数据
     * @param limit：查询limit条数据
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 查询帖子数
    // @Param注解，给方法参数起别名
    // SQL中需要要到动态条件，条件需要用到该参数，
    // 对应的方法仅有一个参数，并且在<if>里使用，参数前必须用@Param注解起别名，否则会报错
    /**
     * 返回userId对应的发帖条数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId")int userId);
}
