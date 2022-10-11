package com.cyh.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.reggie.Entity.Employee;
import com.cyh.reggie.mapper.EmployeeMapper;
import com.cyh.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl  extends ServiceImpl<EmployeeMapper, Employee > implements EmployeeService {
}
