package define.entity;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.mac.smartcontrol.util.FormatTransfer;

public class Appl_S {
	short usIdx;
	short usRgnIdx; // 区域索引
	String szName; // 名称
	byte ucType; // 家电类型

	public Appl_S(short usIdx, short usRgnIdx, String szName, byte ucType) {
		super();
		this.usIdx = usIdx;
		this.usRgnIdx = usRgnIdx;
		this.szName = szName;
		this.ucType = ucType;
	}

	public Appl_S() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static short getSize() {
		return 37;
	}

	public short getUsIdx() {
		return usIdx;
	}

	public void setUsIdx(short usIdx) {
		this.usIdx = usIdx;
	}

	public short getUsRgnIdx() {
		return usRgnIdx;
	}

	public void setUsRgnIdx(short usRgnIdx) {
		this.usRgnIdx = usRgnIdx;
	}

	public String getSzName() {
		return szName;
	}

	public void setSzName(String szName) {
		this.szName = szName;
	}

	public byte getUcType() {
		return ucType;
	}

	public void setUcType(byte ucType) {
		this.ucType = ucType;
	}

	public byte[] getAppl_S() {
		ByteBuffer bb_Msg = ByteBuffer.allocate(getSize());
		bb_Msg.put(FormatTransfer.toLH(usIdx));
		bb_Msg.put(FormatTransfer.toLH(usRgnIdx));

		try {
			int szName_Len = szName.getBytes("gbk").length;
			bb_Msg.put(szName.getBytes("gbk"));
			if (szName_Len < 32) {
				byte[] szName_Sub = new byte[32 - szName_Len];
				bb_Msg.put(new String(szName_Sub).getBytes("gbk"));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bb_Msg.put(ucType);
		return bb_Msg.array();
	}

	public void setAppl_S(byte[] b) {
		byte[] usIdx_b = new byte[2];
		System.arraycopy(b, 0, usIdx_b, 0, 2);
		usIdx = FormatTransfer.lBytesToShort(usIdx_b);

		byte[] usRgnIdx_b = new byte[2];
		System.arraycopy(b, 2, usRgnIdx_b, 0, 2);
		usRgnIdx = FormatTransfer.lBytesToShort(usRgnIdx_b);

		byte[] szName_b = new byte[32];
		System.arraycopy(b, 4, szName_b, 0, 32);
		// szName = new String(szName_b).trim();
		try {
			szName = new String(szName_b, "gbk").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ucType = b[36];
	}
}
