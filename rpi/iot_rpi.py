#-*- coding:utf-8 -*-
## 라이브러리 불러오기
import Adafruit_DHT
import requests
import json
import time
import math
import busio
import digitalio
import board
import adafruit_mcp3xxx.mcp3008 as MCP
from adafruit_mcp3xxx.analog_in import AnalogIn

sensor = Adafruit_DHT.DHT11     #  sensor 객체 생성

pin = 2                        # Data핀

RLOAD= 10.0
RZERO=76.63
PARA = 116.6020682
PARB = 2.769034857
CORA = 0.00035
CORB = 0.02718
CORC = 1.39538
CORD = 0.0018
CORE = -0.003333333
CORF = -0.001923077
CORG = 1.130128205


ATMOCO2 = 397.13

def getCorrectionFactor(t, h, CORA, CORB, CORC, CORD, CORE, CORF, CORG):
    if t<20:
        return CORA * t * t - CORB *t +CORC - (h-33.)*CORD
    else:
        return CORE * t + CORF * h + CORG

def getResistance(value_pin, RLOAD):
    return ((1023./value_pin) - 1.)*RLOAD

def getCorrectedResistance(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD):
	return getResistance(value_pin,RLOAD) / getCorrectionFactor(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG)

def getPPM(PARA,RZERO,PARB,value_pin,RLOAD):
	return PARA * math.pow((getResistance(value_pin,RLOAD)/RZERO), -PARB)

def getCorrectedPPM(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD,PARA,RZERO,PARB):
	return PARA * math.pow((getCorrectedResistance(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD)/RZERO), -PARB)

def getRZero(value_pin,RLOAD,ATMOCO2,PARA,PARB):
	return getResistance(value_pin,RLOAD) * math.pow((ATMOCO2/PARA), (1./PARB))

def getCorrectedRZero(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD,ATMOCO2,PARA,PARB):
	return getCorrectedResistance(t,h,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD) * math.pow((ATMOCO2/PARA), (1./PARB))

def map(x,in_min,in_max,out_min,out_max):
	return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min


spi = busio.SPI(clock=board.SCK, MISO=board.MISO, MOSI=board.MOSI)

cs = digitalio.DigitalInOut(board.D5)

mcp = MCP.MCP3008(spi, cs)

chan0 = AnalogIn(mcp, MCP.P0)


humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)   # 센서 객체에서 센서 값(ㅇ노도, 습도) 읽기


while (True):
    if humidity is not None and temperature is not None: #습도 및 온도 값이 모두 제대로 읽혔다면
        hum = str(humidity)
        tem = str(temperature)
        print('Temp= '+tem+'*C  Humidity= '+hum +'%')
    else:
        print('Failed to get reading. Try again!')
    
    value_ads = chan0.value
    value_pin = map((value_ads - 565), 0, 26690, 0, 1023)
    rzero = getRZero(value_pin,RLOAD,ATMOCO2,PARA,PARB)
    correctedRZero = getCorrectedRZero(temperature,humidity,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD,ATMOCO2,PARA,PARB)
    resistance = getResistance(value_pin,RLOAD)
    ppm = getPPM(PARA,RZERO,PARB,value_pin,RLOAD)
    correctedPPM = getCorrectedPPM(temperature,humidity,CORA,CORB,CORC,CORD,CORE,CORF,CORG,value_pin,RLOAD,PARA,RZERO,PARB)
    c2 = int(ppm)
    co2data = str(c2)
    
    print('CO2: %s ppm' % round(ppm))

    air_data = {"temperature":tem, "humidity":hum, "co2":co2data}

    print('post success')

    res = requests.post('localhost:3553/IoTpj_server/post_air_info/', json = air_data)
    print(res.status_code)
    time.sleep(3600) #1시간 정지

