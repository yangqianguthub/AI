package com.mac.smartcontrol.broadcast;

import java.util.Arrays;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mac.smartcontrol.ModifyDeviceActivity;
import com.mac.smartcontrol.ModifyModeActivity;
import com.mac.smartcontrol.SocketService;

import define.entity.Rgn_S;
import define.oper.MsgOper_E;
import define.oper.body.ack.MsgModAck_S;
import define.oper.body.ack.MsgQryAck_S;
import define.type.ErrCode_E;
import define.type.MsgId_E;

public class ModifyModeBroadcastReceiver extends BroadcastReceiver {

	Activity activity;

	public ModifyModeBroadcastReceiver(Activity activity) {
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
			Toast.makeText(activity, "��ȷ�������Ƿ���,����ʧ��,�����µ�¼", Toast.LENGTH_LONG)
					.show();
			return;
		}
		byte msgId = Byte.parseByte(action.split("_")[0]);
		byte msgOper = Byte.parseByte(action.split("_")[1]);
		byte[] body = intent.getExtras().getByteArray("data");
		ModifyModeActivity modifyModeActivity = (ModifyModeActivity) activity;
		if (msgId == MsgId_E.MSGID_RGN.getVal() && msgOper ==MsgOper_E.MSGOPER_QRY.getVal()) {
			MsgQryAck_S msgQryAck_S = new MsgQryAck_S();
			msgQryAck_S.setMsgQryAck_S(body);
			if (msgQryAck_S.getUsCnt() > 0) {
				if (msgQryAck_S.getUsError() == 0) {
					int selectIdx = 0;
					for (int i = 0; i < msgQryAck_S.getUsCnt(); i++) {
						byte[] rgn_S_Byte = Arrays.copyOfRange(
								msgQryAck_S.getPucData(), i * Rgn_S.getSize(),
								(i + 1) * Rgn_S.getSize());
						Rgn_S rgn_S = new Rgn_S();
						rgn_S.setRgn_S(rgn_S_Byte);
						if (modifyModeActivity.mode_S.getUsRgnIdx() == rgn_S
								.getUsIdx()) {
							selectIdx = i;
						}
						modifyModeActivity.areaList.add(rgn_S);
						modifyModeActivity.areaNameList
								.add(rgn_S.getSzName());
					}
					modifyModeActivity.area_adapter.notifyDataSetChanged();
					modifyModeActivity.area_sp.setSelection(selectIdx);
				} else {
					ErrCode_E.showError(context, msgQryAck_S.getUsError());
				}
			}
		}
		if (msgId == MsgId_E.MSGID_MODE.getVal() && msgOper == MsgOper_E.MSGOPER_MOD.getVal()) {

			MsgModAck_S msgModAck_S = new MsgModAck_S();
			msgModAck_S.setMsgModAck_S(body);
			if (msgModAck_S.getUsError() == 0) {
				Toast.makeText(activity, "�޸ĳɹ�", Toast.LENGTH_LONG).show();

				modifyModeActivity.mode_S.setUsIdx(msgModAck_S.getUsIdx());
				Bundle bundle = new Bundle();
				bundle.putByteArray("mode",
						modifyModeActivity.mode_S.getMode_S());
				Intent i = new Intent();
				i.putExtras(bundle);
				// ����intent
				modifyModeActivity.setResult(Activity.RESULT_OK, i);
				modifyModeActivity.finish();
			} else {
				ErrCode_E.showError(context, msgModAck_S.getUsError());
			}
		}

	}

}