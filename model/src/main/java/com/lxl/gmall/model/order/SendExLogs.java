package com.lxl.gmall.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * author:atGuiGu-mqx
 * date:2022/5/17 16:58
 * 描述：
 **/
@Data
@TableName("send_ex_logs")
public class SendExLogs{
	private Long id;
	private Long messageId;
	private String message;
	private String exchange;
	private String routingKey;
	private Long retryCount;
	private Long delayTime;
	private Long isDelay;
	private Long status;
}
