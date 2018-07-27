/**
 *        __       __               __ 
 *   ____/ /_ ____/ /______ _ ___  / /_
 *  / __  / / ___/ __/ ___/ / __ `/ __/
 * / /_/ / (__  ) / / /  / / /_/ / / 
 * \__,_/_/____/_/ /_/  /_/\__, /_/ 
 *                           / / 
 *                           \/ 
 * http://distriqt.com
 *
 * @brief
 * @author marchbold
 * @created 11/07/2018
 * @copyright http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.controller;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

public class SystemUtils
{


	/**
	 * Get max cpu rate.
	 *
	 * This works by examining the list of CPU frequencies in the pseudo file
	 * "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state" and how much time has been spent
	 * in each. It finds the highest non-zero time and assumes that is the maximum frequency (note
	 * that sometimes frequencies higher than that which was designed can be reported.) So it is not
	 * impossible that this method will return an incorrect CPU frequency.
	 *
	 * Also note that (obviously) this will not reflect different CPU cores with different
	 * maximum speeds.
	 *
	 * @return cpu frequency in MHz
	 */
	public static int getMaxCPUFreqMHz()
	{
		int maxFreq = 0;

		//try {
		//
		//	RandomAccessFile reader = new RandomAccessFile( "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state", "r" );
		//
		//	boolean done = false;
		//	while ( ! done ) {
		//		String line = reader.readLine();
		//		if ( null == line ) {
		//			done = true;
		//			break;
		//		}
		//		String[] splits = line.split( "\\s+" );
		//		assert ( splits.length == 2 );
		//		int timeInState = Integer.parseInt( splits[1] );
		//		if ( timeInState > 0 ) {
		//			int freq = Integer.parseInt( splits[0] ) / 1000;
		//			if ( freq > maxFreq ) {
		//				maxFreq = freq;
		//			}
		//		}
		//	}
		//
		//} catch ( IOException ex ) {
		//	ex.printStackTrace();
		//}


		try
		{
			String cpuMaxFreq = "";
			RandomAccessFile reader = new RandomAccessFile( "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r" );
			cpuMaxFreq = reader.readLine();
			reader.close();

			maxFreq = Integer.parseInt( cpuMaxFreq );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return maxFreq;
	}


	public static int getNumberOfCores()
	{
		if (Build.VERSION.SDK_INT >= 17)
		{
			return Runtime.getRuntime().availableProcessors();
		}
		else
		{
			// Use saurabh64's answer
			return getNumCoresOldPhones();
		}
	}

	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	private static int getNumCoresOldPhones()
	{
		//Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter
		{
			@Override
			public boolean accept( File pathname )
			{
				//Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches( "cpu[0-9]+", pathname.getName() ))
				{
					return true;
				}
				return false;
			}
		}

		try
		{
			//Get directory containing CPU info
			File dir = new File( "/sys/devices/system/cpu/" );
			//Filter to only list the devices we care about
			File[] files = dir.listFiles( new CpuFilter() );
			//Return the number of cores (virtual CPU devices)
			return files.length;
		}
		catch (Exception e)
		{
			//Default to return 1 core
			return 1;
		}
	}






	public static long getTotalMemory( Context context )
	{
		ActivityManager actManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		actManager.getMemoryInfo(memInfo);
		return memInfo.totalMem;
	}


}
