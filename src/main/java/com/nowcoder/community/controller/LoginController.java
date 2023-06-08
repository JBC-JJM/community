package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.UserForm;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultKaptcha kaptcha;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "site/login";
    }

    @GetMapping("/kaptcha-image")
    public void getKaptchaImage(HttpSession session, HttpServletResponse response) throws IOException {
        String text = kaptcha.createText();
        BufferedImage img = kaptcha.createImage(text);

        session.setAttribute("verifycode", text);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            // 向页面输出验证码: 将img字节码拷贝到out以便响应为图片
            ImageIO.write(img, "jpg", out);
        } catch (Exception e) {
            logger.error("LoginController获取验证码图片失败");
        } finally {
            // 清空缓存区
            out.flush();
            out.close();
        }
    }


    @PostMapping("/login")
    public String login(Model model,
                        UserForm userForm,
                        HttpSession session,
                        HttpServletResponse response) {

        String verifyCode = (String) session.getAttribute("verifycode");

        //判空和验证码正确(无视大小写)吗
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(userForm.getVerifycode()) || !verifyCode.equalsIgnoreCase(userForm.getVerifycode())) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        //是否勾选记住我
        int seconds = userForm.getRememberme() ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        userForm.setExpiredSecond((long) seconds);

        Map<String, Object> map = userService.login(userForm);
        //调用service插入，没错就干活
        if (map.containsKey("ticket")) {
            //将凭证返回客户端
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(seconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        model.addAttribute("userForm", userForm);
        return "/site/login";
    }

    @GetMapping("logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }


    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map.get("code") == "success") {
            model.addAttribute("msg", "注册成功，请激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable int userId, @PathVariable String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        //都是跳到激活页面
        return "/site/operate-result";
    }
}