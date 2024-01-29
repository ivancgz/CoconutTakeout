package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/admin/setmeal")
@Api("套餐相关接口")
@Slf4j
public class SetMealController {

    @Resource
    private SetMealService setMealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<?> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setMealService.saveWithSetmealDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return Result<SetmealVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setMealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return Result<PageResult>
     */
    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return Result
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result<?> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setMealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售、停售
     * @param status 套餐状态
     * @param id 套餐id
     * @return Result
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售")
    public Result<?> startOrStop(@PathVariable Integer status, @PathParam(value = "id") Long id) {
        log.info("套餐起售、停售：{}", status);
        setMealService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return Result
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result<?> delete(@RequestParam("ids") List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setMealService.deleteBatch(ids);
        return Result.success();
    }
}
