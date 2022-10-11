package com.cyh.reggie.dto;

import com.cyh.reggie.Entity.Setmeal;
import com.cyh.reggie.Entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
