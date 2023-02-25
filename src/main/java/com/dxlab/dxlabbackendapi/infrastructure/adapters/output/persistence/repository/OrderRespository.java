package com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.repository;

import com.dxlab.dxlabbackendapi.infrastructure.adapters.output.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRespository extends JpaRepository<OrderEntity, Long> {
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ordenes SET resultado_listo = :readyResult, fecha_resultado = CURRENT_TIMESTAMP WHERE id = :orderId", nativeQuery = true)
    int updateDateResult (@Param("orderId") Long orderId, @Param("readyResult") boolean readyResult);
}
