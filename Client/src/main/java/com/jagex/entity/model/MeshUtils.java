package com.jagex.entity.model;

public class MeshUtils {

	public static MeshRevision getRevision(byte[] data) {
		if(data[data.length - 1] == -1 && data[data.length - 2] == -1) {
			int var = data[data.length - 18] & 0xff;
			boolean b = (0x8 & var) == 8;
			return b ? MeshRevision.REVISION_622 : MeshRevision.REVISION_530;
		}
		return MeshRevision.REVISION_317;
	}

}
