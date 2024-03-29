package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * 菜品管理
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品浏览接口")
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return Result<List<DishVO>>
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> getByCategoryId(@PathParam("categoryId") Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        // 构造 Redis 中的 key，规则：dish_分类id
        String key = "dish_" + categoryId;

        // 查询 redis 中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
            // 如果存在，直接返回，无须查询数据库
            return Result.success(list);
        }

        // 如果不存在，查询数据库，将查询到的数据放入 Redis 中
        list = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}
