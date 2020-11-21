package com.nowcoder.nowcommunity.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class LoginTicket implements Serializable {
    private int id;
    private int userId;
    private String ticket;
    private int status;  // 0-有效; 1-无效
    private Date expired;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }


    @Override
    public boolean equals(Object o) {
        // 同一个对象，则直接返回相等
        if(this == o){
            return true;
        }
        // 两者不是同一类型，直接返回false
        if(this.getClass() != o.getClass() || o == null){
            return false;
        }
        // 强制向下转型
        // 在当前类的内部，能够直接访问private成员属性
        // 如果新建实例对象，就只能通过set和get方法访问对象的成员属性
        LoginTicket loginTicket = (LoginTicket) o;
        return this.getId() == loginTicket .getId() &&
                this.userId == loginTicket .getUserId() &&
                this.status == loginTicket .getStatus() &&
                Objects.equals(ticket,loginTicket.ticket) &&
                ((this.expired == loginTicket.expired) || (this.expired != null && this.expired.equals(loginTicket.expired)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, ticket, status, expired);
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
