package com.qxq.lab1.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Data
@Entity
@DynamicInsert
@Table(name="stock")
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="item_id", length = 50)
    private Long itemId;

    @Column(name="count")
    private Long count ;

    @Column(name="version")
    private Long version;
}
