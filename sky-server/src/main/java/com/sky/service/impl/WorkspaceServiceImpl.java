package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return BusinessDataVO
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        // 参数配置map
        Map map = setParam(begin, end);
        
        // 今日新增用户数
        Integer newUsers = userMapper.getByDate(map);

        // 订单总数
        Integer totalOrder = orderMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        
        // 营业额
        Double turnover = orderMapper.sumByMap(map) == null ? 0.0 : orderMapper.sumByMap(map);

        // 有效订单数	
        Integer validOrderCount = orderMapper.countByMap(map);

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrder > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrder.doubleValue();
        }

        // 平均可单价
        Double unitPrice = 0.0;
        if (validOrderCount > 0) {
            unitPrice = turnover / validOrderCount.doubleValue();
        }

        return BusinessDataVO.builder()
                             .newUsers(newUsers)
                             .turnover(turnover)
                             .validOrderCount(validOrderCount)
                             .orderCompletionRate(orderCompletionRate)
                             .unitPrice(unitPrice)
                             .build();
    }

    /**
     * 查询订单管理数据
     * @return OrderOverViewVO
     */
    @Override
    public OrderOverViewVO getOverviewOrders(LocalDateTime begin, LocalDateTime end) {
        // 参数配置map
        Map map = setParam(begin, end);

        //全部订单
        Integer allOrders = orderMapper.countByMap(map);

        //待接单数量
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);

        //待派送数量
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        //已完成数量
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        //已取消数量
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                              .allOrders(allOrders)
                              .waitingOrders(waitingOrders)
                              .deliveredOrders(deliveredOrders)
                              .completedOrders(completedOrders)
                              .cancelledOrders(cancelledOrders)
                              .build();
    }

    /**
     * 查询菜品总览
     * @return DishOverViewVO
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        // 起售菜品
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);

        // 停售菜品
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                             .sold(sold)
                             .discontinued(discontinued)
                             .build();
    }

    /**
     * 查询套餐总览
     * @return SetmealOverViewVO
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        // 起售套餐
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);

        // 停售套餐
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                                .sold(sold)
                                .discontinued(discontinued)
                                .build();
    }

    /**
     * 设置参数map
     * @return Map
     */
    private Map setParam(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        return map;
    }
}
