package cn.lanyj.cehelper.core;

import java.util.List;
import java.util.function.Predicate;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.windows.LPCVOID;

public interface Utils {

	public List<CEProcess> getProcesses();

	public String getLastErrorMessageOrNull();

	public default List<CEProcess> getProcessesWithPredicate(Predicate<CEProcess> predicate) {
		List<CEProcess> processes = getProcesses();
		processes.removeIf((p) -> {
			return !predicate.test(p);
		});
		return processes;
	}

	public long readProcessMemory(Pointer hprocess, CEAddressPointer baseAddress, LPCVOID buffer, long sizeInBytes);

	public long writeProcessMemory(Pointer hprocess, CEAddressPointer baseAddress, LPCVOID buffer, long sizeInBytes);

	public void codeInject(Pointer hprocess, CEAddressPointer originAddress, CEAddressPointer afterPC,
			byte[] newOriginCode, byte[] injectCode);

	public CEAddressPointer getPointerFinalAddress(Pointer hprocess, CEAddressPointer base, long[] offset);

	public long pointsRead(Pointer hprocess, CEAddressPointer base, long[] offset, LPCVOID buffer, long sizeInBytes);

	public long pointsWrite(Pointer hprocess, CEAddressPointer base, long[] offset, LPCVOID buffer, long sizeInBytes);

	public static byte[] intArrayToByteArray(int... vals) {
		byte[] buf = new byte[vals.length];
		for (int i = 0; i < vals.length; i++) {
			buf[i] = (byte) (vals[i] & 0xFF);
		}
		return buf;
	}

}
