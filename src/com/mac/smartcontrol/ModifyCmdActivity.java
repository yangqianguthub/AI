package com.mac.smartcontrol;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mac.smartcontrol.broadcast.ModifyCmdBroadcastReceiver;
import com.mac.smartcontrol.util.WriteUtil;

import define.entity.Appl_S;
import define.entity.Cama_S;
import define.entity.Cmd_S;
import define.entity.Ctrl_S;
import define.entity.Mode_S;
import define.entity.Sens_S;
import define.oper.MsgOperCtrl_E;
import define.oper.MsgOper_E;
import define.oper.body.req.MsgCtrlStudyReq_S;
import define.oper.body.req.MsgCtrlTestReq_S;
import define.oper.body.req.MsgQryReq_S;
import define.type.CmdCode_E;
import define.type.CmdDevType_E;
import define.type.CmdType_E;
import define.type.MsgId_E;
import define.type.MsgType_E;

public class ModifyCmdActivity extends Activity {
	public Cmd_S cmd_S;
	ModifyCmdBroadcastReceiver modifyCmdBroadcastReceiver;

	public List<String> ctrlNameList = new ArrayList<String>();
	public List<String> codeNameList = new ArrayList<String>();
	public List<String> paraNameList = new ArrayList<String>();
	public Spinner ctrl_sp;
	public Spinner cmd_sp = null;
	public Spinner mode_sp = null;
	public ArrayAdapter<String> ctrl_adapter;
	public ArrayAdapter<String> code_adapter;
	public ArrayAdapter<String> para_adapter;

	public List<Ctrl_S> ctrlList = null;
	private String[] para = { "打开", "关闭" };
	private String[] code;
	private Appl_S appl_S;
	private Sens_S sens_S;
	private Cama_S cama_S;
	LinearLayout para_ll;
	LinearLayout infrared_ll;
	LinearLayout cmd_ll;
	LinearLayout mode_ll;
	Button test_Btn;
	Button study_Btn;
	public TextView infrared_code_tv;
	public int ucOffset = -1;
	private int type = -1;// 区分是管理里面添加还是走控制添加
	private byte cmdType;

	private String[] dev_names = new String[] { "电源控制", "红外控制" };
	private String[] sens_names = new String[] { "触发到客户端", "触发指令", "触发智能模式" };
	private String[] cam_names = new String[] { "电源控制" };

	public List<String> cmdNameList;
	public ArrayAdapter<String> cmd_adapter;
	public List<String> modeNameList;
	public ArrayAdapter<String> mode_adapter;
	public List<Cmd_S> cmdList;
	public List<Mode_S> modeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_cmd);

		Bundle bundle = getIntent().getExtras();
		cmd_ll = (LinearLayout) findViewById(R.id.cmd_ll);
		mode_ll = (LinearLayout) findViewById(R.id.mode_ll);
		cmd_S = new Cmd_S();
		if (bundle != null) {
			cmd_S.setCmd_S(bundle.getByteArray("cmd"));
			cmdType = bundle.getByte("cmdType");
			if (cmdType == CmdDevType_E.CMD_DEV_APPL.getVal()) {
				appl_S = new Appl_S();
				appl_S.setAppl_S(bundle.getByteArray("device"));
				type = bundle.getInt("type");

			} else if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
				sens_S = new Sens_S();
				sens_S.setSens_S(bundle.getByteArray("sense"));
				cmdNameList = new ArrayList<String>();
				modeNameList = new ArrayList<String>();
				cmdList = new ArrayList<Cmd_S>();
				modeList = new ArrayList<Mode_S>();

				cmd_sp = (Spinner) findViewById(R.id.cmd_sp);
				mode_sp = (Spinner) findViewById(R.id.mode_sp);

				cmd_adapter = new ArrayAdapter<String>(this,
						R.layout.simple_spinner_item, cmdNameList);
				cmd_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				cmd_sp.setAdapter(cmd_adapter);

				mode_adapter = new ArrayAdapter<String>(this,
						R.layout.simple_spinner_item, modeNameList);
				mode_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mode_sp.setAdapter(mode_adapter);
			} else if (cmdType == CmdDevType_E.CMD_DEV_CAMA.getVal()) {
				cama_S = new Cama_S();
				cama_S.setCama_S(bundle.getByteArray("camare"));
			}
		}

		code = getCode(cmdType);
		ctrlList = new ArrayList<Ctrl_S>();
		para_ll = (LinearLayout) findViewById(R.id.param_ll);
		infrared_ll = (LinearLayout) findViewById(R.id.infrared_ll);

		infrared_code_tv = (TextView) findViewById(R.id.infrared_code_tv);

		// if (cmd_S.getUcType() == CmdCode_E.CMD_CODE_INFRARED_CTRL.getVal()) {
		// para_ll.setVisibility(View.GONE);
		// infrared_ll.setVisibility(View.VISIBLE);
		// }

		final EditText cmd_name_Et = (EditText) findViewById(R.id.cmdname_et);
		cmd_name_Et.setText(cmd_S.getSzName());
		if (type == 0) {
			cmd_name_Et.setEnabled(false);
		}
		final EditText voice_name_Et = (EditText) findViewById(R.id.voicename_et);
		voice_name_Et.setText(cmd_S.getSzVoice());
		ctrl_sp = (Spinner) findViewById(R.id.ctrl_sp);
		final Spinner code_sp = (Spinner) findViewById(R.id.cmdcode_sp);

		final Spinner para_sp = (Spinner) findViewById(R.id.param_sp);
		code_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (cmdType == CmdDevType_E.CMD_DEV_APPL.getVal()) {
					if (arg2 == 1) {
						para_ll.setVisibility(View.GONE);
						infrared_ll.setVisibility(View.VISIBLE);
					} else {
						para_ll.setVisibility(View.VISIBLE);
						infrared_ll.setVisibility(View.GONE);
					}
					cmd_ll.setVisibility(View.GONE);
					mode_ll.setVisibility(View.GONE);
				} else if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
					if (arg2 == 0) {
						mode_ll.setVisibility(View.GONE);
						cmd_ll.setVisibility(View.GONE);
					} else if (arg2 == 1) {
						mode_ll.setVisibility(View.GONE);
						cmd_ll.setVisibility(View.VISIBLE);
					} else if (arg2 == 2) {
						mode_ll.setVisibility(View.VISIBLE);
						cmd_ll.setVisibility(View.GONE);
					}
					infrared_ll.setVisibility(View.GONE);
					para_ll.setVisibility(View.GONE);
				} else if (cmdType == CmdDevType_E.CMD_DEV_CAMA.getVal()) {
					infrared_ll.setVisibility(View.GONE);
					para_ll.setVisibility(View.VISIBLE);
					cmd_ll.setVisibility(View.GONE);
					mode_ll.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		modifyCmdBroadcastReceiver = new ModifyCmdBroadcastReceiver(
				ModifyCmdActivity.this);
		IntentFilter filter = new IntentFilter();

		filter.addAction(MsgId_E.MSGID_CMD.getVal() + "_"
				+ MsgOper_E.MSGOPER_MOD.getVal());

		filter.addAction(MsgId_E.MSGID_CTRL.getVal() + "_"
				+ MsgOper_E.MSGOPER_QRY.getVal());

		filter.addAction(MsgId_E.MSGID_CTRL.getVal() + "_"
				+ MsgOperCtrl_E.MSGOPER_CTRL_STUDY.getVal());

		filter.addAction(MsgId_E.MSGID_CTRL.getVal() + "_"
				+ MsgOperCtrl_E.MSGOPER_CTRL_TEST.getVal());

		filter.addAction(MsgId_E.MSGID_CMD.getVal() + "_"
				+ MsgOper_E.MSGOPER_QRY.getVal());
		filter.addAction(MsgId_E.MSGID_MODE.getVal() + "_"
				+ MsgOper_E.MSGOPER_QRY.getVal());
		filter.addAction("IOException");
		registerReceiver(modifyCmdBroadcastReceiver, filter);

		// try {
		WriteUtil.write(MsgId_E.MSGID_CTRL.getVal(), 0,
				MsgType_E.MSGTYPE_REQ.getVal(), MsgOper_E.MSGOPER_QRY.getVal(),
				(short) 2, new MsgQryReq_S((short) 0).getMsgQryReq_S(), this);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(ModifyCmdActivity.this, "请确认网络是否开启,连接失败",
		// Toast.LENGTH_LONG).show();
		// DisconnectionUtil.restart(ModifyCmdActivity.this);
		// }

		if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
			// try {
			WriteUtil.write(MsgId_E.MSGID_CMD.getVal(), 1,
					MsgType_E.MSGTYPE_REQ.getVal(), MsgOper_E.MSGOPER_QRY
							.getVal(), (short) 2, new MsgQryReq_S((short) 0)
							.getMsgQryReq_S(), this);

			WriteUtil.write(MsgId_E.MSGID_MODE.getVal(), 2,
					MsgType_E.MSGTYPE_REQ.getVal(), MsgOper_E.MSGOPER_QRY
							.getVal(), (short) 2, new MsgQryReq_S((short) 0)
							.getMsgQryReq_S(), this);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// Toast.makeText(ModifyCmdActivity.this, "请确认网络是否开启,连接失败",
			// Toast.LENGTH_LONG).show();
			// DisconnectionUtil.restart(ModifyCmdActivity.this);
			// }
		}

		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// R.layout.simple_spinner_item,types);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// sense_type_sp.setAdapter(adapter);

		ctrl_adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, ctrlNameList);
		ctrl_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ctrl_sp.setAdapter(ctrl_adapter);

		code_adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, code);
		code_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		code_sp.setAdapter(code_adapter);

		para_adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, para);
		para_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		para_sp.setAdapter(para_adapter);

		if (cmdType == CmdDevType_E.CMD_DEV_APPL.getVal()) {
			code_sp.setSelection(cmd_S.getUcCode() - 1);
			if (cmd_S.getUcType() == CmdCode_E.CMD_CODE_INFRARED_CTRL.getVal()) {
				infrared_code_tv.setText(ucOffset + "");
			} else {
				para_sp.setSelection(cmd_S.getUiPara() - 1);
			}
		} else if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
			code_sp.setSelection(cmd_S.getUcCode() - 4);
		}

		study_Btn = (Button) findViewById(R.id.study_btn);
		study_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// try {
				WriteUtil.write(MsgId_E.MSGID_CTRL.getVal(), 1,
						MsgType_E.MSGTYPE_REQ.getVal(),
						MsgOperCtrl_E.MSGOPER_CTRL_STUDY.getVal(),
						MsgCtrlTestReq_S.getSize(), new MsgCtrlStudyReq_S(
								ctrlList.get(ctrl_sp.getSelectedItemPosition())
										.getUsIdx(), cmd_S.getUsIdx())
								.getMsgCtrlStudyReq_S(), ModifyCmdActivity.this);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(ModifyCmdActivity.this, "请确认网络是否开启,连接失败",
				// Toast.LENGTH_LONG).show();
				// DisconnectionUtil.restart(ModifyCmdActivity.this);
				// }
			}
		});
		test_Btn = (Button) findViewById(R.id.test_btn);
		test_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MsgCtrlTestReq_S ctrlTestReq_S = new MsgCtrlTestReq_S();
				ctrlTestReq_S.setUsIdx(ctrlList.get(
						ctrl_sp.getSelectedItemPosition()).getUsIdx());
				ctrlTestReq_S.setUcCode((byte) (code_sp
						.getSelectedItemPosition() + 1));
				if (code_sp.getSelectedItemPosition() == 2) {
					ctrlTestReq_S.setUiPara(0);
				} else {
					ctrlTestReq_S.setUiPara(para_sp.getSelectedItemPosition() + 1);
				}

				// try {
				WriteUtil.write(MsgId_E.MSGID_CTRL.getVal(), 1,
						MsgType_E.MSGTYPE_REQ.getVal(),
						MsgOperCtrl_E.MSGOPER_CTRL_TEST.getVal(),
						MsgCtrlTestReq_S.getSize(),
						ctrlTestReq_S.getMsgCtrlTestReq_S(),
						ModifyCmdActivity.this);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(ModifyCmdActivity.this, "请确认网络是否开启,连接失败",
				// Toast.LENGTH_LONG).show();
				// DisconnectionUtil.restart(ModifyCmdActivity.this);
				// }

			}
		});

		Button add_Btn = (Button) findViewById(R.id.modify_btn);
		ImageView back_Iv = (ImageView) findViewById(R.id.back);
		add_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String cmdName = cmd_name_Et.getText().toString().trim();
				if (cmdName == null || cmdName.equals("")) {
					Toast.makeText(ModifyCmdActivity.this, "名称不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (cmdName.getBytes().length > 32) {
					Toast.makeText(ModifyCmdActivity.this, "名称太长",
							Toast.LENGTH_LONG).show();
					return;
				}
				String voiceName = voice_name_Et.getText().toString().trim();
				if (voiceName == null || voiceName.equals("")) {
					Toast.makeText(ModifyCmdActivity.this, "语音文本不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (voiceName.getBytes().length > 64) {
					Toast.makeText(ModifyCmdActivity.this, "语音文本名称太长",
							Toast.LENGTH_LONG).show();
					return;
				}
				cmd_S.setSzName(cmdName);
				cmd_S.setSzVoice(voiceName);
				cmd_S.setUsCtrlIdx(ctrlList.get(
						ctrl_sp.getSelectedItemPosition()).getUsIdx());

				int idx = code_sp.getSelectedItemPosition();

				if (cmdType == CmdDevType_E.CMD_DEV_APPL.getVal()) {
					cmd_S.setUcCode((byte) (idx + 1));
					if (code_sp.getSelectedItemPosition() == 2) {
						if (ucOffset == -1) {
							Toast.makeText(ModifyCmdActivity.this, "请先进行学习",
									Toast.LENGTH_LONG).show();
							return;
						}
						cmd_S.setUiPara(ucOffset);
					} else {
						cmd_S.setUiPara(para_sp.getSelectedItemPosition() + 1);
					}
					if (type == 0) {
						cmd_S.setUcType(CmdType_E.CMD_TYPE_PREDEF.getVal());
					} else if (type == -1) {
						cmd_S.setUcType(CmdType_E.CMD_TYPE_CUSTOM.getVal());
					}
					cmd_S.setUsDevIdx(appl_S.getUsIdx());
				} else if (cmdType == CmdDevType_E.CMD_DEV_CAMA.getVal()) {
					cmd_S.setUcCode((byte) 1);
					cmd_S.setUiPara(para_sp.getSelectedItemPosition() + 1);
					cmd_S.setUcType(CmdType_E.CMD_TYPE_CUSTOM.getVal());
					cmd_S.setUsDevIdx(cama_S.getUsIdx());
				} else if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
					if (code_sp.getSelectedItemPosition() == 0) {
						cmd_S.setUiPara(0);
						cmd_S.setUcCode((byte) 4);
					} else if (code_sp.getSelectedItemPosition() == 1) {
						if (cmdList.size() == 0) {
							Toast.makeText(ModifyCmdActivity.this, "请选择一个指令",
									Toast.LENGTH_LONG).show();
							return;
						}
						int cmd_Idx = cmd_sp.getSelectedItemPosition();
						cmd_S.setUiPara(cmdList.get(cmd_Idx).getUsIdx());
						cmd_S.setUcCode((byte) 5);
					} else if (code_sp.getSelectedItemPosition() == 2) {
						if (modeList.size() == 0) {
							Toast.makeText(ModifyCmdActivity.this, "请选择一个智能模式",
									Toast.LENGTH_LONG).show();
							return;
						}
						int mode_Idx = mode_sp.getSelectedItemPosition();
						cmd_S.setUiPara(modeList.get(mode_Idx).getUsIdx());
						cmd_S.setUcCode((byte) 6);
					}
					cmd_S.setUcType(CmdType_E.CMD_TYPE_CUSTOM.getVal());
					cmd_S.setUsDevIdx(sens_S.getUsIdx());
				}
				cmd_S.setUcDevType(cmdType);

				// if (code_sp.getSelectedItemPosition() == 2) {
				// if (ucOffset == -1) {
				// Toast.makeText(ModifyCmdActivity.this, "请先进行学习",
				// Toast.LENGTH_LONG).show();
				// return;
				// }
				// cmd_S.setUiPara(ucOffset);
				// } else {
				// cmd_S.setUiPara(para_sp.getSelectedItemPosition() + 1);
				// }
				//
				// cmd_S.setUsDevIdx(appl_S.getUsIdx());
				// cmd_S.setUcDevType(appl_S.getUcType());
				// try {
				WriteUtil.write(MsgId_E.MSGID_CMD.getVal(), 0,
						MsgType_E.MSGTYPE_REQ.getVal(),
						MsgOper_E.MSGOPER_MOD.getVal(), Cmd_S.getSize(),
						cmd_S.getCmd_S(), ModifyCmdActivity.this);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(ModifyCmdActivity.this, "请确认网络是否开启,连接失败",
				// Toast.LENGTH_LONG).show();
				// DisconnectionUtil.restart(ModifyCmdActivity.this);
				// }
			}
		});

		back_Iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	public String[] getCode(byte cmdType) {
		if (cmdType == CmdDevType_E.CMD_DEV_APPL.getVal()) {
			return dev_names;
		} else if (cmdType == CmdDevType_E.CMD_DEV_CAMA.getVal()) {
			return cam_names;
		} else if (cmdType == CmdDevType_E.CMD_DEV_SENS.getVal()) {
			return sens_names;
		}
		return null;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		unregisterReceiver(modifyCmdBroadcastReceiver);
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return true;
	}
}
