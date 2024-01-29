package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetmealMapper setmealMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    @Resource
    private DishMapper dishMapper;

    /**
     * 向套餐表和套餐菜品关联表插入数据
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithSetmealDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 插入套餐数据
        setmealMapper.insert(setmeal);

        // 获取insert语句生产的主键值
        Long setMealId = setmeal.getId();

        // 插入套餐关联菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setMealId));
            setMealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 套餐起售、停售
     * @param status 套餐状态
     * @param id 套餐id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 判断：包含未起售菜品不能起售
        // 1. 获取菜品ids
        List<SetmealDish> setMealDishes = setMealDishMapper.getBySetMealId(id);
        List<Long> dishIds = new ArrayList<>();
        setMealDishes.forEach(setmealDish -> dishIds.add(setmealDish.getDishId()));
        // 2. 通过菜品ids查询dish表
        List<Dish> dishes = dishMapper.getByIds(dishIds);
        dishes.forEach(dish -> {
            if (Objects.equals(dish.getStatus(), StatusConstant.DISABLE)) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        });

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        setmealMapper.update(setmeal);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return SetmealVO
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 根据id查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        // 根据菜品id查询套餐关联菜品数据
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetMealId(id);
        // 将查询到的数据封装到VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 判断是否在起售中
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        // 根据id删除套餐数据
        setmealMapper.deleteByIds(ids);

        // 根据id删除套餐关联菜品数据
        setMealDishMapper.deleteByIds(ids);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 修改套餐表基本信息
        setmealMapper.update(setmeal);

        // 删除原有的套餐关联菜品数据
        setMealDishMapper.deleteBySetMealId(setmealDTO.getId());

        // 重新插入套餐关联菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
            setMealDishMapper.insertBatch(setmealDishes);
        }
    }
}
