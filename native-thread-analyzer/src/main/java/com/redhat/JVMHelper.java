package com.redhat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class JVMHelper {
	
	
	private String Options;
	
	private String SysConfig;
	
	 private String version;
	 
	 private String osVersion;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public static final String OPTION_SIZE = "(b|B|k|K|m|M|g|G)";
	

	public String getOptions() {
		return Options;
	}

	public void setOptions(String options) {
		Options = options;
	}

	public String getSysConfig() {
		return SysConfig;
	}

	public void setSysConfig(String sysConfig) {
		SysConfig = sysConfig;
	}


	 /**
     * Thread stack size. Specified with either the <code>-Xss</code>, <code>-ss</code>, or
     * <code>-XX:ThreadStackSize</code> options. For example:
     * 
     * <pre>
     * -Xss128k
     * </pre>
     * 
     * <pre>
     * -XX:ThreadStackSize=128
     * </pre>
     * 
     * The <code>-Xss</code> options does not work on Solaris, only the <code>-XX:ThreadStackSize</code> option.
     * 
     * @return The JVM thread stack size setting, or null if not explicitly set.
     */
    public String getThreadStackSizeOption() {
        String regex = "(-(X)?(ss|X:ThreadStackSize=)(\\d{1,12})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The thread stack size value, or null if not set. For example:
     * 
     *         <pre>
     * 256K
     *         </pre>
     */
    public String getThreadStackSizeValue() {
        return getOptionValue(getThreadStackSizeOption());
    }

    /**
     * Whether or not the option to disable explicit garbage collection (<code>-XX:+DisableExplicitGC</code>) is used.
     * 
     * @return True if -XX:+DisableExplicitGC option exists, false otherwise.
     */
    public String getDisableExplicitGCOption() {
        String regex = "(-XX:\\+DisableExplicitGC)";
        return getJvmOption(regex);
    }

    /**
     * Minimum heap space. Specified with the <code>-Xms</code> option. For example:
     * 
     * <pre>
     * -Xms1024m
     * -XX:HeapSize=1234567890
     * </pre>
     * 
     * @return The minimum heap space, or null if not explicitly set.
     */
    public String getMinHeapOption() {
        String regex = "(-X(ms|X:InitialHeapSize=)(\\d{1,12})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The minimum heap space value, or null if not set. For example:
     * 
     *         <pre>
     * 2048M
     *         </pre>
     */
    public String getMinHeapValue() {
        return getOptionValue(getMinHeapOption());
    }

    /**
     * Maximum heap space. Specified with the <code>-Xmx</code> option. For example:
     * 
     * <pre>
     * -Xmx1024m
     * -XX:MaxHeapSize=1234567890
     * </pre>
     * 
     * @return The maximum heap space, or null if not explicitly set.
     */
    public String getMaxHeapOption() {
        String regex = "(-X(mx|X:MaxHeapSize=)(\\d{1,12})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The maximum heap space value, or null if not set. For example:
     * 
     *         <pre>
     * 2048M
     *         </pre>
     */
    public String getMaxHeapValue() {
        return getOptionValue(getMaxHeapOption());
    }

    /**
     * @return The maximum heap space in bytes, or 0 if not set.
     */
    public long getMaxHeapBytes() {
        long maxHeapBytes = 0;
        if (getMaxHeapValue() != null) {
            maxHeapBytes = convertOptionSizeToBytes(getMaxHeapValue());
        }
        return maxHeapBytes;
    }

    /**
     * @return The maximum perm space in bytes, or 0 if not set.
     */
    public long getMaxPermBytes() {
        long maxPermBytes = 0;
        if (getMaxPermValue() != null) {
            maxPermBytes = convertOptionSizeToBytes(getMaxPermValue());
        }
        return maxPermBytes;
    }

    /**
     * @return The maximum metaspace in bytes, or 0 if not set.
     */
    public long getMaxMetaspaceBytes() {
        long maxMetaspaceBytes = 0;
        if (getMaxMetaspaceValue() != null) {
            maxMetaspaceBytes = convertOptionSizeToBytes(getMaxMetaspaceValue());
        }
        return maxMetaspaceBytes;
    }

    /**
     * 
     * @return True if the minimum and maximum heap space are set equal.
     */
    public boolean isMinAndMaxHeapSpaceEqual() {
        return getMaxHeapValue() != null && getMinHeapValue() != null
                && getMaxHeapValue().toUpperCase().equals(getMinHeapValue().toUpperCase());
    }

    /**
     * Minimum permanent generation space. Specified with the <code>-XX:PermSize</code> option. For example:
     * 
     * <pre>
     * -XX:PermSize=128M
     * </pre>
     * 
     * @return The minimum permanent generation space, or null if not explicitly set.
     */
    public String getMinPermOption() {
        String regex = "(-XX:PermSize=(\\d{1,12})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The minimum permanent generation space value, or null if not set. For example:
     * 
     *         <pre>
     * 128M
     *         </pre>
     */
    public String getMinPermValue() {
        return getOptionValue(getMinPermOption());
    }

    /**
     * Minimum Metaspace. Specified with the <code>-XX:MetsspaceSize</code> option. For example:
     * 
     * <pre>
     * -XX:MetaspaceSize=128M
     * </pre>
     * 
     * @return The minimum permanent generation space, or null if not explicitly set.
     */
    public String getMinMetaspaceOption() {
        String regex = "(-XX:MetaspaceSize=(\\d{1,10})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The minimum Metaspace value, or null if not set. For example:
     * 
     *         <pre>
     * 128M
     *         </pre>
     */
    public String getMinMetaspaceValue() {
        return getOptionValue(getMinMetaspaceOption());
    }

    /**
     * Maximum permanent generation space (<code>-XX:MaxPermSize</code>). For example:
     * 
     * <pre>
     * -XX:MaxPermSize=128M
     * </pre>
     * 
     * @return The maximum permanent generation space, or null if not explicitly set.
     */
    public String getMaxPermOption() {
        String regex = "(-XX:MaxPermSize=(\\d{1,10})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The maximum permanent generation space value, or null if not set. For example:
     * 
     *         <pre>
     * 128M
     *         </pre>
     */
    public String getMaxPermValue() {
        return getOptionValue(getMaxPermOption());
    }

    /**
     * Maximum Metaspace (<code>-XX:MaxMetaspaceSize</code>). For example:
     * 
     * <pre>
     * -XX:MaxMetaspaceSize=128M
     * </pre>
     * 
     * @return The maximum Metaspace, or null if not explicitly set.
     */
    public String getMaxMetaspaceOption() {
        String regex = "(-XX:MaxMetaspaceSize=(\\d{1,10})(" + OPTION_SIZE + ")?)";
        return getJvmOption(regex);
    }

    /**
     * @return The maximum Metaspace value, or null if not set. For example:
     * 
     *         <pre>
     * 128M
     *         </pre>
     */
    public String getMaxMetaspaceValue() {
        return getOptionValue(getMaxMetaspaceOption());
    }

    /**
     * Client Distributed Garbage Collection (DGC) interval in milliseconds.
     * 
     * <pre>
     * -Dsun.rmi.dgc.client.gcInterval=14400000
     * </pre>
     * 
     * @return The client Distributed Garbage Collection (DGC), or null if not explicitly set.
     */
    public String getRmiDgcClientGcIntervalOption() {
        String regex = "(-Dsun.rmi.dgc.client.gcInterval=(\\d{1,12}))";
        return getJvmOption(regex);
    }

    /**
     * @return The client Distributed Garbage Collection (DGC) interval value in (milliseconds), or null if not set. For
     *         example:
     * 
     *         <pre>
     *         14400000
     *         </pre>
     */
    public String getRmiDgcClientGcIntervalValue() {
        return getOptionValue(getRmiDgcClientGcIntervalOption());
    }

    /**
     * Server Distributed Garbage Collection (DGC) interval in milliseconds.
     * 
     * <pre>
     * -Dsun.rmi.dgc.server.gcInterval=14400000
     * </pre>
     * 
     * @return The server Distributed Garbage Collection (DGC), or null if not explicitly set.
     */
    public String getRmiDgcServerGcIntervalOption() {
        String regex = "(-Dsun.rmi.dgc.server.gcInterval=(\\d{1,12}))";
        return getJvmOption(regex);
    }

    /**
     * @return The server Distributed Garbage Collection (DGC) interval value in (milliseconds), or null if not set. For
     *         example:
     * 
     *         <pre>
     *         14400000
     *         </pre>
     */
    public String getRmiDgcServerGcIntervalValue() {
        return getOptionValue(getRmiDgcServerGcIntervalOption());
    }

    /**
     * The option to disable writing out a heap dump when OutOfMemoryError. For example:
     * 
     * <pre>
     * -XX:-HeapDumpOnOutOfMemoryError
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getHeapDumpOnOutOfMemoryErrorDisabledOption() {
        String regex = "(-XX:-HeapDumpOnOutOfMemoryError)";
        return getJvmOption(regex);
    }

    /**
     * The option to write out a heap dump when OutOfMemoryError. For example:
     * 
     * <pre>
     * -XX:+HeapDumpOnOutOfMemoryError
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getHeapDumpOnOutOfMemoryErrorEnabledOption() {
        String regex = "(-XX:\\+HeapDumpOnOutOfMemoryError)";
        return getJvmOption(regex);
    }

    /**
     * -javaagent instrumentation option. For example:
     * 
     * <pre>
     * -javaagent:byteman.jar=script:kill-3.btm,boot:byteman.jar
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getJavaagentOption() {
        String regex = "(-javaagent:[\\S]+)";
        return getJvmOption(regex);
    }

    /**
     * agentpath instrumentation option. For example:
     * 
     * <pre>
     * -agentpath:/path/to/agent.so
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getAgentpathOption() {
        String regex = "(-agentpath:[\\S]+)";
        return getJvmOption(regex);
    }

    /**
     * The option to disable background compilation of bytecode. For example:
     * 
     * <pre>
     * -Xbatch
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getXBatchOption() {
        String regex = "(-Xbatch)";
        return getJvmOption(regex);
    }

    /**
     * The option to disable background compilation of bytecode. For example:
     * 
     * <pre>
     * -XX:-BackgroundCompilation
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getDisableBackgroundCompilationOption() {
        String regex = "(-XX:-BackgroundCompilation)";
        return getJvmOption(regex);
    }

    /**
     * The option to enable compilation of bytecode on first invocation. For example:
     * 
     * <pre>
     * -Xcomp
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getXCompOption() {
        String regex = "(-Xcomp)";
        return getJvmOption(regex);
    }

    /**
     * The option to disable just in time (JIT) compilation. For example:
     * 
     * <pre>
     * -Xint
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getXIntOption() {
        String regex = "(-Xint)";
        return getJvmOption(regex);
    }

    /**
     * The option to allow explicit garbage collection to be handled concurrently by the CMS and G1 collectors. For
     * example:
     * 
     * <pre>
     * -XX:+ExplicitGCInvokesConcurrent
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getExplicitGcInvokesConcurrentOption() {
        String regex = "(-XX:\\+ExplicitGCInvokesConcurrent)";
        return getJvmOption(regex);
    }

    /**
     * The option to output JVM command line options at the beginning of gc logging. For example:
     * 
     * <pre>
     * -XX:+PrintCommandLineFlags
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintCommandLineFlagsOption() {
        String regex = "(-XX:\\+PrintCommandLineFlags)";
        return getJvmOption(regex);
    }

    /**
     * The option to output details at gc. For example:
     * 
     * <pre>
     * -XX:+PrintGCDetails
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintGCDetailsOption() {
        String regex = "(-XX:\\+PrintGCDetails)";
        return getJvmOption(regex);
    }

    /**
     * The option to output details at gc disabled. For example:
     * 
     * <pre>
     * -XX:-PrintGCDetails
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintGCDetailsDisabled() {
        String regex = "(-XX:\\-PrintGCDetails)";
        return getJvmOption(regex);
    }

    /**
     * The option for the CMS young collector. For example:
     * 
     * <pre>
     * -XX:+UseParNewGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseParNewGCOption() {
        String regex = "(-XX:\\+UseParNewGC)";
        return getJvmOption(regex);
    }

    /**
     * The option to disable the PAR_NEW collector. For example:
     * 
     * <pre>
     * -XX:-UseParNewGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseParNewGcDisabled() {
        String regex = "(-XX:\\-UseParNewGC)";
        return getJvmOption(regex);
    }

    /**
     * The option for the CMS old collector. For example:
     * 
     * <pre>
     * -XX:+UseConcMarkSweepGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseConcMarkSweepGCOption() {
        String regex = "(-XX:\\+UseConcMarkSweepGC)";
        return getJvmOption(regex);
    }

    /**
     * The option for allowing the CMS collector to collect Perm/Metaspace. For example:
     * 
     * <pre>
     * -XX:+CMSClassUnloadingEnabled
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getCMSClassUnloadingEnabled() {
        String regex = "(-XX:\\+CMSClassUnloadingEnabled)";
        return getJvmOption(regex);
    }

    /**
     * The option for disabling the CMS collector to collect Perm/Metaspace. For example:
     * 
     * <pre>
     * -XX:-CMSClassUnloadingEnabled
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getCMSClassUnloadingDisabled() {
        String regex = "(-XX:\\-CMSClassUnloadingEnabled)";
        return getJvmOption(regex);
    }

    /**
     * The option for outputting times for reference processing (weak, soft,JNI). For example:
     * 
     * <pre>
     * -XX:+PrintReferenceGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintReferenceGC() {
        String regex = "(-XX:\\+PrintReferenceGC)";
        return getJvmOption(regex);
    }

    /**
     * The option for printing trigger information. For example:
     * 
     * <pre>
     * -XX:+PrintGCCause
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintGCCause() {
        String regex = "(-XX:\\+PrintGCCause)";
        return getJvmOption(regex);
    }

    /**
     * The option for printing trigger information disabled. For example:
     * 
     * <pre>
     * -XX:-PrintGCCause
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintGCCauseDisabled() {
        String regex = "(-XX:\\-PrintGCCause)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling tiered compilation. For example:
     * 
     * <pre>
     * -XX:+TieredCompilation
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getTieredCompilation() {
        String regex = "(-XX:\\+TieredCompilation)";
        return getJvmOption(regex);
    }

    /**
     * The option for string deduplication statistics. For example:
     * 
     * <pre>
     * -XX:+PrintStringDeduplicationStatistics
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintStringDeduplicationStatistics() {
        String regex = "(-XX:\\+PrintStringDeduplicationStatistics)";
        return getJvmOption(regex);
    }

    /**
     * The option for setting CMS initiating occupancy fraction, the tenured generation occupancy percentage that
     * triggers a concurrent collection. For example:
     * 
     * <pre>
     * -XX:CMSInitiatingOccupancyFraction=70
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getCMSInitiatingOccupancyFraction() {
        String regex = "(-XX:CMSInitiatingOccupancyFraction=\\d{1,3})";
        return getJvmOption(regex);
    }

    /**
     * The option for disabling heuristics (calculating anticipated promotions) and use only the occupancy fraction to
     * determine when to trigger a CMS cycle. When an application has large variances in object allocation and young
     * generation promotion rates, the CMS collector is not able to accurately predict when to start the CMS cycle. For
     * example:
     * 
     * <pre>
     * -XX:+UseCMSInitiatingOccupancyOnly
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getCMSInitiatingOccupancyOnlyEnabled() {
        String regex = "(-XX:\\+UseCMSInitiatingOccupancyOnly)";
        return getJvmOption(regex);
    }

    /**
     * The option for bias locking disabled. For example:
     * 
     * <pre>
     * -XX:-UseBiasedLocking
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getBiasedLockingDisabled() {
        String regex = "(-XX:\\-UseBiasedLocking)";
        return getJvmOption(regex);
    }

    /**
     * The option for printing a class histogram when a thread dump is taken:
     * 
     * <pre>
     * -XX:+PrintClassHistogram
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintClassHistogramEnabled() {
        String regex = "(-XX:\\+PrintClassHistogram)\\b";
        return getJvmOption(regex);
    }

    /**
     * The option for printing a class histogram before every full collection.
     * 
     * <pre>
     * -XX:+PrintClassHistogramAfterFullGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintClassHistogramAfterFullGcEnabled() {
        String regex = "(-XX:\\+PrintClassHistogramAfterFullGC)";
        return getJvmOption(regex);
    }

    /**
     * The option for printing a class histogram before every full collection:
     * 
     * <pre>
     * -XX:+PrintClassHistogramBeforeFullGC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintClassHistogramBeforeFullGcEnabled() {
        String regex = "(-XX:\\+PrintClassHistogramBeforeFullGC)";
        return getJvmOption(regex);
    }

    /**
     * The option for printing application concurrent time:
     * 
     * <pre>
     * -XX:+PrintGCApplicationConcurrentTime
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintGcApplicationConcurrentTime() {
        String regex = "(-XX:\\+PrintGCApplicationConcurrentTime)";
        return getJvmOption(regex);
    }

    /**
     * The option for trace class unloading output:
     * 
     * <pre>
     * -XX:+TraceClassUnloading
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getTraceClassUnloading() {
        String regex = "(-XX:\\+TraceClassUnloading)";
        return getJvmOption(regex);
    }

    /**
     * The option for disabling compressed object references.
     * 
     * <pre>
     * -XX:-UseCompressedOops
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseCompressedOopsDisabled() {
        String regex = "(-XX:\\-UseCompressedOops)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling compressed object references.
     * 
     * <pre>
     * -XX:+UseCompressedOops
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseCompressedOopsEnabled() {
        String regex = "(-XX:\\+UseCompressedOops)";
        return getJvmOption(regex);
    }

    /**
     * The option for disabling log file rotation.
     * 
     * <pre>
     * -XX:-UseGCLogFileRotation
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseGcLogFileRotationDisabled() {
        String regex = "(-XX:\\-UseGCLogFileRotation)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling log file rotation.
     * 
     * <pre>
     * -XX:+UseGCLogFileRotation
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseGcLogFileRotationEnabled() {
        String regex = "(-XX:\\+UseGCLogFileRotation)";
        return getJvmOption(regex);
    }

    /**
     * The option for setting number of log files to rotate. For example:
     * 
     * <pre>
     * -XX:NumberOfGCLogFiles=5
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getNumberOfGcLogFiles() {
        String regex = "(-XX:NumberOfGCLogFiles=\\d{1,2})";
        return getJvmOption(regex);
    }

    /**
     * The option for compressed class pointers enabled.
     * 
     * <pre>
     * -XX:+UseCompressedClassPointers
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseCompressedClassPointersEnabled() {
        String regex = "(-XX:\\+UseCompressedClassPointers)";
        return getJvmOption(regex);
    }

    /**
     * The option for compressed class pointers disabled.
     * 
     * <pre>
     * -XX:-UseCompressedClassPointers
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseCompressedClassPointersDisabled() {
        String regex = "(-XX:\\-UseCompressedClassPointers)";
        return getJvmOption(regex);
    }

    /**
     * The option for setting CompressedClassSpaceSize.
     * 
     * <pre>
     * -XX:CompressedClassSpaceSize=768m
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getCompressedClassSpaceSizeOption() {
        String regex = "(-XX:CompressedClassSpaceSize=((\\d{1,10})(" + OPTION_SIZE + ")?))";
        return getJvmOption(regex);
    }

    /**
     * @return The compressed class space size value, or null if not set. For example:
     * 
     *         <pre>
     * 768m
     *         </pre>
     */
    public String getCompressedClassSpaceSizeValue() {
        return getOptionValue(getCompressedClassSpaceSizeOption());
    }

    /**
     * @return The compressed class space in bytes, or 0 if not set.
     */
    public long getCompressedClassSpaceSizeBytes() {
        long compressedClassSpaceSizeBytes = 0;
        if (getCompressedClassSpaceSizeValue() != null) {
            compressedClassSpaceSizeBytes = convertOptionSizeToBytes(getCompressedClassSpaceSizeValue());
        }
        return compressedClassSpaceSizeBytes;
    }

    /**
     * The option for outputting statistics for the CMS FreeListSpace.
     * 
     * <pre>
     * -XX:PrintFLSStatistics=1
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintFLStatistics() {
        String regex = "(-XX:PrintFLSStatistics=(\\d))";
        return getJvmOption(regex);
    }

    /**
     * The option for outputting tenuring distribution information.
     * 
     * <pre>
     * -XX:+PrintTenuringDistribution
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintTenuringDistribution() {
        String regex = "(-XX:\\+PrintTenuringDistribution)";
        return getJvmOption(regex);
    }

    /**
     * The option for explicit gc invokes concurrent and unloads classes disabled.
     * 
     * <pre>
     * -XX:-ExplicitGCInvokesConcurrentAndUnloadsClasses
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getExplicitGcInvokesConcurrentAndUnloadsClassesDisabled() {
        String regex = "(-XX:\\-ExplicitGCInvokesConcurrentAndUnloadsClasses)";
        return getJvmOption(regex);
    }

    /**
     * The option for class unloading disabled.
     * 
     * <pre>
     * -XX:-ClassUnloading
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getClassUnloadingDisabled() {
        String regex = "(-XX:\\-ClassUnloading)";
        return getJvmOption(regex);
    }

    /**
     * The option for specifying 64-bit.
     * 
     * <pre>
     * -d64
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getD64() {
        String regex = "(-d64)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling printing promotion failure information.
     * 
     * <pre>
     * -XX:+PrintPromotionFailure
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintPromotionFailureEnabled() {
        String regex = "(-XX:\\+PrintPromotionFailure)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling a strict memory barrier.
     * 
     * <pre>
     * -XX:+UseMembar
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseMembarEnabled() {
        String regex = "(-XX:\\+UseMembar)";
        return getJvmOption(regex);
    }

    /**
     * The option for disabling Adaptive Resize Policy output.
     * 
     * <pre>
     * -XX:-PrintAdaptiveSizePolicy
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintAdaptiveResizePolicyDisabled() {
        String regex = "(-XX:\\-PrintAdaptiveSizePolicy)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling Adaptive Resize Policy output.
     * 
     * <pre>
     * -XX:+PrintAdaptiveSizePolicy
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getPrintAdaptiveResizePolicyEnabled() {
        String regex = "(-XX:\\+PrintAdaptiveSizePolicy)";
        return getJvmOption(regex);
    }

    /**
     * The option for setting the maximum tenuring threshold option (the number of times objects surviving a young
     * collection are copied to a survivor space).
     * 
     * <pre>
     * -XX:MaxTenuringThreshold=0
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getMaxTenuringThresholdOption() {
        String regex = "(-XX:MaxTenuringThreshold=(\\d+))";
        return getJvmOption(regex);
    }

    /**
     * The option for setting the size of the eden space compared to ONE survivor space.
     * 
     * <pre>
     * -XX:SurvivorRatio=6
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getSurvivorRatio() {
        String regex = "(-XX:SurvivorRatio=(\\d+))";
        return getJvmOption(regex);
    }

    /**
     * The option for setting the percentage of the survivor space allowed to be occupied.
     * 
     * <pre>
     * -XX:TargetSurvivorRatio=90
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getTargetSurvivorRatio() {
        String regex = "(-XX:TargetSurvivorRatio=(\\d{1,3}))";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling experimental JVM options.
     * 
     * <pre>
     * -XX:+UnlockExperimentalVMOptions
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUnlockExperimentalVmOptionsEnabled() {
        String regex = "(-XX:\\+UnlockExperimentalVMOptions)";
        return getJvmOption(regex);
    }

    /**
     * The option for enabling fast unordered timestamps.
     * 
     * <pre>
     * -XX:+UseFastUnorderedTimeStamps
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseFastUnorderedTimeStampsEnabled() {
        String regex = "(-XX:\\+UseFastUnorderedTimeStamps)";
        return getJvmOption(regex);
    }

    /**
     * The option for setting the occupancy threshold for a region to be considered as a candidate region for a
     * G1_CLEANUP collection. For example:
     * 
     * <pre>
     * -XX:G1MixedGCLiveThresholdPercent=85
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getG1MixedGCLiveThresholdPercent() {
        String regex = "(-XX:G1MixedGCLiveThresholdPercent=\\d{1,3})";
        return getJvmOption(regex);
    }

    /**
     * @return The occupancy threshold percent for a region to be considered as a candidate region for a G1_CLEANUP
     *         collection, or null if not set. For example:
     * 
     *         <pre>
     *         85
     *         </pre>
     */
    public String getG1MixedGCLiveThresholdPercentValue() {
        // Cannot use getOptionValue() because the option name has a number in it.
        String value = null;
        if (getG1MixedGCLiveThresholdPercent() != null) {
            String regex = "^-XX:G1MixedGCLiveThresholdPercent=(\\d{1,3})$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(getG1MixedGCLiveThresholdPercent());
            if (matcher.find()) {
                value = matcher.group(1);
            }
        }
        return value;
    }

    /**
     * The option for setting the G1 heap waste percentage. For example:
     * 
     * <pre>
     * -XX:G1HeapWastePercent=5
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getG1HeapWastePercent() {
        String regex = "(-XX:G1HeapWastePercent=\\d{1,3})";
        return getJvmOption(regex);
    }

    /**
     * @return The G1 heap waste percentage, or null if not set. For example:
     * 
     *         <pre>
     *         5
     *         </pre>
     */
    public String getG1HeapWastePercentValue() {
        // Cannot use getOptionValue() because the option name has a number in it.
        String value = null;
        if (getG1HeapWastePercent() != null) {
            String regex = "^-XX:G1HeapWastePercent=(\\d{1,3})$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(getG1HeapWastePercent());
            if (matcher.find()) {
                value = matcher.group(1);
            }
        }
        return value;
    }

    /**
     * The option for specifying the G1 collector. For example:
     * 
     * <pre>
     * -XX:+UseG1GC
     * </pre>
     * 
     * @return the option if it exists, null otherwise.
     */
    public String getUseG1Gc() {
        String regex = "(-XX:\\+UseG1GC)";
        return getJvmOption(regex);
    }

    /**
     * 
     * @return True if the minimum and maximum permanent generation space are set equal.
     */
    public boolean isMinAndMaxPermSpaceEqual() {
        return (getMinPermValue() == null && getMaxPermValue() == null) || (getMinPermValue() != null
                && getMaxPermValue() != null && convertOptionSizeToBytes(getMinPermValue()) == convertOptionSizeToBytes(getMaxPermValue()));
    }

    /**
     * 
     * @return True if the minimum and maximum Metaspace are set equal.
     */
    public boolean isMinAndMaxMetaspaceEqual() {
        return (getMinMetaspaceValue() == null && getMaxMetaspaceValue() == null) || (getMinMetaspaceValue() != null
                && getMaxMetaspaceValue() != null && convertOptionSizeToBytes(getMinMetaspaceValue()) == convertOptionSizeToBytes(getMaxMetaspaceValue()));
    }

    /**
     * @param regex
     *            The option regular expression.
     * @return The JVM option, or null if not explicitly set.
     */
    public String getJvmOption(final String regex) {
        String option = null;
        if (Options != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(Options);
            if (matcher.find()) {
                option = matcher.group(1);
            }
        }
        return option;
    }

    /**
     * @return True if stack size &gt;= 1024k, false otherwise.
     */
    public boolean hasLargeThreadStackSize() {
        boolean hasLargeThreadStackSize = false;

        String threadStackSize = getThreadStackSizeValue();
        if (threadStackSize != null
                && convertOptionSizeToBytes(threadStackSize) >= Constants.MEGABYTE.longValue()) {
            hasLargeThreadStackSize = true;
        }

        return hasLargeThreadStackSize;
    }

    /**
     * @return The JDK version (e.g. '8'), or `0` if it could not be determined.
     */
    public int JdkNumber() {
        String regex = "^.+version = 1\\.(5|6|7|8|9).+$";
        int number = 0;
        if (version != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(version);
            if (matcher.find()) {
                if (matcher.group(1) != null) {
                    number = Integer.parseInt(matcher.group(1));
                }
            }
        }
        return number;
    }

    /**
     * @return The JDK update (e.g. '60'), or `0` if it could not be determined.
     */
    public int JdkUpdate() {
        String regex = "^.+version = \\(1\\.(5|6|7|8|9)\\.\\d_(\\d{1,3}).+$";
        int number = 0;
        if (version != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(version);
            if (matcher.find()) {
                if (matcher.group(1) != null) {
                    number = Integer.parseInt(matcher.group(2));
                }
            }
        }
        return number;
    }

    /**
     * @return True if 64 bit, false otherwise.
     */
    public boolean is64Bit() {
        boolean is64BitVersion = false;
        if (version != null) {
            is64BitVersion = version.matches("^.+64-Bit.+$");
        }
        boolean is64BitOption = false;
        if (getD64() != null) {
            is64BitOption = true;
        }
        return is64BitVersion || is64BitOption;
    }
    
    public boolean isOS64Bit() {
        boolean is64BitVersion = false;
        if (osVersion != null) {
            is64BitVersion = osVersion.matches("^.+_64.*$");
        }
        return is64BitVersion ;
    }

    
    public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public static final String getOptionValue(String option) {
        String value = null;
        if (option != null) {
            String regex = "^-[a-zA-Z:.]+(=)?(\\d{1,12}(" + OPTION_SIZE + ")?)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(option);
            if (matcher.find()) {
                value = matcher.group(2);
            }
        }
        return value;
    }
    
    public static long convertOptionSizeToBytes(final String size) {

        String regex = "(\\d{1,12})(" + OPTION_SIZE + ")?";

        String value = null;
        char units = 'b';

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(size);
        if (matcher.find()) {
            value = matcher.group(1);
            if (matcher.group(2) != null) {
                units = matcher.group(2).charAt(0);
            }
        }

        BigDecimal bytes = new BigDecimal(value);

        switch (units) {

        case 'b':
        case 'B':
            // do nothing
            break;
        case 'k':
        case 'K':
            bytes = bytes.multiply(Constants.KILOBYTE);
            break;
        case 'm':
        case 'M':
            bytes = bytes.multiply(Constants.MEGABYTE);
            break;
        case 'g':
        case 'G':
            bytes = bytes.multiply(Constants.GIGABYTE);
            break;
        default:
            throw new AssertionError("Unexpected units value: " + units);

        }
        return bytes.longValue();
    }

    
}
