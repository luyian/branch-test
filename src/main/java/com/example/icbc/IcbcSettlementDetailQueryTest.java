package com.example.icbc;

import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.IcbcApiException;
import com.icbc.api.IcbcConstants;
import com.icbc.api.request.SettlementAccountDetailQueryRequestV1;
import com.icbc.api.request.SettlementAccountDetailQueryRequestV1.SettlementAccountDetailQueryRequestV1Biz;
import com.icbc.api.response.SettlementAccountDetailQueryResponseV1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 工行 - 结算账户交易明细查询接口测试
 * <p>
 * 接口路径：/api/settlement/account/detail/V1/query
 * 接口名称：结算账户交易明细查询
 * 功能说明：查询我行个人结算账户交易明细，查询条件为卡号、开始日期、结束日期，
 * 结果按交易时间倒序排序，单次最多返回 10 条记录。
 * <p>
 * 测试数据：
 * - 一类卡号：6222030200000296172
 * - 二类卡号：6214761102614096836
 * - 户名：工真啼
 * - 证件号：217951196403051104
 * - 手机号：13581780252
 *
 * @author liuyinian
 */
public class IcbcSettlementDetailQueryTest {

    /**
     * 应用编号（AppId）
     */
    private static final String APP_ID = "11000000000000032701";

    /**
     * 合作方 SM2 私钥（API 接口签名用）
     */
    private static final String MY_PRIVATE_KEY =
            "2d2a9883fe2c96265b3894aef9a2ec234240dc1f558dfb0937989a9a2a0896bb";

    /**
     * 工行 SM2 公钥（验签用）
     */
    private static final String APIGW_PUBLIC_KEY =
            "04AB236CE90F95D183039ABC90128FF35B69E93ADB147C6A290F7956287113DF4E20916CFF44709C57B512A01444E1DB9CD823BEFD10F02119FDAC2391A36FA8CB";

    /**
     * 对称秘钥（用于卡号加密及 mediumIdHash 计算），来源于测试参数文档 AES_KEY
     */
    private static final String SECRET_KEY = "FtFc/mN0jtJvhf4eG6RNPQ==";

    /**
     * 测试服务器网关地址
     */
    private static final String GATEWAY = "https://apipcs4.dccnet.com.cn";

    /**
     * 结算账户交易明细查询接口路径
     */
    private static final String SERVICE_URL =
            GATEWAY + "/api/settlement/account/detail/V1/query";

    /**
     * 合作方机构编号
     * TODO 替换为真实的合作方机构编号（由工行分配）
     */
    private static final String CORP_NO = "TODO_CORP_NO";

    /**
     * 待查询的卡号（联名卡 / 一类卡 / 二类卡）
     */
    private static final String MEDIUM_ID = "6222030200000296172";

    /**
     * 外部服务代码
     */
    private static final String OUT_SERVICE_CODE = "querydetail";

    /**
     * 币种：1-人民币
     */
    private static final int CCY_RMB = 1;

    public static void main(String[] args) {
        DefaultIcbcClient client = buildClient();
        // 默认查询近 30 天的交易明细
        String beginDate = daysAgo(30);
        String endDate = currentDate();
        querySettlementDetail(client, MEDIUM_ID, beginDate, endDate);
    }

    /**
     * 构造工行 SDK 客户端，使用 SM2 签名
     */
    private static DefaultIcbcClient buildClient() {
        return new DefaultIcbcClient(
                APP_ID,
                IcbcConstants.SIGN_TYPE_SM2,
                MY_PRIVATE_KEY,
                IcbcConstants.CHARSET_UTF8,
                IcbcConstants.FORMAT_JSON,
                APIGW_PUBLIC_KEY,
                null,
                null,
                null,
                null);
    }

    /**
     * 调用结算账户交易明细查询接口
     *
     * @param client    工行 SDK 客户端
     * @param mediumId  待查询卡号
     * @param beginDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     */
    private static void querySettlementDetail(DefaultIcbcClient client, String mediumId,
                                              String beginDate, String endDate) {
        System.out.println("===== 结算账户交易明细查询 =====");
        System.out.println("查询卡号  ：" + mediumId);
        System.out.println("查询区间  ：" + beginDate + " ~ " + endDate);

        SettlementAccountDetailQueryRequestV1 request = new SettlementAccountDetailQueryRequestV1();
        request.setServiceUrl(SERVICE_URL);

        SettlementAccountDetailQueryRequestV1Biz bizContent = new SettlementAccountDetailQueryRequestV1Biz();
        bizContent.setCorpNo(CORP_NO);
        bizContent.setCorpSerno(generateCorpSerno());
        bizContent.setCorpDate(currentDate());
        bizContent.setTrxAccDate(currentDate());
        bizContent.setTrxAccTime(currentTime());
        bizContent.setOutServiceCode(OUT_SERVICE_CODE);
        bizContent.setMediumId(mediumId);
        bizContent.setCcy(CCY_RMB);
        bizContent.setBeginDate(beginDate);
        bizContent.setEndDate(endDate);
        // 首次查询固定送 1
        bizContent.setQueryMode(1);
        bizContent.setPage(1);
        // 翻页字段：首次查询不送，翻页时按规则赋值（pn_busidate / pn_rowRecord）
        bizContent.setPnBusidate("");
        bizContent.setPnRowRecord("");
        // 对称秘钥：来自测试参数文档 AES_KEY
        bizContent.setSecretKey(SECRET_KEY);
        // 卡号 hash 值，按工行接入规范根据 secretKey 对 mediumId 计算得到
        // TODO 如接口要求必填，需按工行《关于签名和验签的说明》中卡号 hash 算法计算
        bizContent.setMediumIdHash("");
        request.setBizContent(bizContent);

        try {
            String msgId = generateMsgId();
            System.out.println("msgId     ：" + msgId);
            SettlementAccountDetailQueryResponseV1 response = client.execute(request, msgId);
            printResponse(response);
        } catch (IcbcApiException e) {
            System.err.println("调用失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 打印响应内容
     */
    private static void printResponse(SettlementAccountDetailQueryResponseV1 response) {
        System.out.println("isSuccess : " + response.isSuccess());
        System.out.println("returnCode: " + response.getReturnCode());
        System.out.println("returnMsg : " + response.getReturnMsg());
        if (response.isSuccess() && response.getReturnCode() == 0) {
            System.out.println("recordNum : " + response.getRecordNum());
            System.out.println("orderDetail: " + response.getOrderDetail());
        }
        System.out.println("response  : " + response);
    }

    /**
     * 生成消息流水号（msgId），全应用唯一
     */
    private static String generateMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成合作方交易流水号
     */
    private static String generateCorpSerno() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 当前日期 yyyy-MM-dd
     */
    private static String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 当前时间 HH:mm:ss
     */
    private static String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * N 天前的日期 yyyy-MM-dd
     */
    private static String daysAgo(int days) {
        long millis = System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000;
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(millis));
    }
}
