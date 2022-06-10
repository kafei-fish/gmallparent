package com.lxl.gmall.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * author:atGuiGu-mqx
 * date:2022/5/17 16:58
 * 描述：
 **/
@Data
@TableName("receiver_ex_logs")
public class ReceiverExLogs {
    private Long id;
    private Long orderId;
    private String content;
    private String messageId;
    private String message;
    private String exchange;
    private String routingKey;
    private String status = "0";
}
