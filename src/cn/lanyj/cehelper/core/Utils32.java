package cn.lanyj.cehelper.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.LPCVOID;
import org.bytedeco.javacpp.windows.PROCESSENTRY32;

public class Utils32 {

	public static String getLastErrorMessage() {
		int error = windows.GetLastError();
		if (error == 0) {
			return null;
		}
		CharPointer charPointer = new CharPointer(8);
		int dwRv = windows.FormatMessage(windows.FORMAT_MESSAGE_ALLOCATE_BUFFER | windows.FORMAT_MESSAGE_FROM_SYSTEM,
				null, error, windows.MAKELANGID(windows.LANG_ENGLISH, windows.SUBLANG_ENGLISH_US),
				charPointer.asBuffer(), 0, null);
		String msg = null;
		if (dwRv != 0) {
			CEAddressPointer local = new CEAddressPointer(charPointer.asByteBuffer().getLong());
			BytePointer bytePointer = new BytePointer(local);
			msg = bytePointer.getString();
			windows.LocalFree(bytePointer);
		}
		CharPointer.free(charPointer);
		windows.SetLastError(0);
		return msg;
	}

	public static List<CEProcess> getProcessMap() {
		List<CEProcess> processes = new ArrayList<>(32);

		PROCESSENTRY32 pe32 = new PROCESSENTRY32();
		pe32.dwSize(pe32.sizeof());
		Pointer hprocess = windows.CreateToolhelp32Snapshot(windows.TH32CS_SNAPPROCESS, 0);
		boolean res = windows.Process32First(hprocess, pe32);
		while (res) {
			processes.add(new CEProcess(pe32.th32ProcessID(), pe32.szExeFile().getString()));
			res = windows.Process32Next(hprocess, pe32);
		}
		Pointer.free(pe32);
		windows.CloseHandle(hprocess);
		return processes;
	}

	public static List<CEProcess> getPidWithPredicate(Predicate<CEProcess> predicate) {
		List<CEProcess> processes = getProcessMap();
		processes.removeIf((t) -> {
			return !predicate.test(t);
		});
		return processes;
	}

	public static List<CEProcess> getPidWithProcessNameStartWith(String nameStart) {
		return getPidWithPredicate((t) -> {
			return t.getName().startsWith(nameStart);
		});
	}

	public static List<CEProcess> getPidWithProcessNameEquals(String name) {
		return getPidWithPredicate((t) -> {
			return name.equals(t.getName());
		});
	}

	public static List<CEProcess> getPidWithProcessNamePattern(String pattern) {
		Pattern patt = Pattern.compile(pattern);
		return getPidWithPredicate((t) -> {
			return patt.matcher(t.getName()).matches();
		});
	}

	public static long readProcessMemory(Pointer hprocess, CEAddressPointer baseAddress, LPCVOID buffer,
			long sizeInBytes) {
		LPCVOID base = new LPCVOID(baseAddress);
		SizeTPointer sizeTPointer = new SizeTPointer(1);
		long successSize = -1;
		try {
			boolean success = windows.ReadProcessMemory(hprocess, base, buffer, sizeInBytes, sizeTPointer);
			if (success) {
				successSize = (int) sizeTPointer.get();
			}
			buffer.limit(buffer.limit() + successSize);
		} finally {
			SizeTPointer.free(sizeTPointer);
		}
		return successSize;
	}

	public static long writeProcessMemory(Pointer hprocess, CEAddressPointer baseAddress, LPCVOID buffer,
			long sizeInBytes) {
		LPCVOID base = new LPCVOID(baseAddress);
		SizeTPointer sizeTPointer = new SizeTPointer(1);
		long successSize = -1;
		try {
			boolean success = windows.WriteProcessMemory(hprocess, base, buffer, sizeInBytes, sizeTPointer);
			if (success) {
				successSize = (int) sizeTPointer.get();
			}
		} finally {
			SizeTPointer.free(sizeTPointer);
		}
		return successSize;
	}

	public static CEAddressPointer getJmpE9Parameter(CEAddressPointer origin, CEAddressPointer destination) {
		CEAddressPointer parameter = new CEAddressPointer(destination.address() - origin.address() - 5);
		return parameter;
	}

	public static void codeInject(Pointer hprocess, CEAddressPointer originAddress, CEAddressPointer afterPC,
			byte[] newOriginCode, byte[] injectCode) {
		Pointer injectAddress = windows.VirtualAllocEx(hprocess, null, 1024, windows.MEM_COMMIT | windows.MEM_RESERVE,
				windows.PAGE_EXECUTE_READWRITE);
		if (injectAddress.isNull()) {
			throw new RuntimeException("windows.VirtualAllocEx failed.");
		}
		{
			int injectCodeSize = injectCode.length;
			CEAddressPointer nx = getJmpE9Parameter(new CEAddressPointer(injectAddress.address() + injectCodeSize - 5),
					afterPC);
			injectCode[injectCodeSize - 4] = (byte) ((nx.address() >> 0) & 0xFF);
			injectCode[injectCodeSize - 3] = (byte) ((nx.address() >> 8) & 0xFF);
			injectCode[injectCodeSize - 2] = (byte) ((nx.address() >> 16) & 0xFF);
			injectCode[injectCodeSize - 1] = (byte) ((nx.address() >> 24) & 0xFF);
			writeProcessMemory(hprocess, new CEAddressPointer(injectAddress.address()),
					new LPCVOID(new BytePointer(injectCode)), injectCodeSize);
		}
		{
			int newOriginCodeSize = newOriginCode.length;
			CEAddressPointer nx = getJmpE9Parameter(originAddress, new CEAddressPointer(injectAddress.address()));
			newOriginCode[1] = (byte) ((nx.address() >> 0) & 0xFF);
			newOriginCode[2] = (byte) ((nx.address() >> 8) & 0xFF);
			newOriginCode[3] = (byte) ((nx.address() >> 16) & 0xFF);
			newOriginCode[4] = (byte) ((nx.address() >> 24) & 0xFF);
			Arrays.fill(newOriginCode, 5, newOriginCodeSize, (byte) 0x90);
			writeProcessMemory(hprocess, originAddress, new LPCVOID(new BytePointer(newOriginCode)), newOriginCodeSize);
		}
	}

	public static CEAddressPointer getPointerFinalAddress(Pointer hprocess, CEAddressPointer base, long[] offset) {
		IntPointer buf = new IntPointer(1);
		LPCVOID ptr = new LPCVOID(buf);
		int index = 0;
		try {
			while (index < offset.length) {
				readProcessMemory(hprocess, base, ptr, 4);
				base.address(buf.get() + offset[index]);
				index++;
			}
		} finally {
			IntPointer.free(buf);
		}
		return base;
	}

	public static long pointsRead(Pointer hprocess, CEAddressPointer base, long[] offset, LPCVOID buffer,
			long sizeInBytes) {
		return readProcessMemory(hprocess, getPointerFinalAddress(hprocess, base, offset), buffer, sizeInBytes);
	}

	public static long pointsWrite(Pointer hprocess, CEAddressPointer base, long[] offset, LPCVOID buffer,
			long sizeInBytes) {
		return writeProcessMemory(hprocess, getPointerFinalAddress(hprocess, base, offset), buffer, sizeInBytes);
	}

	public static byte[] intArrayToByteArray(int... vals) {
		byte[] buf = new byte[vals.length];
		for (int i = 0; i < vals.length; i++) {
			buf[i] = (byte) (vals[i] & 0xFF);
		}
		return buf;
	}

}
