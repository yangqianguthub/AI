package define.oper.body.req;

import java.nio.ByteBuffer;

import com.mac.smartcontrol.util.FormatTransfer;

public class MsgCtrlStudyReq_S {
	short usIdx;       // ��������
    short usCmdIdx;    // ��������Ӧ��ָ������
    
	private MsgCtrlStudyReq_S(short usIdx, short usCmdIdx) {
		super();
		this.usIdx = usIdx;
		this.usCmdIdx = usCmdIdx;
	}
	public short getUsIdx() {
		return usIdx;
	}
	public void setUsIdx(short usIdx) {
		this.usIdx = usIdx;
	}
	public short getUsCmdIdx() {
		return usCmdIdx;
	}
	public void setUsCmdIdx(short usCmdIdx) {
		this.usCmdIdx = usCmdIdx;
	}
	public byte[] getMsgCtrlStudyReq_S(){
		ByteBuffer bb_Msg=ByteBuffer.allocate(4);
		bb_Msg.asShortBuffer().put(FormatTransfer.reverseShort(usIdx));
		bb_Msg.asShortBuffer().put(FormatTransfer.reverseShort(usCmdIdx));
		return bb_Msg.array();
	}
}