package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void saveWithSetmealDishes(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐起售、停售
     * @param status 套餐状态
     * @param id 套餐id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return SetmealVO
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}

