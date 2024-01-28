package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据菜品Id查询对应的套餐Id
     * @param dishIds 菜品id列表
     * @return List<Long> SetMealIds
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);
}
