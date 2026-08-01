package com.sky.controller.admin;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        //新增菜品后，需要删除redis中该分类的菜品数据
        redisTemplate.delete("dish_:" + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        //删除菜品后，需要删除redis中所有分类的菜品数据
        Set keys =  redisTemplate.keys("dish_*");
        //删除所有分类的菜品数据
        redisTemplate.delete(keys);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //修改菜品后，需要删除redis中所有分类的菜品数据
        Set keys =  redisTemplate.keys("dish_*");
        //删除所有分类的菜品数据
        redisTemplate.delete(keys);
        return Result.success();
    }
    /**
     * 根据分类id动态查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        log.info("根据分类id查询菜品：{}",categoryId);
        List<Dish> list = dishService.selectByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 修改菜品状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result updateStatus(@PathVariable Integer status,  Long id){
        log.info("修改菜品状态：{}",status);
        dishService.updateStatus(status,id);
        Set keys =  redisTemplate.keys("dish_*");
        //删除所有分类的菜品数据
        redisTemplate.delete(keys);
        return Result.success();
    }

}
