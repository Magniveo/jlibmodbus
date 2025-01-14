package com.intelligt.modbus.jlibmodbus.master;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.net.ModbusConnectionFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

/*
 * Copyright (C) 2016 "Invertor" Factory", JSC
 * [http://www.sbp-invertor.ru]
 *
 * This file is part of JLibModbus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Vladislav Y. Kochedykov, software engineer.
 * email: vladislav.kochedykov@gmail.com
 */

final public class ModbusMasterRTU extends ModbusMasterSerial {
    final private boolean keepAlive;
    public ModbusMasterRTU(SerialParameters parameters) throws SerialPortException {
        super(ModbusConnectionFactory.getRTU(SerialUtils.createSerial(parameters)));
        keepAlive =false;
    }

    public ModbusMasterRTU(String device, SerialPort.BaudRate baudRate, int dataBits, int stopBits, SerialPort.Parity parity) throws SerialPortException {
        this(new SerialParameters(device, baudRate, dataBits, stopBits, parity));
    }
    public boolean isKeepAlive() {
        return keepAlive;
    }
    public ModbusMasterRTU(TcpParameters parameters) {
        super(ModbusConnectionFactory.getRTUOverTCP(parameters));
        keepAlive = parameters.isKeepAlive();
        try {
            if (isKeepAlive()) {
                connect();
            }
        } catch (ModbusIOException e) {
            Modbus.log().warning("keepAlive is set, connection failed at creation time.");
        }
    }
}
