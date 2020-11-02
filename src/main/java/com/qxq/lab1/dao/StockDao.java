package com.qxq.lab1.dao;

import com.qxq.lab1.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Repository
public interface StockDao extends JpaRepository<Stock, Long> {

    Stock getByItemId(long itemId);

    @Query(value = "SELECT * FROM stock WHERE item_id=?1 for update",nativeQuery = true)
    Stock selectByItemIdForUpdate(long itemId);


    @Modifying
    @Query(value = "UPDATE stock SET count = count - 1, version = version + 1 WHERE id = ?1 and version = ?2",nativeQuery = true)
    int reduceStockWithVersion(long stockId, long version);

    @Modifying
    @Query(value = "UPDATE stock SET count = count - 1 WHERE id = ?1",nativeQuery = true)
    @Transactional
    int reduceStock(long stockId);
}
