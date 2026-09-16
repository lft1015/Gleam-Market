package com.shiguang.market.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.mapper.AnnouncementMapper;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.mapper.ClaimMapper;
import com.shiguang.market.favorite.entity.Favorite;
import com.shiguang.market.favorite.mapper.FavoriteMapper;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.message.entity.Conversation;
import com.shiguang.market.message.entity.Message;
import com.shiguang.market.message.mapper.ConversationMapper;
import com.shiguang.market.message.mapper.MessageMapper;
import com.shiguang.market.report.entity.Report;
import com.shiguang.market.report.mapper.ReportMapper;
import com.shiguang.market.review.entity.Review;
import com.shiguang.market.review.mapper.ReviewMapper;
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
    private final ReportMapper reportMapper;
    private final ReviewMapper reviewMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final FavoriteMapper favoriteMapper;

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
            seedReports();
            seedReviews();
            seedConversations();
            seedFavorites();
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
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        // ON_SALE x7（覆盖所有分类）
        insertItem(u1, "九成新机械键盘 Cherry MX 青轴",
                "自用 Cherry MX Board 3.0S 机械键盘，青轴，使用不到半年，按键灵敏无坏轴，包装齐全。",
                "299.00", "599.00", "数码电子", "ON_SALE", now.minusDays(3));
        insertItem(u1, "《算法导论》第三版 几乎全新",
                "计算机经典教材，买来只看了一章，几乎全新无笔记，送电子版习题答案。",
                "45.00", "128.00", "图书教材", "ON_SALE", now.minusDays(5));
        insertItem(u2, "宿舍用小冰箱 50L 静音节能",
                "50L 小型冰箱，适合宿舍使用，一级能效，运行安静，制冷效果好，毕业转让。",
                "199.00", "399.00", "生活用品", "ON_SALE", now.minusDays(2));
        insertItem(u2, "Nike Air Force 1 白色 42码 仅试穿",
                "正品 Nike AF1 纯白，42码，买大半码仅试穿一次，鞋盒吊牌齐全。",
                "399.00", "799.00", "服饰鞋包", "ON_SALE", now.minusDays(7));
        insertItem(u3, "尤尼克斯羽毛球拍 纳米速攻系列",
                "Yonex 纳米速攻系列球拍，已拉线 24 磅，附带原装拍套，成色 85 新。",
                "150.00", "380.00", "运动户外", "ON_SALE", now.minusDays(4));
        insertItem(u3, "国誉活页笔记本套装 B5 全新未拆",
                "Kokuyo 国誉活页本 B5 尺寸，内含 100 页替芯，全新未拆封，多色可选。",
                "25.00", "49.00", "文具办公", "ON_SALE", now.minusDays(1));
        insertItem(u1, "小米充电宝 20000mAh 快充版",
                "小米移动电源 20000mAh，支持 22.5W 快充，Type-C 双向快充，循环次数少。",
                "69.00", "149.00", "其他", "ON_SALE", now.minusDays(6));

        // DRAFT - 草稿
        insertItem(u2, "Huawei FreeBuds Pro 无线耳机 银色",
                "华为 FreeBuds Pro 主动降噪耳机，银色款，使用约一年，续航正常，配件齐全。",
                "350.00", "899.00", "数码电子", "DRAFT", now.minusDays(1));

        // PENDING_REVIEW - 待审核
        insertItem(u3, "二手 Switch 游戏机 续航版 红蓝",
                "日版 Switch 续航版，红蓝手柄，屏幕贴膜，原装底座+充电器，无漂移。",
                "1200.00", "2100.00", "数码电子", "PENDING_REVIEW", now.minusDays(2));

        // REJECTED - 审核驳回
        insertItem(u1, "自用 iPhone 13 128GB 午夜色",
                "国行 iPhone 13 午夜色 128GB，屏幕有轻微划痕，电池健康 87%，无维修史。",
                "2800.00", "5999.00", "数码电子", "REJECTED", now.minusDays(8));

        // TRADING - 交易中
        insertItem(u2, "Dji Mini 2 无人机 畅飞套装",
                "大疆 Mini 2 畅飞套装，3 块电池，累计飞行不到 20 次，无炸机，箱说全。",
                "1800.00", "3399.00", "数码电子", "TRADING", now.minusDays(10));

        // SOLD - 已售出
        insertItem(u1, "Sony WH-1000XM4 头戴降噪耳机 黑色",
                "索尼 XM4 降噪耳机，黑色，音质完美降噪出色，因升级 XM5 转让。",
                "800.00", "2299.00", "数码电子", "SOLD", now.minusDays(15));

        // OFF_SHELF - 已下架
        insertItem(u3, "MacBook Pro 2020 M1 8+256 深空灰",
                "M1 MacBook Pro 8G+256G，深空灰，屏幕完美键盘无油光，适配器+包装盒齐全。",
                "4500.00", "9999.00", "数码电子", "OFF_SHELF", now.minusDays(20));

        log.info("[DataSeeder] 播种 13 条商品（ON_SALE*7 + 6种其他状态）");
    }

    private void insertItem(Long userId, String title, String description,
                            String price, String originalPrice, String category,
                            String status, LocalDateTime createTime) {
        Item item = new Item();
        item.setUserId(userId);
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(new BigDecimal(price));
        item.setOriginalPrice(new BigDecimal(originalPrice));
        item.setCategory(category);
        item.setStatus(status);
        item.setViewCount(0);
        item.setCreateTime(createTime);
        item.setUpdateTime(createTime);
        itemMapper.insert(item);
    }

    // ==================== 失物招领 ====================

    private void seedLostFoundItems() {
        if (lostFoundMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 失物招领已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        // PENDING x3
        insertLostFound(u1, "寻找黑色双肩包 图书馆三楼遗忘",
                "6月15日下午在图书馆三楼自习区遗忘一个黑色瑞士军刀双肩包，内有教材和文具，捡到请联系。",
                "LOST", "图书馆三楼自习区", now.minusDays(3),
                "QQ: 1234567890", "PENDING");
        insertLostFound(u2, "捡到白色 AirPods 充电盒",
                "在食堂二楼捡到一个白色 AirPods 充电盒（不含耳机），请失主联系确认后归还。",
                "FOUND", "食堂二楼", now.minusDays(1),
                "微信: xiaohong_wx", "PENDING");
        insertLostFound(u1, "丢失校园卡 学号 2021XXXXXX",
                "在教学楼 B 区附近丢失校园卡一张，卡号后四位 8823，捡到请联系，非常感谢！",
                "LOST", "教学楼 B 区", now.minusDays(2),
                "电话: 13800000001", "PENDING");

        // PENDING_REVIEW - 待初审
        insertLostFound(u3, "丢失银色 MacBook 充电器 61W",
                "在图书馆二楼电脑区遗忘一个苹果原装 61W USB-C 充电器，银色，有使用痕迹。",
                "LOST", "图书馆二楼电脑区", now.minusDays(5),
                "电话: 13800000003", "PENDING_REVIEW");

        // REJECTED - 审核驳回
        insertLostFound(u2, "寻找二手课本《高等数学》同济版",
                "丢了一本高等数学第七版上册，封面有点破旧，里面有大量手写笔记。",
                "LOST", "教学楼 C 区", now.minusDays(12),
                "QQ: 555666777", "REJECTED");

        // IN_PROGRESS - 进行中
        insertLostFound(u1, "丢失蓝牙鼠标 Logitech MX Master 3",
                "上周在实验室遗忘一只罗技 MX Master 3 无线鼠标，黑色，自定义按键已设置。",
                "LOST", "实验楼 501", now.minusDays(6),
                "微信: xiaoming_wx", "IN_PROGRESS");

        // PROCESSING x2
        insertLostFound(u2, "丢失 Kindle Paperwhite 电子书阅读器",
                "上周五在图书馆四楼阅览室忘记带走一台 Kindle Paperwhite 第四代，黑色皮质保护套，内有大量专业书籍。",
                "LOST", "图书馆四楼阅览室", now.minusDays(7),
                "QQ: 9876543210", "PROCESSING");
        insertLostFound(u1, "捡到一串钥匙 带 U 盘",
                "在教学楼 A 区走廊捡到一串钥匙，上面挂有一个 32GB 金士顿 U 盘和一个小熊挂件。",
                "FOUND", "教学楼 A 区走廊", now.minusDays(4),
                "微信: xiaoming_wx", "PROCESSING");

        // FOUND - 已找回
        insertLostFound(u2, "丢失水杯 蓝色保温杯 500ml",
                "在田径场看台遗忘一个蓝色膳魔师保温杯 500ml，已找回，感谢捡到的同学！",
                "LOST", "田径场看台", now.minusDays(10),
                "电话: 13800000002", "FOUND");

        // RETURNED - 已归还
        insertLostFound(u3, "捡到校园卡 计算机学院 李同学",
                "在食堂一楼捡到一张校园卡，计算机学院李某，已联系到失主并归还。",
                "FOUND", "食堂一楼", now.minusDays(20),
                "微信: xiaogang_wx", "RETURNED");

        // CLOSED - 已关闭
        insertLostFound(u1, "寻找灰色折叠伞 天堂牌",
                "下雨天在食堂门口拿错了一把灰色天堂折叠伞，自己的那把被他人拿走，已放弃寻找。",
                "LOST", "食堂门口", now.minusDays(14),
                "QQ: 111222333", "CLOSED");

        log.info("[DataSeeder] 播种 12 条失物招领（覆盖全部 8 种状态）");
    }

    private void insertLostFound(Long userId, String title, String description,
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
        lostFoundMapper.insert(lf);
    }

    // ==================== 认领 ====================

    private void seedClaims() {
        if (claimMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 认领记录已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);

        LostFound airpods = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "捡到白色 AirPods 充电盒"));
        LostFound keys = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "捡到一串钥匙 带 U 盘"));
        LostFound kindle = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "丢失 Kindle Paperwhite 电子书阅读器"));

        if (airpods != null) {
            Claim c = new Claim();
            c.setLostFoundId(airpods.getId());
            c.setClaimantId(u1);
            c.setMessage("我的 AirPods 充电盒，背面有一道小划痕，可以拍照片确认。");
            c.setContact("电话: 13800000001");
            c.setVerification("可提供购买记录和序列号");
            c.setStatus("PENDING");
            c.setCreateTime(now.minusHours(12));
            c.setUpdateTime(now.minusHours(12));
            claimMapper.insert(c);
        }

        if (keys != null) {
            Claim c = new Claim();
            c.setLostFoundId(keys.getId());
            c.setClaimantId(u2);
            c.setMessage("那串钥匙是我的，U 盘里有我的课程作业文件，挂件是女朋友送的生日礼物。");
            c.setContact("微信: xiaohong_wx");
            c.setVerification("可当场说出 U 盘内文件内容");
            c.setStatus("APPROVED");
            c.setCreateTime(now.minusDays(3));
            c.setUpdateTime(now.minusDays(2));
            claimMapper.insert(c);
        }

        if (kindle != null) {
            Claim c = new Claim();
            c.setLostFoundId(kindle.getId());
            c.setClaimantId(u1);
            c.setMessage("我的 Kindle 是 Paperwhite 第四代，黑色保护套，里面有一本《深入理解计算机系统》的电子书。");
            c.setContact("QQ: 1234567890");
            c.setVerification("无法提供购买凭证");
            c.setStatus("REJECTED");
            c.setCreateTime(now.minusDays(5));
            c.setUpdateTime(now.minusDays(4));
            claimMapper.insert(c);
        }

        log.info("[DataSeeder] 播种 3 条认领记录（PENDING/APPROVED/REJECTED）");
    }

    // ==================== 举报 ====================

    private void seedReports() {
        if (reportMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 举报记录已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        Item cheapItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "《算法导论》第三版 几乎全新"));
        Item soldItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "Sony WH-1000XM4 头戴降噪耳机 黑色"));
        LostFound backpack = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "寻找黑色双肩包 图书馆三楼遗忘"));

        // 举报商品 - 待处理 低风险
        if (cheapItem != null) {
            insertReport(u1, "ITEM", cheapItem.getId(),
                    "价格异常", "LOW", "该商品实际成色与描述不符，价格虚高。",
                    "PENDING", now.minusDays(1));
        }

        // 举报商品 - 待处理 中风险
        if (soldItem != null) {
            insertReport(u3, "ITEM", soldItem.getId(),
                    "疑似诈骗", "MEDIUM", "卖家要求先付款再发货，被举报后商品已售出。",
                    "PENDING", now.minusDays(4));
        }

        // 举报失物招领 - 待处理 高风险
        if (backpack != null) {
            insertReport(u2, "LOST_FOUND", backpack.getId(),
                    "虚假信息", "HIGH", "该失物招领信息疑似与多起诈骗案关联，请管理员核查。",
                    "PENDING", now.minusDays(2));
        }

        // 举报用户 - 已处理
        insertReport(u3, "USER", u2,
                "骚扰行为", "MEDIUM", "该用户多次发送骚扰私信，已处理警告。",
                "RESOLVED", now.minusDays(10));

        // 举报商品 - 已忽略
        Item nikeItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "Nike Air Force 1 白色 42码 仅试穿"));
        if (nikeItem != null) {
            insertReport(u1, "ITEM", nikeItem.getId(),
                    "疑似仿冒", "LOW", "举报人怀疑是仿品，经审核确认为正品。",
                    "DISMISSED", now.minusDays(14));
        }

        log.info("[DataSeeder] 播种 5 条举报（3种目标+3种状态+3种风险等级）");
    }

    private void insertReport(Long reporterId, String targetType, Long targetId,
                               String reason, String riskLevel, String description,
                               String status, LocalDateTime createTime) {
        Report r = new Report();
        r.setReporterId(reporterId);
        r.setTargetType(targetType);
        r.setTargetId(targetId);
        r.setReason(reason);
        r.setRiskLevel(riskLevel);
        r.setDescription(description);
        r.setStatus(status);
        r.setCreateTime(createTime);
        r.setUpdateTime(createTime);
        reportMapper.insert(r);
    }

    // ==================== 审核 ====================

    private void seedReviews() {
        if (reviewMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 审核记录已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        // ITEM - PENDING
        Item pendingItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "二手 Switch 游戏机 续航版 红蓝"));
        if (pendingItem != null) {
            Review r = new Review();
            r.setTargetType("ITEM");
            r.setTargetId(pendingItem.getId());
            r.setSubmitterId(u3);
            r.setStatus("PENDING");
            r.setCreateTime(now.minusDays(2));
            r.setUpdateTime(now.minusDays(2));
            reviewMapper.insert(r);
        }

        // ITEM - APPROVED
        Item keyboardItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "九成新机械键盘 Cherry MX 青轴"));
        if (keyboardItem != null) {
            Review r = new Review();
            r.setTargetType("ITEM");
            r.setTargetId(keyboardItem.getId());
            r.setSubmitterId(u1);
            r.setReviewerId(u2);
            r.setStatus("APPROVED");
            r.setReviewNote("商品信息真实，审核通过。");
            r.setCreateTime(now.minusDays(3));
            r.setUpdateTime(now.minusDays(3));
            reviewMapper.insert(r);
        }

        // ITEM - REJECTED
        Item rejectedItem = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "自用 iPhone 13 128GB 午夜色"));
        if (rejectedItem != null) {
            Review r = new Review();
            r.setTargetType("ITEM");
            r.setTargetId(rejectedItem.getId());
            r.setSubmitterId(u1);
            r.setReviewerId(u2);
            r.setStatus("REJECTED");
            r.setReviewNote("商品描述与实际不符，缺少购买凭证，予以驳回。");
            r.setCreateTime(now.minusDays(8));
            r.setUpdateTime(now.minusDays(7));
            reviewMapper.insert(r);
        }

        // LOST_FOUND - PENDING
        LostFound pendingLf = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "丢失银色 MacBook 充电器 61W"));
        if (pendingLf != null) {
            Review r = new Review();
            r.setTargetType("LOST_FOUND");
            r.setTargetId(pendingLf.getId());
            r.setSubmitterId(u3);
            r.setStatus("PENDING");
            r.setCreateTime(now.minusDays(5));
            r.setUpdateTime(now.minusDays(5));
            reviewMapper.insert(r);
        }

        // LOST_FOUND - APPROVED
        LostFound processingLf = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "丢失 Kindle Paperwhite 电子书阅读器"));
        if (processingLf != null) {
            Review r = new Review();
            r.setTargetType("LOST_FOUND");
            r.setTargetId(processingLf.getId());
            r.setSubmitterId(u2);
            r.setReviewerId(u3);
            r.setStatus("APPROVED");
            r.setReviewNote("信息完整，审核通过进入处理流程。");
            r.setCreateTime(now.minusDays(7));
            r.setUpdateTime(now.minusDays(7));
            reviewMapper.insert(r);
        }

        // LOST_FOUND - REJECTED
        LostFound rejectedLf = lostFoundMapper.selectOne(new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getTitle, "寻找二手课本《高等数学》同济版"));
        if (rejectedLf != null) {
            Review r = new Review();
            r.setTargetType("LOST_FOUND");
            r.setTargetId(rejectedLf.getId());
            r.setSubmitterId(u2);
            r.setReviewerId(u3);
            r.setStatus("REJECTED");
            r.setReviewNote("信息不完整，未提供具体丢失时间和特征描述，请补充后重新提交。");
            r.setCreateTime(now.minusDays(12));
            r.setUpdateTime(now.minusDays(11));
            reviewMapper.insert(r);
        }

        log.info("[DataSeeder] 播种 6 条审核记录（ITEM+LOST_FOUND × PENDING/APPROVED/REJECTED）");
    }

    // ==================== 会话 & 消息 ====================

    private void seedConversations() {
        if (conversationMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 会话记录已存在，跳过播种");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        Item keyboard = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "九成新机械键盘 Cherry MX 青轴"));
        Item fridge = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "宿舍用小冰箱 50L 静音节能"));

        // 会话1: testuser1 ↔ testadmin 关于机械键盘
        Conversation conv1 = new Conversation();
        conv1.setItemId(keyboard != null ? keyboard.getId() : null);
        conv1.setUser1Id(u1);
        conv1.setUser2Id(u2);
        conv1.setLastMessage("好的，那明天下午三点图书馆门口见？");
        conv1.setLastTime(now.minusHours(2));
        conv1.setUnreadCount(0);
        conv1.setCreateTime(now.minusDays(1));
        conv1.setUpdateTime(now.minusHours(2));
        conversationMapper.insert(conv1);

        // 会话2: testuser1 ↔ testsuperadmin 关于小冰箱
        Conversation conv2 = new Conversation();
        conv2.setItemId(fridge != null ? fridge.getId() : null);
        conv2.setUser1Id(u1);
        conv2.setUser2Id(u3);
        conv2.setLastMessage("还在吗？我想买这个小冰箱。");
        conv2.setLastTime(now.minusHours(5));
        conv2.setUnreadCount(1);
        conv2.setCreateTime(now.minusDays(3));
        conv2.setUpdateTime(now.minusHours(5));
        conversationMapper.insert(conv2);

        // 消息 - 会话1
        insertMessage(conv1.getId(), u1, u2, "你好，请问机械键盘还在吗？", "TEXT", true, now.minusDays(1));
        insertMessage(conv1.getId(), u2, u1, "在的，九成新，用起来很舒服。", "TEXT", true, now.minusDays(1).plusHours(1));
        insertMessage(conv1.getId(), u1, u2, "能便宜点吗？280 可以吗？", "TEXT", true, now.minusDays(1).plusHours(2));
        insertMessage(conv1.getId(), u2, u1, "最低 290，不能再低了。", "TEXT", true, now.minusHours(3));
        insertMessage(conv1.getId(), u1, u2, "行，我要了。哪里交易方便？", "TEXT", true, now.minusHours(3).plusMinutes(30));
        insertMessage(conv1.getId(), u2, u1, "好的，那明天下午三点图书馆门口见？", "TEXT", true, now.minusHours(2));

        // 消息 - 会话2
        insertMessage(conv2.getId(), u1, u3, "你好，这个小冰箱还在吗？", "TEXT", true, now.minusDays(3));
        insertMessage(conv2.getId(), u3, u1, "在的，制冷效果很好，适合宿舍用。", "TEXT", true, now.minusDays(2));
        insertMessage(conv2.getId(), u1, u3, "还在吗？我想买这个小冰箱。", "TEXT", false, now.minusHours(5));

        log.info("[DataSeeder] 播种 2 个会话 + 9 条消息");
    }

    private void insertMessage(Long convId, Long senderId, Long receiverId,
                                String content, String type, boolean isRead, LocalDateTime time) {
        Message m = new Message();
        m.setConversationId(convId);
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setContent(content);
        m.setType(type);
        m.setIsRead(isRead);
        m.setCreateTime(time);
        messageMapper.insert(m);
    }

    // ==================== 收藏 ====================

    private void seedFavorites() {
        if (favoriteMapper.selectCount(null) > 0) {
            log.info("[DataSeeder] 收藏记录已存在，跳过播种");
            return;
        }

        Long u1 = getUserId(TEST_USERNAMES[0]);
        Long u2 = getUserId(TEST_USERNAMES[1]);
        Long u3 = getUserId(TEST_USERNAMES[2]);

        Item fridge = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "宿舍用小冰箱 50L 静音节能"));
        Item shuttlecock = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "尤尼克斯羽毛球拍 纳米速攻系列"));
        Item keyboard = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "九成新机械键盘 Cherry MX 青轴"));
        Item book = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "《算法导论》第三版 几乎全新"));
        Item powerBank = itemMapper.selectOne(new LambdaQueryWrapper<Item>()
                .eq(Item::getTitle, "小米充电宝 20000mAh 快充版"));

        // testuser1 收藏了 3 个商品
        if (fridge != null) insertFavorite(u1, fridge.getId(), LocalDateTime.now().minusDays(5));
        if (shuttlecock != null) insertFavorite(u1, shuttlecock.getId(), LocalDateTime.now().minusDays(3));
        if (keyboard != null) insertFavorite(u1, keyboard.getId(), LocalDateTime.now().minusDays(1));

        // testadmin 收藏了 2 个商品
        if (book != null) insertFavorite(u2, book.getId(), LocalDateTime.now().minusDays(4));
        if (powerBank != null) insertFavorite(u2, powerBank.getId(), LocalDateTime.now().minusDays(2));

        // testsuperadmin 收藏了 1 个商品
        if (shuttlecock != null) insertFavorite(u3, shuttlecock.getId(), LocalDateTime.now().minusDays(6));

        log.info("[DataSeeder] 播种 6 条收藏记录（3 个用户）");
    }

    private void insertFavorite(Long userId, Long itemId, LocalDateTime time) {
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setItemId(itemId);
        f.setCreateTime(time);
        favoriteMapper.insert(f);
    }

    // ==================== 公告 ====================

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

        // 已过期公告
        Announcement a4 = new Announcement();
        a4.setTitle("【已过期】五一假期平台维护通知");
        a4.setContent("五一假期期间（5月1日-5月5日），平台将进行系统升级维护，部分功能可能暂时无法使用，给您带来的不便敬请谅解。");
        a4.setIsActive(false);
        a4.setCreateTime(now.minusDays(140));
        a4.setUpdateTime(now.minusDays(140));

        announcementMapper.insert(a1);
        announcementMapper.insert(a2);
        announcementMapper.insert(a3);
        announcementMapper.insert(a4);
        log.info("[DataSeeder] 播种 4 条公告（3 条有效 + 1 条已过期）");
    }

    private Long getUserId(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }
}