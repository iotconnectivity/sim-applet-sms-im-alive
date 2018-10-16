SIMTOOLS_DIR    = ../sim-tools

APPLET_AID      = 0xd0:0x70:0x02:0xca:0x44:0x90:0x01:0x01
APPLET_NAME     = Iamalive.Iamalive
PACKAGE_AID     = 0xd0:0x70:0x02:0xCA:0x44:0x90:0x01
PACKAGE_NAME    = Iamalive
PACKAGE_VERSION = 1.0

SOURCES = \
	src/Iamalive/Iamalive.java

include $(SIMTOOLS_DIR)/javacard/makefiles/applet-project.mk
