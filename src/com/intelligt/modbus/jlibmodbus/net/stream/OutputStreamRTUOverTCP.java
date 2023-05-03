package com.intelligt.modbus.jlibmodbus.net.stream;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.net.stream.base.LoggingOutputStream;
import com.intelligt.modbus.jlibmodbus.net.stream.base.ModbusOutputStream;
import com.intelligt.modbus.jlibmodbus.utils.CRC16;

import java.io.IOException;
import java.net.Socket;

public class OutputStreamRTUOverTCP extends LoggingOutputStream {

    public OutputStreamRTUOverTCP(final Socket s) throws IOException {
        super(new ModbusOutputStream() {
            final Socket socket = s;
            private int crc = CRC16.INITIAL_VALUE;
            @Override
            public void flush() throws IOException {
                try {
                    byte[] bytes = getBytesWithCrc(toByteArray());
                    writeShortLE(CRC16.calc(toByteArray()));
                    socket.getOutputStream().write(bytes);
                    socket.getOutputStream().flush();
                } catch (Exception e) {
                    throw new IOException(e);
                } finally {
                    super.flush();
                }
            }
            byte[]getBytesWithCrc(byte[] command){
                int crc = CRC16.calc(this.crc,command, 0, command.length);
                byte[]bytes =new byte[command.length+2];
                System.arraycopy(command,0,bytes,0, command.length);
                bytes[bytes.length-2] = (byte) (crc);
                bytes[bytes.length-1] = (byte)(crc>>8);
                return bytes;
            }
            @Override
            public void close() throws IOException {
                socket.getOutputStream().close();
            }
        });
    }
}