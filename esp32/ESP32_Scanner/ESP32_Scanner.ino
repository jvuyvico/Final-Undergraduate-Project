/*
   Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleScan.cpp
   Ported to Arduino ESP32 by Evandro Copercini
*/

#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEBeacon.h>
#include <string>

int scanTime = 5; //In seconds
String payload = "";
uint16_t numIDs[10];
int listRssi[10];
BLEScan* pBLEScan;

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
      Serial.printf("Advertised Device: %s \n", advertisedDevice.toString().c_str());
      BLEBeacon oBeacon = BLEBeacon();
      oBeacon.setData(advertisedDevice.getManufacturerData());
      //Serial.println(advertisedDevice.getManufacturerData().c_str());
      Serial.print("UUID: ");
      Serial.println(oBeacon.getProximityUUID().toString().c_str());
      unsigned int SN = ((__builtin_bswap16(oBeacon.getMajor())*100000) + __builtin_bswap16(oBeacon.getMinor()))/10;
      Serial.printf("ID: %d\n", SN);
      int rssi = advertisedDevice.getRSSI();
      Serial.printf("RSSI: %d\n", rssi);
      payload = payload + String(SN) + ":" +  String(rssi) + ",";
      //Serial.println(payload.c_str());
      
    }
};

void setup() {
  Serial.begin(115200);
  Serial.println("Scanning...");

  BLEDevice::init("");
  pBLEScan = BLEDevice::getScan(); //create new scan
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setActiveScan(true); //active scan uses more power, but get results faster
  pBLEScan->setInterval(100);
  pBLEScan->setWindow(99);  // less or equal setInterval value
}

void loop() {
  // put your main code here, to run repeatedly:
  payload = "";
  BLEScanResults foundDevices = pBLEScan->start(scanTime, false);
  Serial.print("Devices found: ");
  Serial.println(foundDevices.getCount());
  Serial.println("Scan done!");
  Serial.print("Payload = ");
  Serial.println(payload);
  pBLEScan->clearResults();   // delete results fromBLEScan buffer to release memory
  delay(2000); //set interval between scans here
}
