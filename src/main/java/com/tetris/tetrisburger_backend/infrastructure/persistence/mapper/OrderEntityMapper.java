package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.OrderItem;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.OrderEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.OrderItemEntity;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.OrderResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

    default Order toDomain(OrderEntity e) {
        if (e == null) return null;

        List<OrderItem> items = e.getItems() == null ? List.of() :
                e.getItems().stream().map(this::toDomainItem).toList();

        return Order.reconstitute(
                e.getIdOrder(), e.getIdUser(), e.getOrderNumber(),
                OrderStatus.valueOf(e.getStatus().name()),
                e.getTotalAmount(),
                e.getOrderDate(), e.getUpdatedAt(), e.getDeletedAt(),
                e.getCreatedBy(), e.getUpdatedBy(), e.getDeletedBy(),
                items,
                e.getPayment() != null ? e.getPayment().getPaymentMethod() : null  // 👈
        );
    }



    default OrderEntity toEntity(Order o) {
        if (o == null) return null;

        OrderEntity e = new OrderEntity();
        e.setIdOrder(o.getIdOrder());
        e.setIdUser(o.getIdUser());
        e.setOrderNumber(o.getOrderNumber());
        e.setStatus(OrderStatus.valueOf(o.getStatus().name()));  // ← mismo enum
        e.setTotalAmount(o.getTotalAmount());
        e.setOrderDate(o.getOrderDate());
        e.setUpdatedAt(o.getUpdatedAt());
        e.setDeletedAt(o.getDeletedAt());
        e.setCreatedBy(o.getCreatedBy());
        e.setUpdatedBy(o.getUpdatedBy());
        e.setDeletedBy(o.getDeletedBy());

        List<OrderItemEntity> itemEntities = o.getItems() == null ? List.of() :
                o.getItems().stream().map(item -> toEntityItem(item, e)).toList();
        e.setItems(itemEntities);

        return e;
    }

    default OrderItem toDomainItem(OrderItemEntity e) {
        if (e == null) return null;
        return OrderItem.reconstitute(
                e.getIdOrderItem(),
                e.getOrder().getIdOrder(),
                OrderItemType.valueOf(e.getItemType().name()),
                e.getIdBurger(), e.getIdProduct(),
                e.getItemName(), e.getQuantity(),
                e.getUnitPrice(), e.getSubtotal()
        );
    }

    default OrderItemEntity toEntityItem(OrderItem item, OrderEntity orderRef) {
        if (item == null) return null;
        OrderItemEntity e = new OrderItemEntity();
        e.setOrder(orderRef);
        e.setItemType(OrderItemType.valueOf(item.getItemType().name())); // ← mismo enum
        e.setIdBurger(item.getIdBurger());
        e.setIdProduct(item.getIdProduct());
        e.setItemName(item.getItemName());
        e.setQuantity(item.getQuantity());
        e.setUnitPrice(item.getUnitPrice());
        e.setSubtotal(item.getSubtotal());
        return e;
    }

    default List<Order> toDomainList(List<OrderEntity> entities) {
        return entities == null ? List.of() :
                entities.stream().map(this::toDomain).toList();
    }
}