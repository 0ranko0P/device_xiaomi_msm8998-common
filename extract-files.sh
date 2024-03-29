#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

LINEAGE_ROOT="${MY_DIR}"/../../..

HELPER="${LINEAGE_ROOT}/vendor/aosp/build/tools/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        -n | --no-cleanup )
                CLEAN_VENDOR=false
                ;;
        -k | --kang )
                KANG="--kang"
                ;;
        -s | --section )
                SECTION="${2}"; shift
                CLEAN_VENDOR=false
                ;;
        * )
                SRC="${1}"
                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi

function blob_fixup() {
    case "${1}" in
    lib64/libdpmframework.so)
        patchelf --add-needed "libshim_dpmframework.so" "${2}"
        ;;
    lib64/libwfdnative.so)
        patchelf --remove-needed "android.hidl.base@1.0.so" "${2}"
        ;;
    system_ext/etc/init/dpmd.rc)
        sed -i 's|/system/product/bin/dpmd|/system_ext/bin/dpmd|g' "${2}"
        ;;
    system_ext/etc/permissions/audiosphere.xml)
        sed -i 's|/system/framework/audiosphere.jar|/system_ext/framework/audiosphere.jar|g' "${2}"
        ;;
    system_ext/etc/permissions/com.qti.dpmframework.xml | system_ext/etc/permissions/dpmapi.xml | system_ext/etc/permissions/telephonyservice.xml)
        sed -i "s|/system/product/framework/|/system/system_ext/framework/|g" "${2}"
        ;;
    system_ext/etc/permissions/qcrilhook.xml)
        sed -i 's|/product/framework/qcrilhook.jar|/system_ext/framework/qcrilhook.jar|g' "${2}"
        ;;
    system_ext/lib64/lib-imsvideocodec.so)
        for LIBDPM_SHIM in $(grep -L "libshim_imsvt.so" "${2}"); do
            "${PATCHELF}" --add-needed "libshim_imsvt.so" "$LIBDPM_SHIM"
        done
        ;;
    vendor/etc/permissions/qti_libpermissions.xml)
        sed -i 's|name=\"android.hidl.manager-V1.0-java|name=\"android.hidl.manager@1.0-java|g' "${2}"
        ;;
    vendor/lib/hw/camera.msm8998.so)
        patchelf --remove-needed "android.hidl.base@1.0.so" "${2}"
        sed -i "s/service.bootanim.exit/service.bootanim.zzzz/g" "${2}"
        ;;
    vendor/lib/libFaceGrade.so)
        patchelf --remove-needed "libandroid.so" "${2}"
        ;;
    vendor/lib/libMiCameraHal.so)
        patchelf --replace-needed "libicuuc.so" "libicuuc-v28.so" "${2}"
        patchelf --replace-needed "libminikin.so" "libminikin-v28.so" "${2}"
        ;;
    vendor/lib/libarcsoft_beauty_shot.so)
        patchelf --remove-needed "libandroid.so" "${2}"
        ;;
    vendor/lib/libicuuc-v28.so)
        patchelf --set-soname "libicuuc-v28.so" "${2}"
        ;;
    vendor/lib/libminikin-v28.so)
        patchelf --set-soname "libminikin-v28.so" "${2}"
        ;;
    vendor/lib/libmmcamera2_sensor_modules.so)
        sed -i 's|/data/misc/camera/camera_lsc_caldata.txt|/data/vendor/camera/camera_lsc_calib.txt|g' "${2}"
        ;;
    vendor/lib/libmmcamera2_stats_modules.so)
        patchelf --remove-needed "libandroid.so" "${2}"
        ;;
    vendor/lib/libmpbase.so)
        patchelf --remove-needed "libandroid.so" "${2}"
        ;;
    esac
}

# Initialize the helper for common device
setup_vendor "${DEVICE_COMMON}" "${VENDOR}" "${LINEAGE_ROOT}" true "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" \
        "${KANG}" --section "${SECTION}"

extract "${MY_DIR}/proprietary-files-qc.txt" "${SRC}" \
        "${KANG}" --section "${SECTION}"

if [ -s "${MY_DIR}/../${DEVICE}/proprietary-files.txt" ]; then
    # Reinitialize the helper for device
    source "${MY_DIR}/../${DEVICE}/extract-files.sh"
    setup_vendor "${DEVICE}" "${VENDOR}" "${LINEAGE_ROOT}" false "${CLEAN_VENDOR}"

    extract "${MY_DIR}/../${DEVICE}/proprietary-files.txt" "${SRC}" \
            "${KANG}" --section "${SECTION}"
fi

"${MY_DIR}/setup-makefiles.sh"
