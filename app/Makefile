# Usage:
# make install
# make reinstall
# make uninstall
# make clean

APPNAME := vocabulary
LABEL := $(APPNAME)
APKFILE := $(APPNAME).apk
PACKAGENAME := org.mercury0114.$(APPNAME)
ANDROIDVERSION := 30
ANDROIDTARGET := $(ANDROIDVERSION)

ifeq ($(shell uname), Linux)
OS_NAME = linux-x86_64
endif
ifeq ($(shell uname), Darwin)
OS_NAME = darwin-x86_64
endif

ANDROIDSDK := ~/Android/Sdk
ifeq ($(ANDROIDSDK),)
$(error ANDROIDSDK directory not found)
endif

NDK := $(firstword $(wildcard $(ANDROIDSDK)/ndk/*))
ifeq ($(NDK),)
$(error NDK directory not found)
endif

BUILD_TOOLS := $(lastword $(wildcard $(ANDROIDSDK)/build-tools/*) )
ifeq ($(BUILD_TOOLS),)
$(error BUILD_TOOLS directory not found)
endif

ANDROIDSRCS := android_native_app_glue.c 
ANDROIDSRCS += android_main.c
ANDROIDSRCS += rawdraw/CNFG3D.c
ANDROIDSRCS += rawdraw/CNFGFunctions.c

CFLAGS := -ffunction-sections -Os -fdata-sections -Wall -fvisibility=hidden
CFLAGS += -Os -DANDROID -DAPPNAME=\"$(APPNAME)\"
CFLAGS += -DANDROID_FULLSCREEN
CFLAGS += -I.
CFLAGS += -I./rawdraw -I$(NDK)/sysroot/usr/include
CFLAGS += -I$(NDK)/sysroot/usr/include/android
CFLAGS += -I$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/include/android
CFLAGS += -I$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/include/GLES

LDFLAGS := -Wl,--gc-sections -s -Wall -lm -lGLESv3 -lEGL -landroid -llog
LDFLAGS += -shared -uANativeActivity_onCreate

CC_ARM64 := $(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/bin/aarch64-linux-android$(ANDROIDVERSION)-clang
CC_ARM32 := $(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/bin/armv7a-linux-androideabi$(ANDROIDVERSION)-clang

# Which binaries to build? Just comment/uncomment these lines:
TARGETS += makecapk/lib/arm64-v8a/lib$(APPNAME).so
TARGETS += makecapk/lib/armeabi-v7a/lib$(APPNAME).so

STOREPASS := password
DNAME := "CN=example.com, OU=ID, O=Example, L=Doe, S=John, C=GB"
KEYSTOREFILE := my-release-key.keystore

clean :
	rm -rf $(APPNAME).apk.idsig $(KEYSTOREFILE) temp.apk makecapk.apk makecapk $(APKFILE) AndroidManifest.xml

uninstall : 
	adb uninstall $(PACKAGENAME)

AndroidManifest.xml :
	PACKAGENAME=$(PACKAGENAME) \
		ANDROIDVERSION=$(ANDROIDVERSION) \
		ANDROIDTARGET=$(ANDROIDTARGET) \
		APPNAME=$(APPNAME) \
		LABEL=$(LABEL) envsubst '$$ANDROIDTARGET $$ANDROIDVERSION $$APPNAME $$PACKAGENAME $$LABEL' \
		< AndroidManifest.xml.template > AndroidManifest.xml

$(KEYSTOREFILE) : clean
	keytool -genkey -v -keystore $(KEYSTOREFILE) -alias standkey -keyalg RSA -keysize 2048 -validity 10000 -storepass $(STOREPASS) -keypass $(STOREPASS) -dname $(DNAME)

makecapk/lib/arm64-v8a/lib$(APPNAME).so : $(ANDROIDSRCS)
	mkdir -p makecapk/lib/arm64-v8a
	$(CC_ARM64) $(CFLAGS) $(CFLAGS_ARM64) -o $@ $(ANDROIDSRCS) -L$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/lib/aarch64-linux-android/$(ANDROIDVERSION) $(LDFLAGS)

makecapk/lib/armeabi-v7a/lib$(APPNAME).so : $(ANDROIDSRCS)
	mkdir -p makecapk/lib/armeabi-v7a
	$(CC_ARM32) $(CFLAGS) $(CFLAGS_ARM32) -o $@ $(ANDROIDSRCS) -L$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/lib/arm-linux-androideabi/$(ANDROIDVERSION) $(LDFLAGS)

compile : $(TARGETS) $(KEYSTOREFILE) AndroidManifest.xml
	mkdir -p makecapk/assets
	cp -r Sources/assets/* makecapk/assets
	$(BUILD_TOOLS)/aapt package -f -F temp.apk -I $(ANDROIDSDK)/platforms/android-$(ANDROIDVERSION)/android.jar -M AndroidManifest.xml -S Sources/res -A makecapk/assets -v --target-sdk-version $(ANDROIDTARGET)
	unzip -o temp.apk -d makecapk
	cd makecapk && zip -D9r ../makecapk.apk . && zip -D0r ../makecapk.apk ./resources.arsc ./AndroidManifest.xml
	jarsigner -sigalg SHA1withRSA -digestalg SHA1 -verbose -keystore $(KEYSTOREFILE) -storepass $(STOREPASS) makecapk.apk standkey
	$(BUILD_TOOLS)/zipalign -v 4 makecapk.apk $(APKFILE)
	$(BUILD_TOOLS)/apksigner sign --key-pass pass:$(STOREPASS) --ks-pass pass:$(STOREPASS) --ks $(KEYSTOREFILE) $(APKFILE)

install : $(KEYSTOREFILE) compile
	adb install -r $(APKFILE)

reinstall : uninstall install
