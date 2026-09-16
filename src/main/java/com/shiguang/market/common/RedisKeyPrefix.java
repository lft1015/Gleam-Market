package com.shiguang.market.common;

/**
 * Redis Key 前缀常量
 *
 * @author gugu
 */
public final class RedisKeyPrefix {

    /** Token 黑名单前缀 */
    public static final String TOKEN_BLACKLIST = "token:blacklist:";

    /** 商品详情缓存前缀 */
    public static final String ITEM_DETAIL = "item:detail:";

    /** 商品浏览量前缀 */
    public static final String ITEM_VIEW_COUNT = "item:view:";

    /** 缓存空值（防穿透） */
    public static final String NULL_PLACEHOLDER = "__NULL__";

    /** 商品详情缓存过期时间：10 分钟 */
    public static final long ITEM_DETAIL_TTL_SECONDS = 600;

    /** 空值缓存过期时间：1 分钟（防穿透） */
    public static final long NULL_TTL_SECONDS = 60;

    private RedisKeyPrefix() {}
}