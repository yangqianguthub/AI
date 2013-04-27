package com.mac.smartcontrol.thread;

import android.app.Service;
import android.content.Intent;

import com.mac.smartcontrol.util.WriteUtil;

import define.entity.Message;
import define.entity.MessageQueue;
import define.type.MsgType_E;

public class MessageQueueRunnable implements Runnable {

	Service service;
	public boolean isRun = true;

	public MessageQueueRunnable(Service service) {
		super();
		this.service = service;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRun) {
			if (MessageQueue.getList().size() < 0) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			for (int i = 0; i < MessageQueue.getList().size(); i++) {
				Message message = MessageQueue.getList().get(i);
				if (message.getMessage_Header().getUsMsgId() == 1
						&& message.getMessage_Header().getUcMsgOper() == 5) {
					WriteUtil.usSsnId = message.getMessage_Header()
							.getUsSsnId();
				}
				if (message.getMessage_Header().getUcMsgType() == MsgType_E.MSGTYPE_NTF
						.getVal()) {
					Intent intent = new Intent("ntf");
					intent.putExtra("ntf", message.getUsBody());
					try {
						service.sendBroadcast(intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						isRun = false;
						service.stopSelf();
					}
					synchronized (MessageQueue.getList()) {
						MessageQueue.remove(i);
					}

					return;
				}
				String action = message.getMessage_Header().getUsMsgId() + "_"
						+ message.getMessage_Header().getUcMsgOper();
				Intent intent = new Intent(action);
				intent.putExtra("data", message.getUsBody());
				try {
					service.sendBroadcast(intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					isRun = false;
					service.stopSelf();
				}
				synchronized (MessageQueue.getList()) {
					try {
						MessageQueue.remove(i);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						isRun = false;
						service.stopSelf();
					}
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}