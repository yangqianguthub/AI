package com.mac.smartcontrol.broadcast;

import java.util.Arrays;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mac.smartcontrol.AddModeCmdActivity;
import com.mac.smartcontrol.SocketService;

import define.entity.Appl_S;
import define.entity.Cama_S;
import define.entity.Cmd_S;
import define.entity.Rgn_S;
import define.entity.Sens_S;
import define.oper.MsgOper_E;
import define.oper.body.ack.MsgAddAck_S;
import define.oper.body.ack.MsgQryAck_S;
import define.type.ErrCode_E;
import define.type.MsgId_E;

public class AddModeCmdBroadcastReceiver extends BroadcastReceiver {

	Activity activity;

	public AddModeCmdBroadcastReceiver(Activity activity) {
		super();
		this.activity = activity;
	}

	// int flag = 0;

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

		short msgId = Short.parseShort(action.split("_")[0]);
		byte msgOper = Byte.parseByte(action.split("_")[1]);
		byte[] body = intent.getExtras().getByteArray("data");
		AddModeCmdActivity addModeCmdActivity = (AddModeCmdActivity) activity;

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
						addModeCmdActivity.areaList.add(rgn_S);
						// addModeCmdActivity.areaListStr.add(rgn_S.getSzName());
					}
					// flag++;
					// if (flag == 5) {
					// addModeCmdActivity.area_sp
					// .setAdapter(addModeCmdActivity.area_adapter);
					// addModeCmdActivity.area_adapter.notifyDataSetChanged();
					// addModeCmdActivity.device_adapter
					// .notifyDataSetChanged();
					// addModeCmdActivity.cmd_adapter.notifyDataSetChanged();
					// flag = 0;
					// }

				}
			} else {
				ErrCode_E.showError(context, msgQryAck_S.getUsError());
			}
		}

		if (msgId == MsgId_E.MSGID_APPL.getVal()
				&& msgOper == MsgOper_E.MSGOPER_QRY.getVal()) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsError() == 0) {
				if (msgQryAck_S.getUsCnt() > 0) {
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] appl_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Appl_S.getSize(),
								(i + 1) * Appl_S.getSize());
						Appl_S appl_S = new Appl_S();
						appl_S.setAppl_S(appl_S_Byte);
						addModeCmdActivity.deviceList.add(appl_S);
					}
					// flag++;
					// if (flag == 5) {
					// addModeCmdActivity.area_sp
					// .setAdapter(addModeCmdActivity.area_adapter);
					// addModeCmdActivity.area_adapter.notifyDataSetChanged();
					// addModeCmdActivity.device_adapter.notifyDataSetChanged();
					// addModeCmdActivity.cmd_adapter.notifyDataSetChanged();
					// flag = 0;
					// }
				}
			} else {
				ErrCode_E.showError(context, msgQryAck_S.getUsError());
			}
		}

		if ((msgId == MsgId_E.MSGID_SENS.getVal() && msgOper == MsgOper_E.MSGOPER_QRY
				.getVal())
				|| (msgId == MsgId_E.MSGID_SENS.getVal() && msgOper == MsgOper_E.MSGOPER_MAX
						.getVal())) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsError() == 0) {
				if (msgQryAck_S.getUsCnt() > 0) {
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] sens_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Sens_S.getSize(),
								(i + 1) * Sens_S.getSize());
						Sens_S sens_S = new Sens_S();
						sens_S.setSens_S(sens_S_Byte);
						addModeCmdActivity.senseList.add(sens_S);
					}
					// flag++;
					// if (flag == 5) {
					// addModeCmdActivity.area_sp
					// .setAdapter(addModeCmdActivity.area_adapter);
					// addModeCmdActivity.area_adapter.notifyDataSetChanged();
					// addModeCmdActivity.device_adapter.notifyDataSetChanged();
					// addModeCmdActivity.cmd_adapter.notifyDataSetChanged();
					// flag = 0;
					// }
				}
			} else {
				ErrCode_E.showError(activity, msgQryAck_S.getUsError());
			}
		}

		if ((msgId == MsgId_E.MSGID_CAMA.getVal() && msgOper == MsgOper_E.MSGOPER_QRY
				.getVal())
				|| (msgId == MsgId_E.MSGID_CAMA.getVal() && msgOper == MsgOper_E.MSGOPER_MAX
						.getVal())) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsError() == 0) {
				if (msgQryAck_S.getUsCnt() > 0) {
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] cama_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Cama_S.getSize(),
								(i + 1) * Cama_S.getSize());
						Cama_S cama_S = new Cama_S();
						cama_S.setCama_S(cama_S_Byte);
						addModeCmdActivity.cameraList.add(cama_S);
					}
					// flag++;
					// if (flag == 5) {
					// addModeCmdActivity.area_sp
					// .setAdapter(addModeCmdActivity.area_adapter);
					// addModeCmdActivity.area_adapter.notifyDataSetChanged();
					// addModeCmdActivity.device_adapter.notifyDataSetChanged();
					// addModeCmdActivity.cmd_adapter.notifyDataSetChanged();
					// flag = 0;
					// }
				}
			} else {
				ErrCode_E.showError(activity, msgQryAck_S.getUsError());
			}

		}

		if (msgId == MsgId_E.MSGID_CMD.getVal()
				&& msgOper == MsgOper_E.MSGOPER_QRY.getVal()) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsError() == 0) {
				if (msgQryAck_S.getUsCnt() > 0) {
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] cmd_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Cmd_S.getSize(),
								(i + 1) * Cmd_S.getSize());
						Cmd_S cmd_S = new Cmd_S();
						cmd_S.setCmd_S(cmd_S_Byte);
						addModeCmdActivity.cmdList.add(cmd_S);
					}
					// flag++;
					// if (flag == 3) {
					// addModeCmdActivity.area_sp
					// .setAdapter(addModeCmdActivity.area_adapter);
					// addModeCmdActivity.area_adapter.notifyDataSetChanged();
					// addModeCmdActivity.device_adapter
					// .notifyDataSetChanged();
					// addModeCmdActivity.cmd_adapter.notifyDataSetChanged();
					// flag = 0;
					// }
				}
			} else {
				ErrCode_E.showError(activity, msgQryAck_S.getUsError());
			}
		}
		if (msgId == MsgId_E.MSGID_MODECMD.getVal()
				&& msgOper == MsgOper_E.MSGOPER_ADD.getVal()) {
			MsgAddAck_S msgAddAck_S = new MsgAddAck_S();
			msgAddAck_S.setMsgAddAck_S(body);
			if (msgAddAck_S.getUsError() == 0) {
				Toast.makeText(activity, "添加成功", Toast.LENGTH_LONG).show();
				addModeCmdActivity.modeCmd_S.setUsIdx(msgAddAck_S.getUsIdx());
				Bundle bundle = new Bundle();
				bundle.putByteArray("modecmd",
						addModeCmdActivity.modeCmd_S.getModeCmd_S());
				Intent i = new Intent();
				i.putExtras(bundle);
				// // 返回intent
				addModeCmdActivity.setResult(Activity.RESULT_OK, i);
				addModeCmdActivity.finish();
			} else {
				ErrCode_E.showError(context, msgAddAck_S.getUsError());
			}
		}

	}
}
