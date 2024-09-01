package com.roy.tradigo.service;

import com.roy.tradigo.domain.OrderType;
import com.roy.tradigo.model.Coin;
import com.roy.tradigo.model.Order;
import com.roy.tradigo.model.OrderItem;
import com.roy.tradigo.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;

}
