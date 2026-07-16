package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品，同时插入菜品对应的数据，需要操作两张表：dish、dish_flavor
     * @param dishDTO
     */
    @Override
    @Transactional// 开启事务
    public void saveWithFlavor(DishDTO dishDTO) {

        log.info("新增菜品：{}",dishDTO);
        // 向dish表插入数据
        Dish dish = new Dish();
        // 对象拷贝
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        // 获取菜品id
        Long DishId =dish.getId();




        //获取口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            // 向dish_flavor表插入数据
            dishFlavorMapper.insertBatch(flavors);

        }



    }
}
