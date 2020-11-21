package com.nowcoder.nowcommunity.dao;

import com.nowcoder.nowcommunity.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

//@CacheNamespace(blocking = true)//开启二级缓存
@Mapper
@Repository
public interface LoginTicketMapper {

    /**
     * 插入一条登录凭证
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 查询登录凭证
     * @param ticket 凭证字符串
     * @return
     */
    @Select(
            {"select id,user_id,ticket,status,expired ",
             "from login_ticket where ticket = #{ticket}"
            }
    )
    @Options(useCache = true,timeout = 1000,flushCache = Options.FlushCachePolicy.FALSE)
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改登录凭证的状态
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);
}
