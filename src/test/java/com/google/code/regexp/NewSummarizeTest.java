package com.google.code.regexp;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: Java 源代码的字符串中的反斜线被解释为 Unicode 转义或其他字符转义。因此必须在字符串字面值中使用两个反斜线，表示正则表达式受到保护，不被 Java 字节码编译器解释。
 * @author: chenr
 * @date: 18-7-2
 */
public class NewSummarizeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private boolean genMatcher(String parStr, String result) {
        return Pattern.compile(parStr).matcher(result).find();
    }

    /**
     * 1. "\d" 匹配数字
     * 2. "\w" 匹配字母或数字
     * 3. "\s" 匹配空格
     * 4. "\b" 匹配字边界
     */
    @Test
    public void 精确匹配() {
        Assert.assertTrue(genMatcher("00\\d", "008"));  //tip: don`t match "00A"
        Assert.assertTrue(genMatcher("er\\b", "never"));
        Assert.assertFalse(genMatcher("er\\b", "nevered"));
    }

    /**
     * 1. "."匹配任意字符
     */
    @Test
    public void 任意匹配() {
        Assert.assertTrue(genMatcher("ja.", "java"));    //tip: just match "jav"
    }

    /**
     * 1. "*" 任意个字符(包含0个) === "{0}"
     * 2. "+" 至少一个字符 === "{1}"
     * 3. "?" 0或1个字符 === "{0,1}"
     * 4. "{n}" "{n,m}" n-m个字符
     */
    @Test
    public void 变长匹配() {
        Assert.assertTrue(genMatcher("\\d{3,3}", "001"));
        Assert.assertTrue(genMatcher("0+", "9031"));
        Assert.assertTrue(genMatcher("0*", "931"));
    }

    /**
     * 1. "[]" 匹配范围,在方括号中，不需要转义字符
     * 2. "[^]" 不匹配
     * 2. "^" 行开头 "$" 结尾
     * 3. "|" 或者匹配 === "[ab]"
     */
    @Test
    public void 进阶精确匹配() {
        Assert.assertTrue(genMatcher("[0-9a-zA-Z_]+", "_D3")); //数字 字母 下划线匹配字符
        Assert.assertTrue(genMatcher("[a-zA-Z\\_][0-9a-zA-Z\\_]*", "ai"));  // 字母开头,任意字母
        Assert.assertTrue(genMatcher("(J|j)ava", "java"));
        Assert.assertTrue(genMatcher("^ja", "java"));
        Assert.assertTrue(genMatcher("^ja$", "ja"));

    }

    /**
     * "()" 分组提取
     */
    @Test
    public void 分组提取() {
        Pattern pattern = Pattern.compile("^(\\d{3})-(\\d{3,8})$");
        Matcher matcher = pattern.matcher("010-123456");
        while (matcher.find()) {
            Assert.assertEquals(matcher.group(0), "010-123456");
            Assert.assertEquals(matcher.group(1), "010");
            Assert.assertEquals(matcher.group(2), "123456");
        }
    }

    /**
     * "\d+?" 非贪婪匹配
     */
    @Test
    public void 非贪婪匹配() {
        Pattern pattern = Pattern.compile("^(\\d+?)(0*)$");
        Matcher matcher = pattern.matcher("10203000");
        while(matcher.find()) {
            Assert.assertEquals(matcher.group(2), "000");
        }
    }
}
