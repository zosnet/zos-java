package com.zos.common.ws.client.graphenej.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Enum type used to keep track of all the operation types and their corresponding ids.
 *
 * <a href="https://bitshares.org/doxygen/operations_8hpp_source.html">Source</a>
 *
 * Created by nelson on 11/6/16.
 */
@AllArgsConstructor
@NoArgsConstructor
public enum OperationType {
    TRANSFER_OPERATION(0,"转账"),
    DIY_OPERATION(35,"自定义操作"),
    DEPOSIT_OPERATION(49,"DEPOSIT"),
    WITHDRAW_OPERATION(48,"WITHDRAW"),
    BROADCAST_STORE_DATA(73,"数据存储广播"),
    ACCOUNT_AUTHENTICATE(100,"ACCOUNT_AUTHENTICATE"),
    LOCKTOKEN_CREATE(107,"LOCKTOKEN_CREATE"),
    LOCKTOKEN_UPDATE(108,"LOCKTOKEN_UPDATE"),
    LOCKTOKEN_REMOVE(109,"LOCKTOKEN_REMOVE"),
    LOCKTOKEN_AWARDS(110,"LOCKTOKEN_AWARDS"),
//    VERTIFY_TRANSACTION(),



    LIMIT_ORDER_CREATE_OPERATION(56,"BITLENDER_LOAN_OPERATION"),
    LIMIT_ORDER_CANCEL_OPERATION(-1,""),
    CALL_ORDER_UPDATE_OPERATION(-1,""),
    FILL_ORDER_OPERATION(-1,""),           // VIRTUAL
    ACCOUNT_CREATE_OPERATION(-1,""),
    ACCOUNT_UPDATE_OPERATION(-1,""),
    ACCOUNT_WHITELIST_OPERATION(-1,""),
    ACCOUNT_UPGRADE_OPERATION(-1,""),
    ACCOUNT_TRANSFER_OPERATION(-1,""),
    ASSET_CREATE_OPERATION(-1,""),
    ASSET_UPDATE_OPERATION(-1,""),
    ASSET_UPDATE_BITASSET_OPERATION(-1,""),
    ASSET_UPDATE_FEED_PRODUCERS_OPERATION(-1,""),
    ASSET_ISSUE_OPERATION(-1,""),
    ASSET_RESERVE_OPERATION(-1,""),
    ASSET_FUND_FEE_POOL_OPERATION(-1,""),
    ASSET_SETTLE_OPERATION(-1,""),
    ASSET_GLOBAL_SETTLE_OPERATION(-1,""),
    ASSET_PUBLISH_FEED_OPERATION(-1,""),
    WITNESS_CREATE_OPERATION(-1,""),
    WITNESS_UPDATE_OPERATION(-1,""),
    PROPOSAL_CREATE_OPERATION(-1,""),
    PROPOSAL_UPDATE_OPERATION(-1,""),
    PROPOSAL_DELETE_OPERATION(-1,""),
    WITHDRAW_PERMISSION_CREATE_OPERATION(-1,""),
    WITHDRAW_PERMISSION_UPDATE_OPERATION(-1,""),
    WITHDRAW_PERMISSION_CLAIM_OPERATION(-1,""),
    WITHDRAW_PERMISSION_DELETE_OPERATION(-1,""),
    COMMITTEE_MEMBER_CREATE_OPERATION(-1,""),
    COMMITTEE_MEMBER_UPDATE_OPERATION(-1,""),
    COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION(-1,""),
    VESTING_BALANCE_CREATE_OPERATION(-1,""),
    VESTING_BALANCE_WITHDRAW_OPERATION(-1,""),
    WORKER_CREATE_OPERATION(-1,""),
    CUSTOM_OPERATION(-1,""),
    ASSERT_OPERATION(-1,""),
    BALANCE_CLAIM_OPERATION(-1,""),
    OVERRIDE_TRANSFER_OPERATION(-1,""),
    TRANSFER_TO_BLIND_OPERATION(-1,""),
    BLIND_TRANSFER_OPERATION(-1,""),
    TRANSFER_FROM_BLIND_OPERATION(-1,""),
    ASSET_SETTLE_CANCEL_OPERATION(-1,""),  // VIRTUAL
    ASSET_CLAIM_FEES_OPERATION(-1,"");

    @Getter private int code;
    @Getter private String sub;
}

/**
 * 链上类型参照
 */
//    typedef fc::static_variant<
//        transfer_operation,             //0 转账
//        limit_order_create_operation,   //1 挂单交易
//        limit_order_cancel_operation,   //2 取消挂单
//        call_order_update_operation,    //3 抵押发币
//        fill_order_operation,           //4 VIRTUAL
//        account_create_operation,   //5 创建用户
//        account_update_operation,   //6 更新用户
//        account_whitelist_operation,   //7
//        account_upgrade_operation,   //8 升级为终生会员
//        account_transfer_operation,   //9
//        asset_create_operation,   //10 创建货币
//        asset_update_operation,   //11 更新货币
//        asset_update_bitasset_operation,   //12 更新智能货币参数
//        asset_update_feed_producers_operation,   //13 修改货币喂价人
//        asset_issue_operation,   //14
//        asset_reserve_operation,   //15
//        asset_fund_fee_pool_operation,   //16
//        asset_settle_operation,   //17
//        asset_global_settle_operation,   //18
//        asset_publish_feed_operation,   //19 货币喂价
//        witness_create_operation,   //20 创建见证人
//        witness_update_operation,   //21 更新见证人
//        proposal_create_operation,   //22 创建提案
//        proposal_update_operation,   //23 更新提案
//        proposal_delete_operation,   //24 删除提案
//        withdraw_permission_create_operation,   //25
//        withdraw_permission_update_operation,   //26
//        withdraw_permission_claim_operation,   //27
//        withdraw_permission_delete_operation,   //28
//        committee_member_create_operation,   //29 创建理事会成员
//        committee_member_update_operation,   //30 修改理事会成员
//        committee_member_update_global_parameters_operation,   //31 修改系统参数/燃烧币费用
//        vesting_balance_create_operation,   //32 创建锁定资产
//        vesting_balance_withdraw_operation,   //33 提取锁定资产
//        worker_create_operation,   //34 创建 预算项目
//        custom_operation,   //35
//        assert_operation,   //36
//        balance_claim_operation,   //37  提取锁定资产
//        override_transfer_operation,   //38
//        transfer_to_blind_operation,   //39 正常账户转帐到隐私账户
//        blind_transfer_operation,   //40  隐私之间转账
//        transfer_from_blind_operation,   //41 从隐私账户到正常账户
//        asset_settle_cancel_operation,  //42 VIRTUAL
//        asset_claim_fees_operation,   //43
//        fba_distribute_operation,       //44 VIRTUAL
//        bid_collateral_operation,   //45
//        execute_bid_operation,           //46 VIRTUAL
//        asset_reserve_fees_operation,    //47
//        gateway_withdraw_operation,         //48 提币
//        gateway_deposit_operation,          //49 充值
//        gateway_issue_currency_operation,   //50 给网关发行货币
//        bitlender_option_create_operation,  //51 创建法币参数
//        bitlender_option_author_operation,  //52 创建法币董事会
//        bitlender_option_update_operation,  //53 修改法币参数
//        bitlender_rate_update_operation,      //54 修改法币率利
//        asset_property_operation,             //55 赋权资产为法币或可抵押数字货币
//        bitlender_loan_operation,             //56 借款
//        bitlender_invest_operation,           //57 投资
//        bitlender_repay_interest_operation,   //58 还息
//        bitlender_overdue_interest_operation, //59 逾期还利息
//        bitlender_repay_principal_operation,            //60 还款
//        bitlender_pre_repay_principal_operation,        //61 提前还款
//        bitlender_overdue_repay_principal_operation,    //62 逾期还款
//        bitlender_add_collateral_operation,             //63 增加抵押
//        bitlender_recycle_operation,            //64 处理不良资产
//        bitlender_setautorepayer_operation,     //65 自动还款账户
//        fill_object_history_operation,          //66 VIRTUAL
//        issue_fundraise_create_operation,        //67 创建筹资参数
//        issue_fundraise_update_operation,        //68 修改筹资参数
//        buy_fundraise_create_operation,               //69 筹资
//        buy_fundraise_enable_operation,               //70 筹资有效
//        account_coupon_operation,               //71 增加优惠卷
//        change_identity_operation,              //72 激活见证人
//        bitlender_autorepayment_operation,      //73 自动还款
//        withdraw_exchange_fee_operation,        //74 提取费用
//        bitlender_paramers_update_operation,    //75 修改借贷系统参数
//        gateway_create_operation,                 //76 创建网关
//        gateway_update_operation,   //77 更新网关
//        carrier_create_operation,   //78 创建运营商
//        carrier_update_operation,   //79 更新运营商
//        budget_member_create_operation,   //80 创建预算委员会成员
//        budget_member_update_operation,   //81 更新预算委员会成员
//        transfer_vesting_operation,   //82 锁定转账
//        revoke_vesting_operation,   //83 取消锁定转账
//        bitlender_remove_operation,             //84 取消借款
//        bitlender_squeeze_operation,             //85 VIRTUAL
//        bitlender_publish_feed_operation,        //86 借贷喂价
//        bitlender_update_feed_producers_operation, //87 更新借贷喂价人
//        bitlender_test_operation,                 //88 测试，不要使用
//        bitlender_option_stop_operation,  //89 借贷停运
//        issue_fundraise_publish_feed_operation,        //90 众筹喂价
//        finance_paramers_update_operation,        //91 众筹系统参数
//        sell_exchange_create_operation,        //92 发布兑换
//        sell_exchange_update_operation,        //93 更新兑换
//        sell_exchange_remove_operation,        //94 删除兑换
//        buy_exchange_create_operation,        //95  发布购买
//        buy_exchange_update_operation,        //96 更新购买
//        buy_exchange_remove_operation,        //97  删除购买
//        issue_fundraise_remove_operation,     //98  删除筹资
//        bitlender_option_fee_mode_operation,   //99 修改法币参数
//        account_authenticate_operation,      //100 认证信息
//        author_create_operation,            //101 创建认证人
//        author_update_operation,            //102 更新认证人
//        bitlender_recycle_interest_operation, //103 利息逾期
//        account_config_operation,      //104 用户配置
//        asset_update_gateway_operation,   //105 更新资产网关
//        committee_member_update_zos_parameters_operation,   //106 修改ZOS系统参数
//        locktoken_create_operation,   //107 创建锁仓
//        locktoken_update_operation,   //108 修改锁仓
//        locktoken_remove_operation,   //109  取消锁仓
//        locktoken_node_operation   //110 收益锁仓
