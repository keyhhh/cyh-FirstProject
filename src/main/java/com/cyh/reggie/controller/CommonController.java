package com.cyh.reggie.controller;

import com.cyh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 实现文件上传和下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    //读取配置文件中参数值
    @Value("${reggie.path}")
    private String basePath;

    /**
     * @param file 这个参数名不能随便写，需要和前端一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> uploaad(MultipartFile file) {
        //file是临时文件，需转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //原始文件名,并截取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //新文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断目录是否存在
        if (!dir.exists()) {
            //目录不存在需要创建
            dir.mkdirs();
        }
        try {
            //将文件转存到指定位置，并使用UUID命名
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回文件名，需要新增菜品，传完图片，会将文件名保存
        return R.success(fileName);
    }

    /**
     * 下载文件
     * 在上传之后，自动请求该方法，并回显图片
     * @param response
     * @param name
     */

    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            //输出流，通过输出流将文件写道浏览器，展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应文件类型
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[fileInputStream.available()];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            fileInputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}