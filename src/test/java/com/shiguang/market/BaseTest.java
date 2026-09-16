package com.shiguang.market;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 测试基类：H2 内存数据库 + Mock 中间件 + test 配置文件
 *
 * @author gugu
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class BaseTest {
}