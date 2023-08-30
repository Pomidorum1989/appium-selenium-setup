package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PerformanceData {

    @AllArgsConstructor
    @Getter
    public enum PerformanceDataType {
        BATTERY_INFO    ("battery"),
        CPU_INFO        ("cpu"),
        MEMORY_INfO     ("memory"),
        NETWORK_INFO    ("network");

        private final String value;

        public String getValue() {
            return this.value + "info";
        }
    }

    @AllArgsConstructor
    @Getter
    public enum CPUData {
        USER    ("user"),
        KERNEL  ("kernel");

        public final String value;
    }

    @AllArgsConstructor
    @Getter
    public enum MemoryData {
        DALVIK_PRIVATE_DIRTY        ("dalvikPrivateDirty"),
        DALVIK_PSS                  ("dalvikPss"),
        DALVIK_RSS                  ("dalvikRss"),
        EGL_PRIVATE_DIRTY           ("eglPrivateDirty"),
        EGL_PSS                     ("eglPss"),
        GL_PRIVATE_DIRTY            ("glPrivateDirty"),
        GL_PSS                      ("glPss"),
        NATIVE_HEAP_ALLOCATED_SIZE  ("nativeHeapAllocatedSize"),
        NATIVE_HEAP_SIZE            ("nativeHeapSize"),
        NATIVE_PRIVATE_DIRTY        ("nativePrivateDirty"),
        NATIVE_PSS                  ("nativePss"),
        NATIVE_RSS                  ("nativeRss"),
        TOTAL_PRIVATE_DIRTY         ("totalPrivateDirty"),
        TOTAL_PSS                   ("totalPss"),
        TOTAL_RSS                   ("totalRss");

        public final String value;
    }

    @AllArgsConstructor
    @Getter
    public enum NetworkData {
        ACTIVE_TIME     ("activeTime"),
        BUCKET_DURATION ("bucketDuration"),
        OP              ("op"),
        RB              ("rb"),
        RP              ("rp"),
        ST              ("st"),
        TB              ("tb"),
        TP              ("tp");

        public final String value;
    }

    @AllArgsConstructor
    @Getter
    public enum BatteryData {
        POWER("power");

        public final String value;
    }
}
