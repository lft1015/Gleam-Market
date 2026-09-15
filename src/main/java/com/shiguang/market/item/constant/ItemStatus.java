package com.shiguang.market.item.constant;

/**
 * 商品状态常量
 *
 * @author gugu
 */
public final class ItemStatus {

    private ItemStatus() {}

    /** 草稿 */
    public static final String DRAFT = "DRAFT";

    /** 审核中 */
    public static final String REVIEWING = "REVIEWING";

    /** 在售 */
    public static final String ON_SALE = "ON_SALE";

    /** 交易中 */
    public static final String TRADING = "TRADING";

    /** 已售出 */
    public static final String SOLD = "SOLD";

    /** 已下架 */
    public static final String DELISTED = "DELISTED";
}
