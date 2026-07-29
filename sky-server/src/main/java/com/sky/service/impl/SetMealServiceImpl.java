package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 套餐分页查询
     * @param setMealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setMealPageQueryDTO) {
        log.info("分页查询：{}",setMealPageQueryDTO);
        PageHelper.startPage(setMealPageQueryDTO.getPage(), setMealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setMealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        log.info("新增套餐：{}",setmealDTO);
        Setmeal setmeal = new Setmeal();
        // 对象拷贝
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表插入数据
        setmealMapper.insert(setmeal);

        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //向套餐菜品表插入数据
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional// 开启事务，确保删除套餐和关联菜品菜品操作的原子性
    public void deleteBatch(List<Long> ids) {
        // 查询当前套餐状态，是否可以删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                // 状态为1，不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        for (Long id : ids) {
            // 删除套餐表中的数据
            setmealMapper.deleteById(id);
            // 删除套餐和菜品的关联数据
            setmealDishMapper.deleteBySetmealId(id);
        }



    }

    /**
     * 根据id查询套餐和关联的菜品数据
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 先查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        // 查询关联的菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        // 封装数据并返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional// 开启事务，确保修改套餐和关联菜品菜品操作的原子性
    public void updateWithDish(SetmealDTO setmealDTO) {
        log.info("修改套餐：{}",setmealDTO);
        // 修改套餐表数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateById(setmeal);

        // 修改套餐菜品表数据,删除当前套餐对应的菜品数据，重新插入当前提交过来的菜品数据
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
        });
        // 保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        // 修改套餐表数据
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.updateById(setmeal);
    }


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }


}
