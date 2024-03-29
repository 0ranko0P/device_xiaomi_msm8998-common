#
# Copyright (C) 2018-2020 The LineageOS Project
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

# Camera
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    vendor.camera.aux.packagelist=com.android.camera,org.lineageos.snap

# DPM
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    persist.vendor.dpm.feature=1 \
    persist.vendor.dpm.nsrm.bkg.evt=3955

# Graphics
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.hardware.egl=adreno \
    ro.hardware.vulkan=adreno \
    ro.sf.blurs_are_expensive=1 \
    ro.surface_flinger.supports_background_blur=0

ifeq ($(TARGET_BUILD_VARIANT),user)
PRODUCT_SYSTEM_PROPERTIES += \
    persist.vendor.ims.disableADBLogs=1 \
    persist.vendor.ims.disableDebugLogs=1 \
    persist.vendor.ims.disableIMSLogs=1 \
    persist.vendor.ims.disableQXDMLogs=1 \
    persist.vendor.ims.loglevel=0 \
    persist.vendor.ims.rtp.enableqxdm=0 \
    persist.vendor.ims.vt.enableadb=0
endif
