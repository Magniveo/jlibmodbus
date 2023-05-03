package com.intelligt.modbus.jlibmodbus.net.transport;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.msg.ModbusMessageFactory;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusMessage;
import com.intelligt.modbus.jlibmodbus.net.stream.InputStreamRTUOverTCP;
import com.intelligt.modbus.jlibmodbus.net.stream.OutputStreamRTUOverTCP;

import java.io.IOException;
import java.net.Socket;
class ModbusTransportRTUOverTCP extends ModbusTransport {

    final private Socket socket;
    ModbusTransportRTUOverTCP(Socket socket) throws IOException {
        super(new InputStreamRTUOverTCP(socket), new OutputStreamRTUOverTCP(socket));
        this.socket = socket;
    }

    @Override
    protected ModbusMessage read(ModbusMessageFactory factory) throws ModbusNumberException, ModbusIOException {
        if (getInputStream() instanceof InputStreamRTUOverTCP) {
            InputStreamRTUOverTCP is = (InputStreamRTUOverTCP) getInputStream();
            try {
                is.frameInit();
                ModbusMessage msg = createMessage(factory);
                //is.frameCheck(msg.getCrc());//TODO: need rewrite for check crc
                return msg;
            } catch (IOException ioe) {
                throw new ModbusIOException(ioe);
            }
        } else {
            throw new ModbusIOException("Can't cast getInputStream() to InputStreamSerial");
        }
    }

    @Override
    public void sendImpl(ModbusMessage msg) throws ModbusIOException {
        msg.write(getOutputStream());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
