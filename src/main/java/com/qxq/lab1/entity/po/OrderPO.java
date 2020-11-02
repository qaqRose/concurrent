package com.qxq.lab1.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: QXQ
 * @time: 2020/11/1 16:08
 * @desc: TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPO implements Serializable {
    private Long userId;
    private Long itemId;
}
