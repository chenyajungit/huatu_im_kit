1
           智能机器人Android端SDK使用文档V1.0
1 智能机器人SDK是什么?
答:在融云提供的IM通讯功能的基础上进行再改造优化,使得智能机器人SDK支持用户自定义各种模板,从而满足多样化的功能需求。
2 怎么集成?
答:
(1)智能机器人最新源码地址

对项目涉及IM部分有特殊定制化需求,可更新该代码在其基础上进行修改更新
(2)
步骤一:
通过依赖方式更新使用
在build.gradle中添加依赖:
implementation 'com.github.chenyajungit:huatu_im_kit:tag'
tag可通过https://github.com/chenyajungit/huatu_im_kit查看最新版本

步骤二:
新建类继承OATextMessageItemProvider(自定义模板使用此类来实现)类,其中需要实现的方法如下:
//将文字消息类型的View直接传出去,这样方便二次开发
public abstract void textViewMessageListener(View view);
/**
 * @param type        点击的表单项内容 1副标题  2表单项  3按钮项
 * @param hrefType    链接类型 1 H5  2 原生
 * @param hrefContent 链接参数
 */
public abstract void templateClickListener(int type, int hrefType, String hrefContent) ;

/**
 *
 * @param pdfUrl pdf的url
 */
public abstract void clickPdfListener(String pdfUrl) ;

/**
 *
 * @param webUrl 网页链接
 */
public abstract void commonListener(String webUrl) ;

/**
 *
 * @param view 长按对应的View
 * @param messageId 长按对应的消息id
 */
public abstract void commonTextMessageLongClickListener(View view,Integer messageId) ;

/**
 *
 * @param view 点击对应的View
 * @param messageId 对应的消息id
 */
public abstract void commonTextMessageClickListener(View view,Integer messageId) ;

继承类头部需添加该标识
@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true
)


步骤三:
自定义Application在其onCreate()方法中进行智能机器人初始化

String appKey = "6tnym1br64wq7";
RongHelper rongHelper=new RongHelper();
/**
 *
 * @param context
 * @param esbAppId  esb的appId与secret,用来进行模板管理的,可联系南京esb相关开发老师进行配置
 * @param esbSecret esb的appId与secret,用来进行模板管理的,可联系南京esb相关开发老师进行配置
 * @param inteval   每两次推送欢迎语之间,间隔的分钟数。需要推送时，此字段必填
 * @param pushFlag  是否在查询的同时，直接推送欢迎语
 * @param isESBProductionEnvironment  是否是esb生产环境,是则为true,测试环境则为false
 */

rongHelper.configEsb(SdkApplication.this,"10001004","asdfasdfasd",2,true,false);
rongHelper.rongInit(SdkApplication.this, appKey, true, new RongHelper.InitImListener() {
    @Override
    public void doBeforeInit() {

    }

    @Override
    public void doAfterInit() {
        //在此可以设置自定义模板消息,其中文字消息,需要实现OATextMessageItemProvider类
        RongIM.registerMessageTemplate(new OATextDemoMessageItemProvider());
    }
});

步骤四:
建立会话列表页面






在AndroidManifest.xml进行配置

通过如上配置与开发,最基础的智能机器人SDK使用已搭建完毕,效果如下

注:需看比较细致的配置信息可通过https://github.com/chenyajungit/huatu_im_kit
更新最新代码查看app主项目中配置即可。

对于智能机器人使用和配置有相关问题需要沟通,可联系南京研发部安卓开发陈亚军
微信号:henrychen1234
手机号:18512150753
