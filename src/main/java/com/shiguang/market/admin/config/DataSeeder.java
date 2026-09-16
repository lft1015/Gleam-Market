package com.shiguang.market.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.mapper.AnnouncementMapper;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.mapper.ClaimMapper;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class DataSeeder implements ApplicationRunner {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final PasswordEncoder passwordEncoder;
    private final LostFoundMapper lostFoundMapper;
    private final AnnouncementMapper announcementMapper;
    private final ClaimMapper claimMapper;

    private static final String[] TEST_USERNAMES = {"testuser1", "testadmin", "testsuperadmin"};
    private static final String TEST_PASSWORD = "123456";
    private static final String[] NICKNAMES = {"小明（普通用户）", "小红（管理员）", "小刚（超管）"};
    private static final String[] PHONES = {"13800000001", "13800000002", "13800000003"};
    private static final String[] EMAILS = {"test1@gleam.com", "test2@gleam.com", "test3@gleam.com"};
    private static final String[] ROLES = {"USER", "ADMIN", "SUPER_ADMIN"};

    @Override
    public void run(ApplicationArguments args) {
        log.info("[DataSeeder] 开始执行测试数据播种...");
        try {
            seedUsers();
            seedItems();
            seedLostFoundItems();
            seedClaims();
            seedAnnouncements();
            log.info("[DataSeeder] 测试数据播种完成");
        } catch (Exception e) {
            log.error("[DataSeeder] 测试数据播种异常", e);
        }
    }

    private void seedUsers() {
        int created = 0;
        for (int i = 0; i < TEST_USERNAMES.length; i++) {
            String uname = TEST_USERNAMES[i];
            User exist = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, uname));
            if (exist != null) {
                log.info("[DataSeeder] 用户 {} 已存在，跳过", uname);
                continue;
            }
            User user = new User();
            user.setUsername(uname);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setNickname(NICKNAMES[i]);
            user.setPhone(PHONES[i]);
            user.setEmail(EMAILS[i]);
            user.setRole(ROLES[i]);
            user.setStatus("ACTIVE");
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
            created++;
        }
        log.info("[DataSeeder] 播种 {} 个测试用户", created);
    }

    private void seedItems() {
        if (itemMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 商品已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long userId1 = getUserId(TEST_USERNAMES[0]);
        Long userId2 = getUserId(TEST_USERNAMES[1]);
        Long userId3 = getUserId(TEST_USERNAMES[2]);

        Item item1 = buildItem(userId1, "九成新机械键盘 Cherry MX 青轴",
                "自用 Cherry MX Board 3.0S 机械键盘，青轴，使用不到半年，按键灵敏无坏轴，包装齐全。",
                new BigDecimal("299.00"), new BigDecimal("599.00"), "数码电子",
                "ON_SALE", now.minusDays(3));

        Item item2 = buildItem(userId1, "《算法导论》第三版 几乎全新",
                "计算机经典教材，买来只看了一章，几乎全新无笔记，送电子版习题答案。",
                new BigDecimal("45.00"), new BigDecimal("128.00"), "图书教材",
                "ON_SALE", now.minusDays(5));

        Item item3 = buildItem(userId2, "宿舍用小冰箱 50L 静音节能",
                "50L 小型冰箱，适合宿舍使用，一级能效，运行安静，制冷效果好，毕业转让。",
                new BigDecimal("199.00"), new BigDecimal("399.00"), "生活用品",
                "ON_SALE", now.minusDays(2));

        Item item4 = buildItem(userId2, "Nike Air Force 1 白色 42码 仅试穿",
                "正品 Nike AF1 纯白，42码，买大半码仅试穿一次，鞋盒吊牌齐全。",
                new BigDecimal("399.00"), new BigDecimal("799.00"), "服饰鞋包",
                "ON_SALE", now.minusDays(7));

        Item item5 = buildItem(userId3, "尤尼克斯羽毛球拍 纳米速攻系列",
                "Yonex 纳米速攻系列球拍，已拉线 24 磅，附带原装拍套，成色 85 新。",
                new BigDecimal("150.00"), new BigDecimal("380.00"), "运动户外",
                "ON_SALE", now.minusDays(4));

        Item item6 = buildItem(userId3, "国誉活页笔记本套装 B5 全新未拆",
                "Kokuyo 国誉活页本 B5 尺寸，内含 100 页替芯，全新未拆封，多色可选。",
                new BigDecimal("25.00"), new BigDecimal("49.00"), "文具办公",
                "ON_SALE", now.minusDays(1));

        Item item7 = buildItem(userId1, "小米充电宝 20000mAh 快充版",
                "小米移动电源 20000mAh，支持 22.5W 快充，Type-C 双向快充，循环次数少。",
                new BigDecimal("69.00"), new BigDecimal("149.00"), "其他",
                "ON_SALE", now.minusDays(6));

        itemMapper.insert(item1);
        itemMapper.insert(item2);
        itemMapper.insert(item3);
        itemMapper.insert(item4);
        itemMapper.insert(item5);
        itemMapper.insert(item6);
        itemMapper.insert(item7);
        log.info("[DataSeeder] 播种 7 个示例商品");
    }

    private void seedLostFoundItems() {
        if (lostFoundMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 失物招领已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long userId1 = getUserId(TEST_USERNAMES[0]);
        Long userId2 = getUserId(TEST_USERNAMES[1]);

        // 待处理 - 寻物
        lostFoundMapper.insert(buildLostFound(userId1,
                "寻找黑色双肩包 图书馆三楼遗忘",
                "6月15日下午在图书馆三楼自习区遗忘一个黑色瑞士军刀双肩包，内有教材和文具，捡到请联系。",
                "LOST", "图书馆三楼自习区", now.minusDays(3),
                "QQ: 1234567890", "PENDING"));

        // 待处理 - 招领
        lostFoundMapper.insert(buildLostFound(userId2,
                "捡到白色 AirPods 充电盒",
                "在食堂二楼捡到一个白色 AirPods 充电盒（不含耳机），请失主联系确认后归还。",
                "FOUND", "食堂二楼", now.minusDays(1),
                "微信: xiaohong_wx", "PENDING"));

        // 待处理 - 寻物
        lostFoundMapper.insert(buildLostFound(userId1,
                "丢失校园卡 学号 2021XXXXXX",
                "在教学楼 B 区附近丢失校园卡一张，卡号后四位 8823，捡到请联系，非常感谢！",
                "LOST", "教学楼 B 区", now.minusDays(2),
                "电话: 13800000001", "PENDING"));

        // 处理中 - 寻物
        lostFoundMapper.insert(buildLostFound(userId2,
                "丢失 Kindle Paperwhite 电子书阅读器",
                "上周五在图书馆四楼阅览室忘记带走一台 Kindle Paperwhite 第四代，黑色皮质保护套，内有大量专业书籍。",
                "LOST", "图书馆四楼阅览室", now.minusDays(7),
                "QQ: 9876543210", "PROCESSING"));

        // 处理中 - 招领
        lostFoundMapper.insert(buildLostFound(userId1,
                "捡到一串钥匙 带 U 盘",
                "在教学楼 A 区走廊捡到一串钥匙，上面挂有一个 32GB 金士顿 U 盘和一个小熊挂件。",
                "FOUND", "教学楼 A 区走廊", now.minusDays(4),
                "微信: xiaoming_wx", "PROCESSING"));

        // 已找回/已归还
        lostFoundMapper.insert(buildLostFound(userId2,
                "丢失水杯 蓝色保温杯 500ml",
                "在田径场看台遗忘一个蓝色膳魔师保温杯 500ml，已找回，感谢捡到的同学！",
                "LOST", "田径场看台", now.minusDays(10),
                "电话: 13800000002", "FOUND"));

        // 已关闭
        lostFoundMapper.insert(buildLostFound(userId1,
                "寻找灰色折叠伞 天堂牌",
                "下雨天在食堂门口拿错了一把灰色天堂折叠伞，自己的那把被他人拿走，已放弃寻找。",
                "LOST", "食堂门口", now.minusDays(14),
                "QQ: 111222333", "CLOSED"));

        log.info("[DataSeeder] 播种 7 条失物招领（含多状态）");
    }

    private LostFound buildLostFound(Long userId, String title, String description,
                                      String type, String location, LocalDateTime lostTime,
                                      String contact, String status) {
        LostFound lf = new LostFound();
        lf.setUserId(userId);
        lf.setTitle(title);
        lf.setDescription(description);
        lf.setType(type);
        lf.setLocation(location);
        lf.setLostTime(lostTime);
        lf.setContact(contact);
        lf.setStatus(status);
        lf.setCreateTime(LocalDateTime.now());
        lf.setUpdateTime(LocalDateTime.now());
        return lf;
    }

    private void seedClaims() {
        if (claimMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 认领记录已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 获取失物招领 ID（按标题查）
        LostFound airpods = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "捡到白色 AirPods 充电盒"));
        LostFound keys = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "捡到一串钥匙 带 U 盘"));
        LostFound kindle = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "丢失 Kindle Paperwhite 电子书阅读器"));

        Long userId1 = getUserId(TEST_USERNAMES[0]);
        Long userId2 = getUserId(TEST_USERNAMES[1]);

        // 认领 AirPods - 待审核
        if (airpods != null) {
            Claim c1 = new Claim();
            c1.setLostFoundId(airpods.getId());
            c1.setClaimantId(userId1);
            c1.setMessage("我的 AirPods 充电盒，背面有一道小划痕，可以拍照片确认。");
            c1.setContact("电话: 13800000001");
            c1.setVerification("可提供购买记录和序列号");
            c1.setStatus("PENDING_REVIEW");
            c1.setCreateTime(now.minusHours(12));
            c1.setUpdateTime(now.minusHours(12));
            claimMapper.insert(c1);
        }

        // 认领钥匙 - 已通过
        if (keys != null) {
            Claim c2 = new Claim();
            c2.setLostFoundId(keys.getId());
            c2.setClaimantId(userId2);
            c2.setMessage("那串钥匙是我的，U 盘里有我的课程作业文件，挂件是女朋友送的生日礼物。");
            c2.setContact("微信: xiaohong_wx");
            c2.setVerification("可当场说出 U 盘内文件内容");
            c2.setStatus("APPROVED");
            c2.setCreateTime(now.minusDays(3));
            c2.setUpdateTime(now.minusDays(2));
            claimMapper.insert(c2);
        }

        // 认领 Kindle - 已拒绝
        if (kindle != null) {
            Claim c3 = new Claim();
            c3.setLostFoundId(kindle.getId());
            c3.setClaimantId(userId1);
            c3.setMessage("我的 Kindle 是 Paperwhite 第四代，黑色保护套，里面有一本《深入理解计算机系统》的电子书。");
            c3.setContact("QQ: 1234567890");
            c3.setVerification("无法提供购买凭证");
            c3.setStatus("REJECTED");
            c3.setCreateTime(now.minusDays(5));
            c3.setUpdateTime(now.minusDays(4));
            claimMapper.insert(c3);
        }

        log.info("[DataSeeder] 播种 3 条认领记录（待审核/已通过/已拒绝）");
    }

    private void seedAnnouncements() {
        if (announcementMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 公告已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Announcement a1 = new Announcement();
        a1.setTitle("欢迎使用 Gleam Market 校园二手交易平台");
        a1.setContent("欢迎来到 Gleam Market！这是一个专为校园师生打造的二手交易与失物招领平台。请注意遵守平台规则，诚信交易，共建和谐校园。如有任何疑问请联系管理员。");
        a1.setIsActive(true);
        a1.setCreateTime(now.minusDays(30));
        a1.setUpdateTime(now.minusDays(30));

        Announcement a2 = new Announcement();
        a2.setTitle("交易安全提醒");
        a2.setContent("温馨提示：\n1. 建议选择校内公共场所进行面对面交易；\n2. 贵重物品交易请当场验货确认；\n3. 谨防先款后货等诈骗行为；\n4. 发现可疑行为请及时举报。");
        a2.setIsActive(true);
        a2.setCreateTime(now.minusDays(20));
        a2.setUpdateTime(now.minusDays(20));

        Announcement a3 = new Announcement();
        a3.setTitle("平台功能更新说明");
        a3.setContent("近期平台已上线以下新功能：\n• 失物招领模块：可在平台上发布寻物启事和招领信息；\n• 认领审核流程：失物认领需提交凭证并通过审核；\n• 消息通知系统：交易双方可通过站内消息沟通。\n欢迎体验新功能，如有建议请反馈给管理员。");
        a3.setIsActive(true);
        a3.setCreateTime(now.minusDays(7));
        a3.setUpdateTime(now.minusDays(7));

        announcementMapper.insert(a1);
        announcementMapper.insert(a2);
        announcementMapper.insert(a3);
        log.info("[DataSeeder] 播种 3 条公告");
    }

    private Item buildItem(Long userId, String title, String description,
                           BigDecimal price, BigDecimal originalPrice,
                           String category, String status, LocalDateTime createTime) {
        Item item = new Item();
        item.setUserId(userId);
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        item.setOriginalPrice(originalPrice);
        item.setCategory(category);
        item.setStatus(status);
        item.setViewCount(0);
        item.setCreateTime(createTime);
        item.setUpdateTime(createTime);
        return item;
    }

    private Long getUserId(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }
}