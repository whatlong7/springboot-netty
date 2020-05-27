#!/usr/bin/env python
# coding: utf-8
# python 3.7
#
# Usage: python test.py weight impl [local/test/picooc09/us]
# Example: python3 test3.py 50 400 local

import socket
import random
import time
import sys


class SocketServer(object):
    # TCP_IP = 'sock999.picooc.com'
    TCP_IP = 'localhost'
    # TCP_IP = '101.201.220.135'  # PICOOC09
    TCP_PORT = 999
    BUFFER_SIZE = 1024

    def __init__(self):
        self.data = None
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            if len(sys.argv) > 1:
                if sys.argv[-1] == 'local':
                    self.TCP_IP = 'localhost'
                    self.TCP_PORT = 999
                    print(self.TCP_IP, ':', self.TCP_PORT)
                if sys.argv[-1] == 'test':
                    self.TCP_IP = '172.17.0.20'
                    self.TCP_PORT = 999
                    print(self.TCP_IP, ':', self.TCP_PORT)
                if sys.argv[-1] == 'picooc09':
                    self.TCP_IP = '101.201.220.135'
                    self.TCP_PORT = 999
                    print(self.TCP_IP, ':', self.TCP_PORT)
                if sys.argv[-1] == 'us':
                    self.TCP_IP = 'sock999.picooc.us'
                    self.TCP_PORT = 999

            print(self.TCP_IP, ':', self.TCP_PORT)
            self.socket.connect((self.TCP_IP, self.TCP_PORT))
            print('connected')
        except Exception as inst:
            print(inst.message)
            return

    def send(self, data):
        try:
            # self.socket.send(data.decode('hex'))
            self.socket.send(bytes.fromhex(data))
            print('Will read reply')
            message = self.socket.recv(self.BUFFER_SIZE)
            print('Have read reply')
            # print(message.encode('hex'))
            print(message.hex())
        except Exception as e:
            print('>>>')
            print(e)

    def close(self):
        self.socket.close()


def calculate_checksum(*args):
    # Calculate Hex sum (integer result)
    hex_string = ''
    hex_array = []
    for i in args:
        hex_string += i

    while hex_string:
        hex_array.append(hex_string[-2:])
        hex_string = hex_string[:-2]

    sum_int = 0
    for i in hex_array:
        sum_int += int(i, 16)

    # convert it to binary sum
    sum_bin = '{0:b}'.format(sum_int)
    new_bin = ''

    # Negate the binary sum
    for (i, v) in enumerate(sum_bin):
        if v == '0':
            new_bin += '1'
        else:
            new_bin += '0'

    # format the bin -> hex, just return last 2
    return "{0:02X}".format(int(new_bin, 2))[-2:]


def A5_data():
    header = 'A5'
    utc = format(int(time.time()), 'x')
    length = '{0:02d}'.format(len(header + utc) / 2 + 2)
    checksum = calculate_checksum(header, length, utc[0:2], utc[2:4], utc[4:6], utc[6:])
    data = header + length + utc + checksum
    return data


def test_A6():
    return 'A611D0490000C5545722B3CE02E515A47C'


def A6_data(weight=None, imp=None):
    data = 'A611'
    seconds = int(time.time())  # - foo * 60 * 60
    if weight is None:
        weight = int(37.01 * 100 + int(random.random() * 100)) / 100.0
    if imp is None:
        imp = imp or 550 + int(random.random() * 10)
    print(weight, imp, seconds)

    utc_hex = hex(seconds)[2:].upper()
    weight_hex = hex(int(weight * 2 * 10))[2:].zfill(4).upper()
    imp_hex = hex(imp * 10)[2:].zfill(4).upper()

    mac_hex = 'D0490000C554'  # test s3
    # mac_hex = 'D04900033073' # home s3
    # mac_hex = 'D049000333B5' # yanjiang s3

    data += mac_hex + utc_hex + weight_hex + imp_hex
    checksum_hex = calculate_checksum(data)
    data += checksum_hex

    return data


def A8_data():
    data = 'A818' + 'AABBCCDDEEFFFFEEDDCCBBAA' + '2B4C4F47494E5F4F4B' + 'AC'
    #data = 'A818' + 'D04900000023D4EE07333F7A' + '2B4C4F47494E5F4F4B' + 'B1'
    return data


def A9_data():
    prefix = 'A92A'
    content = 'A92AD0490000C5544F56DE810F00A000000D0A4F56DE810F00A000000D0A4E56DE94B0009C00000D0AED'
    data = prefix + content
    checksum_hex = calculate_checksum(data)
    data += checksum_hex

    return data


if __name__ == '__main__':
    try:
        weight = float(sys.argv[1])
        imp = int(sys.argv[2])
    except:
        weight = None
        imp = None

    sock = SocketServer()

    # sock.send('A50aD049000102030130') #A5国内
    # sock.send('A50aD049000102020230') #A5国外
    # sock.send('A61101000000D049000102030101020123') #A6没带域名
    # sock.send('A61201000000D04900010203010102010121') #A6国内
    # sock.send('A61201000000D04900010203010102010220') #A6国外

    # sock.send('A50aD049000100000135') #A5国外
    # sock.send('A611D04901000000000103020101020123') #A6没带域名
    # sock.send('A612D0490001000000010302010102010121') #A6国内
    # sock.send('A91FD049000100004F01010302010102010d0a4E10010302010102010d0a2b') #A6国外

    # sock.send('A50aD04900020303022d') #A5国外
    # sock.send('A611D04901000000000202030101020122') #A6没带域名
    sock.send('A612D049010200000002030301020200021c') #A6国内
    # sock.send('A91FD049000203034F01010302010102010d0a4E10010302010102010d0a24') #A6国外

    # sock.send(A5_data())
    # sock.send(A6_data(weight, imp))
    # sock.send(A8_data())
    # sock.send(A9_data())
    # sock.close()
    #
    # exit()
    # from multiprocessing import Pool
    # p = Pool(10)
    #
    # foo = [i for i in xrange(0, 100)]
    #
    # p.map(sock.send, [A5_data])
