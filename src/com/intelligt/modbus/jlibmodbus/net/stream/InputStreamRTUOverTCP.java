package com.intelligt.modbus.jlibmodbus.net.stream;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusChecksumException;
import com.intelligt.modbus.jlibmodbus.net.stream.base.LoggingInputStream;
import com.intelligt.modbus.jlibmodbus.net.stream.base.ModbusInputStream;
import com.intelligt.modbus.jlibmodbus.utils.CRC16;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class InputStreamRTUOverTCP extends LoggingInputStream {

    public InputStreamRTUOverTCP(final Socket s) throws IOException {
        super(new ModbusInputStream() {
            final private Socket socket = s;
            final private BufferedInputStream in = new BufferedInputStream(s.getInputStream());

            private int crc = CRC16.INITIAL_VALUE;
            @Override
            public int read() throws IOException {
                int c = in.read();
                if (-1 == c) {
                    throw new IOException("Input stream is closed");
                }
                crc = CRC16.calc(crc, (byte) c);
                return c;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int count = 0;
                int k = 0;
                while (count < len && k != -1) {
                    k = in.read(b, off + count, len - count);
                    if (-1 != k)
                        count += k;
                }
                crc = CRC16.calc(crc, b, off, len);
                return count;
            }

            @Override
            public void setReadTimeout(int readTimeout) {
                try {
                    socket.setSoTimeout(readTimeout);
                } catch (SocketException e) {
                    Modbus.log().warning(e.getLocalizedMessage());
                }
            }
            @Override
            public void close() throws IOException {
                in.close();
            }
        });
    }

    public void frameInit() throws IOException {
    }
    public void frameCheck(int c_crc) throws IOException, ModbusChecksumException {
        int r_crc = readShortLE();
        if (c_crc != r_crc) {
            throw new ModbusChecksumException(r_crc, c_crc);
        }
    }
}
