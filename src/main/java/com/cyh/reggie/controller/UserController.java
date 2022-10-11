package com.cyh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyh.reggie.Entity.User;
import com.cyh.reggie.common.R;
import com.cyh.reggie.service.UserService;
import com.cyh.reggie.utils.SMSUtils;
import com.cyh.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码ValidateCodeUtils的generateValidateCode（）
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("生成的验证码位{}",code);

//            //调用阿里云短信服务发送短信SMSUtils的sendMessage（）
//            SMSUtils.sendMessage("瑞吉外卖", "阿里云申请的code", phone, code);

            //生成的验证码保存到session
            httpSession.setAttribute( phone, code);
            R.success("手机验证码发送成功");

        }

        return R.error("发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession){
        //获取请求中的电话
        String phone = (String)map.get("phone");
        //获取请求中的验证码
        String code = (String)map.get("code");
        //获取session中锋验证码
        String codeInSession = (String) httpSession.getAttribute(phone);
        //验证码进行比对（保存在session中的和前端传来的）
        if (codeInSession != null && codeInSession.equals(code)){
            //比对成功,判断该电话是否存在数据库中
            LambdaQueryWrapper<User>  queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                //用户不存在，创建用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登陆失败");
    }

}
