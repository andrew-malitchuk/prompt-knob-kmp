# Flashing the Firmware

Flashing writes the compiled firmware to the ESP32 over USB. This is done from
the [firmware repository](https://github.com/andrew-malitchuk/prompt-knob-firmware)
with PlatformIO. If you have not set up PlatformIO yet, start with
[Getting Started](getting-started.md).

## Flash the device

Connect the device with a data-capable USB cable, then from the firmware
repository root:

```bash
pio run -t upload
```

This compiles the firmware (if needed) and uploads it. PlatformIO auto-detects
the serial port and puts the ESP32 into download mode automatically.

## Open the serial monitor

After flashing, watch the boot and runtime logs:

```bash
pio device monitor
```

Press `Ctrl+C` to exit. The monitor and an upload cannot use the port at the
same time — close the monitor before flashing again.

## Choosing a port manually

If auto-detection picks the wrong device (or there are several), list the ports
and pass one explicitly:

```bash
# List available serial ports
pio device list

# Upload to a specific port
pio run -t upload --upload-port /dev/tty.usbserial-XXXX
```

On macOS the port typically looks like `/dev/tty.usbserial-*` or
`/dev/tty.SLAB_USBtoUART`; on Linux `/dev/ttyUSB0`; on Windows `COM3` and
similar.

## Troubleshooting

### The device is not detected / no serial port appears

- Use a **data** USB cable — charge-only cables have no data lines and will
  never enumerate a port.
- The ESP32 uses an onboard USB-to-UART bridge. If no port shows up, install the
  driver for your bridge chip (commonly **CP210x** or **CH340/CH341**) from the
  vendor, then reconnect.
- Run `pio device list` to confirm the OS sees the port at all.

### Upload fails or times out

- Close any open serial monitor — it holds the port and blocks the upload.
- Unplug and replug the device, then retry `pio run -t upload`.
- Some boards need the **BOOT** button held while flashing starts; release it
  once the upload begins.
- Lower the upload speed if you see checksum/sync errors (set the appropriate
  option in the firmware's `platformio.ini`).

### Permission denied on the serial port (Linux)

Add your user to the `dialout` group and re-log in:

```bash
sudo usermod -a -G dialout "$USER"
```

## After flashing

With fresh firmware running, pair the device with the companion app:

- [Getting started on Android](../user-guide/getting-started-android.md)
- [Getting started on macOS](../user-guide/getting-started-macos.md)
