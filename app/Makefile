# Usage:
# make install
# make reinstall
# make uninstall
# make clean

APPNAME?=vocabulary
LABEL?=$(APPNAME)
APKFILE ?= $(APPNAME).apk
PACKAGENAME?=org.mercury0114.$(APPNAME)
RAWDRAWANDROID?=.
RAWDRAWANDROIDSRCS=android_native_app_glue.c
SRC?=test.c
ANDROIDVERSION?=30
ANDROIDTARGET?=$(ANDROIDVERSION)
CFLAGS?=-ffunction-sections -Os -fdata-sections -Wall -fvisibility=hidden
LDFLAGS?=-Wl,--gc-sections -s
ANDROIDSRCS:= $(SRC) $(RAWDRAWANDROIDSRCS)


UNAME := $(shell uname)
ifeq ($(UNAME), Linux)
OS_NAME = linux-x86_64
endif
ifeq ($(UNAME), Darwin)
OS_NAME = darwin-x86_64
endif

ANDROIDSDK := ~/Android/Sdk
ifeq ($(ANDROIDSDK),)
$(error ANDROIDSDK directory not found)
endif

NDK?=$(firstword $(wildcard $(ANDROIDSDK)/ndk/*))
ifeq ($(NDK),)
$(error NDK directory not found)
endif

BUILD_TOOLS?=$(lastword $(wildcard $(ANDROIDSDK)/build-tools/*) )
ifeq ($(BUILD_TOOLS),)
$(error BUILD_TOOLS directory not found)
endif

CFLAGS +=-Os -DANDROID -DAPPNAME=\"$(APPNAME)\"
CFLAGS +=-DANDROID_FULLSCREEN
CFLAGS += -I./rawdraw -I$(NDK)/sysroot/usr/include -I$(NDK)/sysroot/usr/include/android -I$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/include/android -I. -DANDROIDVERSION=$(ANDROIDVERSION)
LDFLAGS += -lm -lGLESv3 -lEGL -landroid -llog
LDFLAGS += -shared -uANativeActivity_onCreate

CC_ARM64:=$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/bin/aarch64-linux-android$(ANDROIDVERSION)-clang
CC_ARM32:=$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/bin/armv7a-linux-androideabi$(ANDROIDVERSION)-clang

# Which binaries to build? Just comment/uncomment these lines:
TARGETS += makecapk/lib/arm64-v8a/lib$(APPNAME).so
TARGETS += makecapk/lib/armeabi-v7a/lib$(APPNAME).so

STOREPASS?=password
DNAME:="CN=example.com, OU=ID, O=Example, L=Doe, S=John, C=GB"
KEYSTOREFILE:=my-release-key.keystore
ALIASNAME?=standkey

$(KEYSTOREFILE) : clean
	keytool -genkey -v -keystore $(KEYSTOREFILE) -alias $(ALIASNAME) -keyalg RSA -keysize 2048 -validity 10000 -storepass $(STOREPASS) -keypass $(STOREPASS) -dname $(DNAME)

makecapk/lib/arm64-v8a/lib$(APPNAME).so : $(ANDROIDSRCS)
	mkdir -p makecapk/lib/arm64-v8a
	$(CC_ARM64) $(CFLAGS) $(CFLAGS_ARM64) -o $@ $^ -L$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/lib/aarch64-linux-android/$(ANDROIDVERSION) $(LDFLAGS)

makecapk/lib/armeabi-v7a/lib$(APPNAME).so : $(ANDROIDSRCS)
	mkdir -p makecapk/lib/armeabi-v7a
	$(CC_ARM32) $(CFLAGS) $(CFLAGS_ARM32) -o $@ $^ -L$(NDK)/toolchains/llvm/prebuilt/$(OS_NAME)/sysroot/usr/lib/arm-linux-androideabi/$(ANDROIDVERSION) $(LDFLAGS)

makecapk.apk : $(TARGETS) AndroidManifest.xml
	mkdir -p makecapk/assets
	cp -r Sources/assets/* makecapk/assets
	rm -rf temp.apk
	$(BUILD_TOOLS)/aapt package -f -F temp.apk -I $(ANDROIDSDK)/platforms/android-$(ANDROIDVERSION)/android.jar -M AndroidManifest.xml -S Sources/res -A makecapk/assets -v --target-sdk-version $(ANDROIDTARGET)
	unzip -o temp.apk -d makecapk
	rm -rf makecapk.apk
	cd makecapk && zip -D9r ../makecapk.apk . && zip -D0r ../makecapk.apk ./resources.arsc ./AndroidManifest.xml
	jarsigner -sigalg SHA1withRSA -digestalg SHA1 -verbose -keystore $(KEYSTOREFILE) -storepass $(STOREPASS) makecapk.apk $(ALIASNAME)
	rm -rf $(APKFILE)
	$(BUILD_TOOLS)/zipalign -v 4 makecapk.apk $(APKFILE)
	$(BUILD_TOOLS)/apksigner sign --key-pass pass:$(STOREPASS) --ks-pass pass:$(STOREPASS) --ks $(KEYSTOREFILE) $(APKFILE)
	rm -rf temp.apk
	rm -rf makecapk.apk
	@ls -l $(APKFILE)

AndroidManifest.xml :
	PACKAGENAME=$(PACKAGENAME) \
		ANDROIDVERSION=$(ANDROIDVERSION) \
		ANDROIDTARGET=$(ANDROIDTARGET) \
		APPNAME=$(APPNAME) \
		LABEL=$(LABEL) envsubst '$$ANDROIDTARGET $$ANDROIDVERSION $$APPNAME $$PACKAGENAME $$LABEL' \
		< AndroidManifest.xml.template > AndroidManifest.xml

install : $(KEYSTOREFILE) makecapk.apk
	@echo "Installing" $(PACKAGENAME)
	adb install -r $(APKFILE)
reinstall : uninstall install
uninstall : 
	adb uninstall $(PACKAGENAME)
clean :
	rm -rf $(APPNAME).apk.idsig $(KEYSTOREFILE) temp.apk makecapk.apk makecapk $(APKFILE) AndroidManifest.xml

