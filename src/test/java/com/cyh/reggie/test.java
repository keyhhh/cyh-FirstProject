package com.cyh.reggie;

import com.cyh.reggie.Entity.Employee;
import com.cyh.reggie.mapper.EmployeeMapper;
import com.cyh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
@Slf4j
public class test {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void test1() {
        Employee employee = employeeMapper.selectById(1);
        log.info(employee.toString());
        //新增
//        employeeMapper.insert(employee);
        //删除
//        employeeMapper.deleteById(1);
//        Map<String,Object> map = new HashMap<>();
//        map.put( "name", "zhangsan");
//        employeeMapper.deleteByMap(map);
        //修改
//        employee.setId(1L);
//        employee.setUpdateTime(LocalDateTime.now());
//        employeeMapper.updateById(employee);
        //查询
//        List<Long> list = Arrays.asList(1L,2L,3L);
//        employeeMapper.selectBatchIds(list);
//        List<Map<String, Object>> maps = employeeMapper.selectMaps(null);
//        maps.forEach(System.out::println);

//        employeeService.save(new Employee());
//        employeeService.count();//查询总记录数

//        List<Employee> list = new ArrayList<>();
//        list.add(new Employee());
//        boolean b = employeeService.saveBatch(list);//批量添加,返回操作成功否
    }
}
