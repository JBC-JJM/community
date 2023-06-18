package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.UpdatePasswordFrom;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //本地文件夹
    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @GetMapping("/setting")
    public String setting() {
        return "/site/setting";
    }

    //上传图片

    /**
     * @param model
     * @param headerImage : 图片文件接收,不要犯请求参数名称不对应的错误,为了使用什么nb的框架参数在http参数后名称不一致大概都会有错，即使封装了
     *                    需要注意的是这样默认传输的图片不可以大于1m
     *                    参考 https://blog.csdn.net/weixin_62883794/article/details/130746018
     * @return
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadImage(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        //1、保存文件到本地
        String fileName = headerImage.getOriginalFilename();
        String imageType = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(imageType)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        //新建本地图片文件
        String imageName = CommunityUtil.generateUUID() + "." + imageType;
        File file = new File(uploadPath + "/" + imageName);
        //流输出
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        //2、更新用户头像
        User user = hostHolder.getUser();
        //域名（端口）+项目目录+user/header+文件名
        String headerUrl = domain + contextPath + "/user/header/" + imageName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    //万能的图片输出流，图片访问路径
    @GetMapping("/header/{fileName}")
    public void getHeaderImage(@PathVariable String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件类型
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        //自动关闭tyr-catch-resources 资源
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
//            byte[] buffer = new byte[1024];
//            int b = 0;
//            while ((b = fis.read(buffer)) != -1) {
//                os.write(buffer, 0, b);
//            }

            //输入流(BufferedImage)、类型、输出流
            ImageIO.write(ImageIO.read(fis), suffix, os);
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //修改密码
    @PostMapping("/updatePassword")
    public String updatePassword(Model model, UpdatePasswordFrom updatePasswordFrom) {
        User user = hostHolder.getUser();
        Integer id = user.getId();
        String newPassword = updatePasswordFrom.getNewPassword();
        String oldPassword = updatePasswordFrom.getOldPassword();
        String confirmPassword = updatePasswordFrom.getConfirmPassword();

        if (!confirmPassword.equals(newPassword)) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        boolean count = userService.updatePassword(id, newPassword, oldPassword);
        if (count == false) {
            throw new RuntimeException("更新密码失败，旧密码错误或者dao调用失败");
        }
        return "redirect:/index";
    }


    @Autowired
    private LikeService likeService;

    //用户详细页面
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable int userId, Model model) {

        User user = userService.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);

        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", userLikeCount);

        return "site/profile";
    }

}
