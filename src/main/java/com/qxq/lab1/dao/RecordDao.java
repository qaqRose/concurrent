package com.qxq.lab1.dao;

import com.qxq.lab1.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Repository
public interface RecordDao  extends JpaRepository<Record, Long> {

}
