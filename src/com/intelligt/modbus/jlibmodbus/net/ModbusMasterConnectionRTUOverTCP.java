package com.intelligt.modbus.jlibmodbus.net;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.net.stream.base.LoggingInputStream;
import com.intelligt.modbus.jlibmodbus.net.stream.base.LoggingOutputStream;
import com.intelligt.modbus.jlibmodbus.net.transport.ModbusTransport;
import com.intelligt.modbus.jlibmodbus.net.transport.ModbusTransportFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ModbusMasterConnectionRTUOverTCP extends ModbusConnection {

    final private TcpParameters parameters;
    private ModbusTransport transport = null;

    ModbusMasterConnectionRTUOverTCP(TcpParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public LoggingOutputStream getOutputStream() {
        return transport.getOutputStream();
    }

    @Override
    public LoggingInputStream getInputStream() {
        return transport.getInputStream();
    }

    @Override
    public ModbusTransport getTransport() {
        return transport;
    }

    @Override
    protected void openImpl() throws ModbusIOException {
        if (!isOpened()) {
            if (parameters != null) {
                InetSocketAddress isa = new InetSocketAddress(parameters.getHost(), parameters.getPort());
                Socket socket = new Socket();
                try {
                    socket.connect(isa, parameters.getConnectionTimeout());
                    socket.setKeepAlive(parameters.isKeepAlive());
                    socket.setTcpNoDelay(true);

                    transport = ModbusTransportFactory.createRTUOverTCP(socket);
                    setReadTimeout(getReadTimeout());
                } catch (Exception e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // ignored
                    }
                    throw new ModbusIOException(e);
                }
            } else {
                throw new ModbusIOException("TCP parameters is null");
            }
        }
    }

    @Override
    protected void closeImpl() throws ModbusIOException {
        try {
            if (transport != null) {
                transport.close();
            }
        } catch (IOException e) {
            throw new ModbusIOException(e);
        } finally {
            transport = null;
        }
    }
}