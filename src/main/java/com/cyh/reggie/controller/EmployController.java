package com.cyh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyh.reggie.Entity.Employee;
import com.cyh.reggie.common.R;
import com.cyh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("employee")
public class EmployController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 登陆判断
     *
     * @param employee 前端传来json形式数据，参数及注解RequestBody
     * @param request  登陆成功后将员工对象ID存储在Session，未来获取目前登录的用户时可以直接get
     * @return
     */
    @PostMapping("login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {

        //1.将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//用户名字段有唯一约束所以getOne

        //3.若未查询到返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4.密码比对，不一致返回登陆失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5.查询员工状态，若为已禁用，则返回员工已禁用结果（员工数据内的status字段为1表示正常）
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6.登陆成功，将员工id存入session并返回登录成功
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());


        return R.success(emp);
    }


    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的员工信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * 添加员工保存的请求，Request URL: http://localhost:8080/employee，因此不在需要写路径
     *
     * @param request
     * @param employee 添加@RequestBody注解是因为前端穿的数据时JSON形式
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息为{}", employee.toString());
        //初始化的默认密码，不要使用明文
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

//        //记录的创建时间LocalDateTime.now()获取当前系统的时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获取当前登录用户的ID，就是创建这条信息的用户
//        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        /*
        保存信息时save()可能出现数据库字段的唯一性约束，抛出异常，
        可以对单个使用try catch，但使用异常处理器全局捕获，需创建类
         */
        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /***
     * Page类中有前台需要的records、total,所以使用page泛型；前端使用的get，
     * @param page 前端传的页码
     * @param pageSize 前端传出单页显示数据量
     * @param name 根据姓名查询
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //构造分页构造器，通过Page对象
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器，name
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件,.like()中的StringUtils.isNotEmpty(name)判断是否为空，为空不执行
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByAsc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据员工id修改信息
     * 关于禁用启用账号，因为前端传来的status已经是相反的数据，所以不用重新设定
     * @param employee
     * @return
     */

    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request){
        /*j
        s对long型数字处理丢失精度，id为19位，js只能处理16，导致传来的id错误
        可以对服务端给页面响应JSON数据时处理，将long型转为字符串类型

         */
        log.info(employee.toString());

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }


    /***
     * 根据员工id查询员工信息"
     * @param id 因为id是拼接在url后的，所以 @GetMapping("/{id}")，并在形参前加注解@PathVariable
     * @return R.success(信息)
     */

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据员工id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("未查询到员工信息");
    }

}
