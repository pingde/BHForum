package com.baiheplayer.bbs.common;

/**
 * 管理网络请求
 * Created by Administrator on 2016/11/22.
 */

public class HttpDatas {

    public static String BASE_URI = "https://dev.baiheplayer.com/api/app-bbs";    //
//    public static String BASE_URI = "http://192.168.0.120:8080/api/app-bbs";    //
    /**
     * 获取Token
     */
    public static String ACCESS_TOKEN = BASE_URI + "/token/access_token";
    /**
     * 刷新Token
     */
    public static String REFRESH_TOKEN = BASE_URI + "/token/refresh_token";
    /*************************************************************************************
     *
     * 其他
     *************************************************************************************/
    /**
     * 发送验证短信
     */
    public static String SEND_SMS = BASE_URI + "/ot/send_verification_code";
    /**
     * 获得位置
     */
    public static String GET_LOCATION = BASE_URI + "/ot/area";
    /**
     * 获取Banner图
     */
    public static String GET_BANNER = BASE_URI + "/ot/banner";
    /**
     * Banner图点击统计
     */
    public static String BANNER_CLICK = BASE_URI + "/ot/banner_click";
    /**
     * 上传文件
     **/
    public static String UP_LOAD = BASE_URI + "/ot/upload";
    /**
     * 版本信息检查
     **/
    public static String VERSION_CHECK = BASE_URI + "/ot/check_update";


    /***********************************************************************************
     *
     * 用户
     ************************************************************************************/
    /**
     * 用户登录
     */
    public static String USER_LOGIN = BASE_URI + "/user/login";
    /**
     * 用户退出
     */
    public static String USER_LOGOUT = BASE_URI + "/user/logout";
    /**
     * 用户注册
     */
    public static String USER_REGISTER = BASE_URI + "/user/register";
    /**
     * 找回密码
     */
    public static String USER_FINDPWD = BASE_URI + "/user/findpwd";
    /**
     * 重置密码
     */
    public static String USER_RESETPWD = BASE_URI + "/user/resetpwd";
    /**
     * 获取用户信息
     */
    public static String USER_INFO = BASE_URI + "/user/info";
    /**
     * 更新用户信息
     */
    public static String UPDATE_INFO = BASE_URI + "/user/update_info";
    /**
     * 绑定手机号&邮箱
     */
    public static String BIND_ACCOUNT = BASE_URI + "/user/bind";
    /**
     * 新增收货地址
     */
    public static String ADD_ADDRESS = BASE_URI + "/user/address/add";
    /**
     * 更新收货地址
     */
    public static String UPDATE_ADDRESS = BASE_URI + "/user/address/update";
    /**
     * 移除收货地址
     */
    public static String REMOVE_ADDRESS = BASE_URI + "/user/address/remove";
    /**
     * 默认收货地址
     */
    public static String DEFAULT_ADDRESS = BASE_URI + "/user/address/set_default";

    /************************************************************************************
     *
     * 热点资讯 NEWS
     ***********************************************************************************/
    /**
     * 热点资讯
     */
    public static String HOT_NEWS = BASE_URI + "/hot_news";
    /**
     * 热点推荐
     */
    public static String HOT_TOPIC = BASE_URI + "/hot_topic";

    /************************************************************************************
     * 论坛首页
     * 帖子TOPIC
     ***********************************************************************************/
    /**
     * 帖子列表
     */
    public static String GET_TOPIC = BASE_URI + "/topic/index";
    /**
     * 帖子详情
     */
    public static String TOPIC_DETIAL = BASE_URI + "/topic/detail";
    /**
     * 发布帖子
     */
    public static String POST_TOPIC = BASE_URI + "/topic/add";
    /**
     * 分享帖子
     */
    public static String SHARE_TOPIC = BASE_URI + "/topic/share";
    /**
     * 移除帖子
     */
    public static String REMOVE_TOPIC = BASE_URI + "/topic/remove";
    /**
     * 举报帖子
     */
    public static String REPORT_TOPIC = BASE_URI + "/topic/report";
    /**
     * 收藏帖子
     */
    public static String ADD_TOPIC = BASE_URI + "/topic/favorite/add";
    /**
     * 移除收藏
     */
    public static String REMOVE_STORE = BASE_URI + "/topic/favorite/remove";
    /**
     * 点赞帖子
     */
    public static String FAVOR_TOPIC = BASE_URI + "/topic/praise/add";
    /**
     * 取消点赞
     */
    public static String REMOVE_FAVOR = BASE_URI + "/topic/praise/remove";

    /************************************************************************************
     *
     * 我的
     ***********************************************************************************/
    /**
     * 我的帖子
     */
    public static String MY_LIST = BASE_URI + "/topic/my_list";
    /**
     * 我的收藏
     */
    public static String MY_STORE = BASE_URI + "/topic/my_favorite";
    /**
     * 我的喜欢
     */
    public static String MY_PRAISE = BASE_URI + "/topic/my_praise";
    /**
     * 我的预定
     */
    public static String MY_PRE_ORDER = BASE_URI + "/user/pre_order";
    /**
     * 我的消息
     */
    public static String MY_NEWS = BASE_URI + "/user/message";
    /**
     * 我的通知
     */
    public static String MY_NOTICE = BASE_URI + "/user/notice";
    /**
     * 我的地址
     */
    public static String MY_ADDRESS = BASE_URI + "/user/address/list";
    /**
     * 背景更换
     */
    public static String BG_CHANGE = BASE_URI + "/user/bg_change";
    /**
     * 背景移除
     */
    public static String BG_REMOVE = BASE_URI + "/user/bg_remove";
    /*************************************************************************************
     *
     * 评论
     ***********************************************************************************/
    /**
     * 评论列表
     */
    public static String COMMENT_LIST = BASE_URI + "/topic/comment/list";
    /**
     * 评论详情
     */
    public static String COMMENT_DETAIL = BASE_URI + "/topic/comment/detail";
    /**
     * 添加评论
     */
    public static String COMMENT_ADD = BASE_URI + "/topic/comment/add";
    /**
     * 删除评论
     */
    public static String COMMENT_REMOVE = BASE_URI + "/topic/comment/remove";
    /**
     * 回复评论
     */
    public static String COMMENT_REPLY = BASE_URI + "/topic/comment/reply";
    /**
     * 点赞评论
     */
    public static String COMMENT_PRAISE = BASE_URI + "/topic/comment/add_praise";
    /**
     * 取消点赞评论
     */
    public static String COMMENT_UNPRAISE = BASE_URI + "/topic/comment/remove_praise";


}
