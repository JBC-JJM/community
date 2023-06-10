package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();


    //bean的生命周期!类似的东西
    @PostConstruct
    public void init() {
        //java自动关闭流
        try (
                //this的作用域
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //加个缓冲流,可以一次读一行
                BufferedReader words = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            //一个一个的单词的存放到前缀树中
            while ((keyword = words.readLine()) != null) {
                rootNode.addSubNode(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }


    //敏感词加入到前缀树中，没有实现其他功能是因为这里只用到了遍历判断节点是否存在
    private class TrieNode {
        //这里省略了pass
        //最终节点标识end，没有额外的功能，只是标志为一个word的结束
        private boolean end = false;
        //子节点的字符，嵌套子节点
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public void addSubNode(String word) {
            TrieNode node = this; //从根节点开始
            for (Character c : word.toCharArray()) {   //遍历字符串
                if (node.getSubNode(c) == null) {       //该字符不存在
                    node.subNode.put(c, new TrieNode());
                }
                node = node.getSubNode(c);  //进入下一个子节点
            }
            node.setEnd(true);  //最终节点设置为真， 设置结束标识
        }

        //获取默认子节点
        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }
    }


    /**
     * 双指针过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //树的指针
        TrieNode node = rootNode;
        int begin = 0;
        int end = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        //这里使用while，由于会有指针回退的效果和一个指针一直向前走，但是另一个指针回回退反复走，是不可以使用for的
        while (begin < text.length()) {
            if (end < text.length()) {
                char c = text.charAt(end);

                // 跳过符号
                if (isSymbol(c)) {
                    // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                    if (node == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    // 无论符号在开头或中间,指针3都向下走一步
                    end++;
                    continue;
                }

                node = node.getSubNode(c);//子节点
                if (node == null) {         //字母不在树中
                    sb.append(text.charAt(begin));
                    end = ++begin;//end回退
                    node = rootNode;
                } else if (node.isEnd()) {  //敏感词出现
                    sb.append(REPLACEMENT);
                    begin = ++end;
                    node = rootNode;
                } else {                    //字母在树中且不是end
                    ++end;
                }
            } else { //end突破了
                sb.append(text.charAt(begin));
                end = ++begin;//end回退
                node = rootNode;
            }
        }

        return sb.toString();
    }


    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
