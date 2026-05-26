package com.example.icbc;

import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.IcbcApiException;
import com.icbc.api.IcbcConstants;
import com.icbc.api.request.MybankAccountCorporatewalletBaseinfoqueryRequestV1;
import com.icbc.api.request.MybankAccountCorporatewalletBaseinfoqueryRequestV1.CorporatewalletBaseinfoqueryRequestV1Biz;
import com.icbc.api.request.MybankAccountCorporatewalletDetailqueryRequestV1;
import com.icbc.api.request.MybankAccountCorporatewalletDetailqueryRequestV1.MybankAccountCorporatewalletDetailqueryRequestV1Biz;
import com.icbc.api.response.MybankAccountCorporatewalletBaseinfoqueryResponseV1;
import com.icbc.api.response.MybankAccountCorporatewalletDetailqueryResponseV1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 工行 e 钱包接口测试类（光伏场景）
 * <p>
 * 包含两个接口的调用骨架：
 * 1. e 钱包基本信息查询（含余额）：mybank/account/corporatewallet/baseinfoquery/V1
 * 2. e 钱包明细查询：mybank/account/corporatewallet/detailquery/V1
 * <p>
 * 测试参数来自《e钱包测试参数光伏.docx》，签名算法为 SM2。
 * 使用前请按需填入 AGR_NO（合作方协议号）和 WALLET_ID（钱包ID）。
 *
 * @author liuyinian
 */
public class IcbcEwalletTest {

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
     * 测试服务器地址
     */
    private static final String GATEWAY = "https://apipcs4.dccnet.com.cn";

    /**
     * e 钱包基本信息查询接口路径
     */
    private static final String SERVICE_URL_BASEINFO =
            GATEWAY + "/api/mybank/account/corporatewallet/baseinfoquery/V1";

    /**
     * e 钱包明细查询接口路径
     */
    private static final String SERVICE_URL_DETAIL =
            GATEWAY + "/api/mybank/account/corporatewallet/detailquery/V1";

    /**
     * 合作方协议号
     * TODO 替换为真实的 e 钱包合作方协议号
     */
    private static final String AGR_NO = "TODO_AGR_NO";

    /**
     * 钱包 ID
     * TODO 替换为真实的 e 钱包 ID
     */
    private static final String WALLET_ID = "TODO_WALLET_ID";

    /**
     * 占位 MAC 地址
     */
    private static final String DEFAULT_MAC = "00:00:00:00:00:00";

    /**
     * 占位 IP 地址
     */
    private static final String DEFAULT_IP = "127.0.0.1";

    public static void main(String[] args) {
        DefaultIcbcClient client = buildClient();
        // 1. 基本信息查询（含余额）
        testBaseinfoQuery(client);
        printSeparator();
        // 2. 明细查询
        testDetailQuery(client);
    }

    /**
     * 构造工行 SDK 客户端，使用 SM2 签名
     *
     * @return DefaultIcbcClient 实例
     */
    private static DefaultIcbcClient buildClient() {
        // 构造方法签名：appId, signType, privateKey, charset, format, icbcPublicKey,
        // encryptKey, encryptType, ca, password
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
     * 测试 e 钱包基本信息查询（含余额）
     *
     * @param client 工行 SDK 客户端
     */
    private static void testBaseinfoQuery(DefaultIcbcClient client) {
        System.out.println("===== e 钱包基本信息查询（含余额）=====");
        MybankAccountCorporatewalletBaseinfoqueryRequestV1 request =
                new MybankAccountCorporatewalletBaseinfoqueryRequestV1();
        request.setServiceUrl(SERVICE_URL_BASEINFO);

        CorporatewalletBaseinfoqueryRequestV1Biz bizContent =
                new CorporatewalletBaseinfoqueryRequestV1Biz();
        bizContent.setAgr_no(AGR_NO);
        bizContent.setBus_serialno(generateBusSerialNo());
        bizContent.setWork_date(currentDate());
        bizContent.setWork_time(currentTime());
        bizContent.setMac(DEFAULT_MAC);
        bizContent.setIp(DEFAULT_IP);
        bizContent.setWallet_id(WALLET_ID);
        request.setBizContent(bizContent);

        try {
            String msgId = generateMsgId();
            System.out.println("msgId: " + msgId);
            MybankAccountCorporatewalletBaseinfoqueryResponseV1 response =
                    client.execute(request, msgId);
            printResponse(response.isSuccess(), response.getReturnCode(),
                    response.getReturnMsg(), response);
        } catch (IcbcApiException e) {
            System.err.println("调用失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试 e 钱包明细查询
     *
     * @param client 工行 SDK 客户端
     */
    private static void testDetailQuery(DefaultIcbcClient client) {
        System.out.println("===== e 钱包明细查询 =====");
        MybankAccountCorporatewalletDetailqueryRequestV1 request =
                new MybankAccountCorporatewalletDetailqueryRequestV1();
        request.setServiceUrl(SERVICE_URL_DETAIL);

        MybankAccountCorporatewalletDetailqueryRequestV1Biz bizContent =
                new MybankAccountCorporatewalletDetailqueryRequestV1Biz();
        bizContent.setAgr_no(AGR_NO);
        bizContent.setBus_serialno(generateBusSerialNo());
        bizContent.setWork_date(currentDate());
        bizContent.setWork_time(currentTime());
        bizContent.setMac(DEFAULT_MAC);
        bizContent.setIp(DEFAULT_IP);
        bizContent.setWallet_id(WALLET_ID);
        // 查询区间：默认查最近 30 天
        bizContent.setStrart_date(daysAgo(30));
        bizContent.setStrart_time("00:00:00");
        bizContent.setEnd_date(currentDate());
        bizContent.setEnd_time(currentTime());
        // 借贷标志：1-借（支出），2-贷（收入），空-全部
        bizContent.setLoan_sign("");
        bizContent.setPage_size(10);
        bizContent.setPage_num(1);
        request.setBizContent(bizContent);

        try {
            String msgId = generateMsgId();
            System.out.println("msgId: " + msgId);
            MybankAccountCorporatewalletDetailqueryResponseV1 response =
                    client.execute(request, msgId);
            printResponse(response.isSuccess(), response.getReturnCode(),
                    response.getReturnMsg(), response);
        } catch (IcbcApiException e) {
            System.err.println("调用失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 打印响应结果
     */
    private static void printResponse(boolean success, int returnCode,
                                      String returnMsg, Object response) {
        System.out.println("isSuccess  : " + success);
        System.out.println("returnCode : " + returnCode);
        System.out.println("returnMsg  : " + returnMsg);
        System.out.println("response   : " + response);
    }

    /**
     * 生成消息流水号（msgId），全应用唯一
     */
    private static String generateMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成业务流水号
     */
    private static String generateBusSerialNo() {
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

    private static void printSeparator() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println();
    }
}
