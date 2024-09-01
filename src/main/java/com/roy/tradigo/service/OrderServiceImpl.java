package com.roy.tradigo.service;

import com.roy.tradigo.domain.OrderStatus;
import com.roy.tradigo.domain.OrderType;
import com.roy.tradigo.model.*;
import com.roy.tradigo.repository.OrderItemRepository;
import com.roy.tradigo.repository.OrderRepository;
import com.roy.tradigo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {

        double price=orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

        Order order=new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimeStamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(()->new Exception("Order Not Found"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice){
        OrderItem orderItem=new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);

        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user) throws Exception {
        if(quantity<=0){
            throw new Exception("Quantity should Be Greater than Zero");
        }
        double buyPrice = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin, quantity,buyPrice, 0);
        Order order=createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order, user);

        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);

        Order saveOrder=orderRepository.save(order);

        // Create Asset
        Asset oldAsset=assetService.findAssetByUserIdAndCoinId(
                order.getUser().getId(),
                order.getOrderItem().getCoin().getId());
        if(oldAsset==null){
            assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity() );
        }
        else{
            assetService.updateAsset(oldAsset.getId(), quantity);
        }

        return saveOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, User user) throws Exception {
        if(quantity<=0){
            throw new Exception("Quantity should Be Greater than Zero");
        }

        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());

        if(assetToSell!=null){
            double buyPrice = assetToSell.getBuyPrice();
            double sellPrice = coin.getCurrentPrice();
            OrderItem orderItem = createOrderItem(coin, quantity,buyPrice, sellPrice);
            Order order=createOrder(user, orderItem, OrderType.SELL);
            orderItem.setOrder(order);

            if(assetToSell.getQuantity()>=quantity){
                walletService.payOrderPayment(order, user);
                order.setOrderStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);

                Order saveOrder=orderRepository.save(order);

                // Create Asset
                Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
                if(updatedAsset.getQuantity()*coin.getCurrentPrice() <=1){
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return saveOrder;
            }

            throw new Exception("Insufficient Quantity To Sell");

        }

        throw new Exception("Asset Not Found");
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {

        if(orderType.equals(OrderType.BUY)){
            return buyAsset(coin, quantity, user);
        }
        else if(orderType.equals(OrderType.SELL)){
            return sellAsset(coin, quantity, user);
        }

        throw new Exception("Invalid Order Type");

    }
}
