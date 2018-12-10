package cn.lanyj.cehelper;

import java.util.List;
import java.util.NoSuchElementException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.LPCVOID;

public class PlantsVsZombies {

	public static void snailRunFast(Pointer hprocess) {
		{
			long originAddress = 0x0052095A;
			byte newOriginCode[] = Utils32.intArrayToByteArray(0xE9, 0xA1, 0xF6, 0x35, 0x00, 0x90, 0x90, 0x90);
			byte injectCode[] = Utils32.intArrayToByteArray(0xC7, 0x44, 0x24, 0x10, 0x00, 0x00, 0xA0, 0x40, 0xD8, 0x44,
					0x24, 0x10, 0xD9, 0x5C, 0x24, 0x24, 0xE9, 0x4D, 0x09, 0xCA, 0xFF);
			Utils32.codeInject(hprocess, new CEAddressPointer(originAddress), new CEAddressPointer(originAddress + 5),
					newOriginCode, injectCode);
		}
		{
			long originAddress = 0x00520935;
			byte newOriginCode[] = Utils32.intArrayToByteArray(0xE9, 0xA1, 0xF6, 0x35, 0x00, 0x90, 0x90, 0x90);
			byte injectCode[] = Utils32.intArrayToByteArray(0xC7, 0x44, 0x24, 0x10, 0x00, 0x00, 0xA0, 0x40, 0xD8, 0x64,
					0x24, 0x10, 0xD9, 0x5C, 0x24, 0x24, 0xE9, 0x4D, 0x09, 0xCA, 0xFF);
			Utils32.codeInject(hprocess, new CEAddressPointer(originAddress), new CEAddressPointer(originAddress + 5),
					newOriginCode, injectCode);
		}
	}

	/**
	 * 
	 * @param hprocess
	 * @param address
	 * @param bytesInInts In order to reduce type conversion, convert int to byte
	 */
	private static void simpleMemoryWrite(Pointer hprocess, long address, int... bytesInInts) {
		Utils32.writeProcessMemory(hprocess, new CEAddressPointer(address),
				new LPCVOID(new BytePointer(Utils32.intArrayToByteArray(bytesInInts))), bytesInInts.length);
	}

	public static void plantsFastMoneyProduce(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x521D43, 0x83, 0xCD, 0x9C);
	}

	public static void firstSunshineCount(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x40B095 + 6, 0x20, 0xA1, 0x07, 0x00);
	}

	public static void plantsWithoutRemainTime(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x487296, 0x90, 0x90);
	}

	public static void zombiesWithoutRemainTime(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x413FD9, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90);
	}

	public static void fireUnlimited(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x53130F, 0x29, 0xFF, 0x90, 0x90);
	}

	public static void plantsAliveForeverWithNormalZombie(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x52FCF0, 0x83, 0x46, 0x40, 0x00);
	}

	public static void bombWithoutHole(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x466884 + 3, 0x64, 0x00, 0x00, 0x00);
	}

	public static void magentShroomWithoutRemainTime(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x461637 + 3, 0x64, 0x00, 0x00, 0x00);
	}

	public static void chomperWithoutRemainTime(Pointer hprocess) {
		simpleMemoryWrite(hprocess, 0x46154E + 3, 0x64, 0x00, 0x00, 0x00);
	}

	private static int simpleIntPointsRead(Pointer hprocess, long baseAddress, long[] offset) {
		int ret = -1;
		IntPointer buf = new IntPointer(1);
		try {
			Utils32.pointsRead(hprocess, new CEAddressPointer(baseAddress), offset, new LPCVOID(buf), 4);
		} finally {
			IntPointer.free(buf);
		}
		return ret;
	}

	private static void simpleIntPointsWrite(Pointer hprocess, long baseAddress, long[] offset, int value) {
		IntPointer buf = new IntPointer(1);
		buf.put(value);
		try {
			Utils32.pointsWrite(hprocess, new CEAddressPointer(baseAddress), offset, new LPCVOID(buf), 4);
		} finally {
			IntPointer.free(buf);
		}
	}

	public static void giveMeSunshine(Pointer hprocess) {
		simpleIntPointsWrite(hprocess, 0x6A9F38, new long[] { 0x768, 0x5560 }, 500000);
	}

	public static void giveMeFertilizer(Pointer hprocess) {
		simpleIntPointsWrite(hprocess, 0x6A9F38, new long[] { 0x82C, 0x1F8 }, 11000);
	}

	public static void giveMePesticide(Pointer hprocess) {
		simpleIntPointsWrite(hprocess, 0x6A9F38, new long[] { 0x82C, 0x1FC }, 11000);
	}

	public static void giveMeChocolate(Pointer hprocess) {
		simpleIntPointsWrite(hprocess, 0x6A9F38, new long[] { 0x82C, 0x1F8 + 0x30 }, 11000);
	}

	public static void giveMeSuperFertilizer(Pointer hprocess) {
		simpleIntPointsWrite(hprocess, 0x6A9F38, new long[] { 0x82C, 0x1F8 + 0x38 }, 11000);
	}

	public static void giveMeTreeOfWisdom(Pointer hprocess) {
		long baseAddress = 0x6A9F38;
		long[] offset = { 0x82C, 0xF4 };
		int before = simpleIntPointsRead(hprocess, baseAddress, offset);
		simpleIntPointsWrite(hprocess, baseAddress, offset, before + 10000);
	}

	public static void giveMeGold(Pointer hprocess) {
		long baseAddress = 0x6A9F38;
		long[] offset = { 0x82C, 0x28 };
		int before = simpleIntPointsRead(hprocess, baseAddress, offset);
		simpleIntPointsWrite(hprocess, baseAddress, offset, before + 10000);
	}

	public static void destoryArmor(Pointer hprocess) {
		{
			simpleMemoryWrite(hprocess, 0x531044, 0x29, 0xC9);
		}
		{
			long originAddress = 0x00530CA1;
			byte newOriginCode[] = Utils32.intArrayToByteArray(0xE9, 0x5A, 0xF3, 0x48, 0x00, 0x90);
			byte injectCode[] = Utils32.intArrayToByteArray(0x89, 0x86, 0xDC, 0x00, 0x00, 0x00, 0x29, 0x86, 0xDC, 0x00,
					0x00, 0x00, 0xE9, 0x96, 0x0C, 0xB7, 0xFF);
			Utils32.codeInject(hprocess, new CEAddressPointer(originAddress), new CEAddressPointer(originAddress + 5),
					newOriginCode, injectCode);
		}
	}
	
	public static void main(String[] args) {
		List<CEProcess> processes = Utils32.getPidWithProcessNamePattern("popcapgame1\\.exe");
		if (processes.isEmpty()) {
			throw new NoSuchElementException("No such process.");
		}
		CEProcess handle = processes.get(0);
		Pointer hprocess = windows.OpenProcess((int) windows.PROCESS_ALL_ACCESS, false, handle.getPid());
		
		destoryArmor(hprocess);
		plantsWithoutRemainTime(hprocess);
		plantsAliveForeverWithNormalZombie(hprocess);
		fireUnlimited(hprocess);
		bombWithoutHole(hprocess);
		chomperWithoutRemainTime(hprocess);
		firstSunshineCount(hprocess);
		giveMeSunshine(hprocess);
		
		windows.CloseHandle(hprocess);
		{
			String msg = Utils32.getLastErrorMessage();
			if(msg != null) {
				throw new RuntimeException("Kernel Dll Error: " + msg);
			}
		}
		System.out.println("Finished.");
	}

}
