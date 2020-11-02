package com.qxq.lab1.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Data
@Entity
@Table(name="record")
public class Record {
    @Id
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="item_id")
    private Long itemId;

    @Column(name="num")
    private Long num ;

    @Column(name="create_date")
    private Date createDate;
}
