package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据菜品Id查询对应的套餐Id
     * @param dishIds 菜品id列表
     * @return List<Long> SetMealIds
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量套餐关联菜品插入
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐关联菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetMealId(Long id);

    /**
     * 根据套餐id返回套餐关联数据
     * @param id
     * @return SetmealDish
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id};")
    List<SetmealDish> getBySetMealId(Long id);

    /**
     * 根据套餐选择id删除套餐关联菜品数据
     * @param ids
     */
    void deleteByIds(List<Long> ids);
}
