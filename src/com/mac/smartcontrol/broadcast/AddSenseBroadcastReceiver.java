package com.mac.smartcontrol.broadcast;

import java.util.Arrays;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mac.smartcontrol.AddSenseActivity;
import com.mac.smartcontrol.SocketService;

import define.entity.Rgn_S;
import define.oper.MsgOper_E;
import define.oper.body.ack.MsgAddAck_S;
import define.oper.body.ack.MsgQryAck_S;
import define.type.ErrCode_E;
import define.type.MsgId_E;

public class AddSenseBroadcastReceiver extends BroadcastReceiver {

	Activity activity;

	public AddSenseBroadcastReceiver(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals("IOException")) {
			Intent it = new Intent();
			it.setClass(activity, SocketService.class);
			activity.stopService(it);
			Toast.makeText(activity, "请确认网络是否开启,操作失败,请重新登录", Toast.LENGTH_LONG)
					.show();
			return;
		}
		byte msgId = Byte.parseByte(action.split("_")[0]);
		byte msgOper = Byte.parseByte(action.split("_")[1]);
		byte[] body = intent.getExtras().getByteArray("data");
		AddSenseActivity addSenseActivity = (AddSenseActivity) activity;
		if (msgId == MsgId_E.MSGID_RGN.getVal()
				&& msgOper == MsgOper_E.MSGOPER_QRY.getVal()) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsError() == 0) {
				if (msgQryAck_S.getUsCnt() > 0) {
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] rgn_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Rgn_S.getSize(),
								(i + 1) * Rgn_S.getSize());
						Rgn_S rgn_S = new Rgn_S();
						rgn_S.setRgn_S(rgn_S_Byte);
						addSenseActivity.areaList.add(rgn_S);
						addSenseActivity.area_adapter.add(rgn_S.getSzName());
					}
				}
			} else {
				ErrCode_E.showError(context, msgQryAck_S.getUsError());
			}
		}
		if (msgId == MsgId_E.MSGID_SENS.getVal()
				&& msgOper == MsgOper_E.MSGOPER_ADD.getVal()) {
			MsgAddAck_S msgAddAck_S = new MsgAddAck_S();
			msgAddAck_S.setMsgAddAck_S(body);
			if (msgAddAck_S.getUsError() == 0) {
				Toast.makeText(activity, "添加成功", Toast.LENGTH_LONG).show();
				addSenseActivity.sens_S.setUsIdx(msgAddAck_S.getUsIdx());
				Bundle bundle = new Bundle();
				bundle.putByteArray("sense",
						addSenseActivity.sens_S.getSens_S());
				Intent i = new Intent();
				i.putExtras(bundle);
				// // 返回intent
				addSenseActivity.setResult(Activity.RESULT_OK, i);
				addSenseActivity.finish();
			} else {
				ErrCode_E.showError(context, msgAddAck_S.getUsError());
			}
		}

	}

}
