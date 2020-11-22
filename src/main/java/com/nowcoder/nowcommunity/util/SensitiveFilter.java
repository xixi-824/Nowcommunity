package com.nowcoder.nowcommunity.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    // 日志记录
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 过滤词替换常量
    private static String KeyWords = "***";

    // 前缀树根结点
    private TrieNode root = new TrieNode();

    /**
     * ioc容器在初始化启动时，会将该工具类组件以单例模式装入容器中
     * 应该将过滤敏感词txt文件读取
     */
    @PostConstruct
    public void init() {
        try (
                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        ) {
            String keywords = null;
            while ((keywords = reader.readLine()) != null) {
                // 将关键敏感词加入前缀树

            }
        } catch (IOException e) {
            logger.error("敏感词添加失败：" + e.getMessage());
        }
    }

    /**
     * 将一个敏感词添加到前缀树中
     * @param keyword
     */
    private void addKeyword(String keyword){
        TrieNode tempNode = root;
        for(int i = 0;i < keyword.length();i++){
            // 1、获取当前位字符
            char c = keyword.charAt(i);

            // 2、获取当前字符的子结点
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            // 3、循环添加下一结点
            tempNode = subNode;

            // 4、结尾结点设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 获取过滤后的内容
     * @param text：过滤前的文本内容
     * @return
     */
    public String getText(String text){
        // 1、判断内容是否为空
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 结果集
        StringBuilder sb = new StringBuilder(text.length());
        // 指针1：前缀树指针
        TrieNode curNode = root;

        // 指针2：文本内容检查起始位
        int begin = 0;
        // 指针3：文本内容检查末尾位
        int position = 0;

        while(position < text.length()){
            // 1、获取position位指针
            char c = text.charAt(position);

            // 2、当前位是否为特殊字符(例如:&、$)
            // &&赌&博&&
            if(isSymbol(c)){
                // 1、前缀树为起始位，将该符号位计入最终结果
                if(curNode == root){
                    begin++;
                }
                // 指针3后移一位
                position++;
                continue;
            }

            // 判断当前位字符是否为敏感字符
            curNode = curNode.getSubNode(c);
            if(curNode == null){
                // 当前[begin,position]不是敏感字符
                sb.append(text.charAt(begin));
                curNode = root;
                position = ++begin;
            }else if(curNode.isKeywordEnd()){
                // 到达敏感子树的尾部
                // 当前[begin,position]是敏感词
                sb.append(KeyWords);
                curNode = root;
                begin = ++position;
            }else{
                // 继续判断
                position++;
            }

            // 最后一批字符装入结果集
            sb.append(text.substring(begin));
        }
        return sb.toString();
    }

    /**
     * 判断是否为符号
     * @param c
     * @return true 表示当前为特殊字符
     */
    private boolean isSymbol(Character c){
        // 0x2E80 ~ 0x9FFF 是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树结点
    private class TrieNode {

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子结点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        /**
         * 添加子结点
         *
         * @param c
         * @param node
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        /**
         * 获取子结点
         *
         * @param c
         * @return
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
