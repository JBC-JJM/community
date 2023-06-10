package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OtherTest {

    @Test
    public void testDate() {
//        参考: https://blog.csdn.net/weixin_45948234/article/details/112178525
        long timeMillis = System.currentTimeMillis();
        timeMillis += 3600 * 1000;  //以毫秒计算
        System.out.println("没有格式化的时间戳" + timeMillis);

        Date date = new Date(timeMillis);
        System.out.println("强转的时间戳" + date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        System.out.println("格式化的时间戳" + formatter.format(timeMillis));
    }

    //本地文件夹
    @Value("${community.path.upload}")
    private String uploadPath;

    @Test
    //参考 https://blog.csdn.net/u012581020/article/details/130625872
    public void testImageIO() throws Exception {
        String imagePath = uploadPath + "/" + "1.png";
        InputStream inputStream = new FileInputStream(new File(imagePath));
//
//        //放到内存中并封装一下,read( File、InputStream、URL 和 ImageInputStream类型都行)
//        BufferedImage readImage = ImageIO.read(inputStream);
//        //写到磁盘，write的输入流只支持BufferedImage
//        ImageIO.write(readImage, "png", new File(uploadPath +"/"+ "2.png"));

        OutputStream outputStream = new FileOutputStream(uploadPath + "/" + "2.png");
        byte[] buffer = new byte[1024];
        int b = 0;
        while ((b = inputStream.read(buffer)) != -1) {//一次性读一k
            System.out.println("一次性读" + b); //一次性写一k
            outputStream.write(buffer, 0, b);
        }

        inputStream.close();
        outputStream.close();
    }
}
