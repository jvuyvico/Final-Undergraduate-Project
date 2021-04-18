#include <WiFi.h>
#include <HTTPClient.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEBeacon.h>
#include <string>

int scanTime = 5; //In seconds
String payload = "";
String bid = "00001"; //bldg ID
String rid = "00001"; //room ID
//BLEUUID prxUUID = "";
uint16_t numIDs[10];
int listRssi[10];
BLEScan* pBLEScan;

const char* ssid = "HyperDriveJeepney";
const char* password = "$Bruno08Hotch13$";

//Your Domain name with URL path or IP address with path
const char* serverName = "http://192.168.1.15:8000/esp/";

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
      payload = payload + "," + String(SN) + ":" +  String(rssi);
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
  payload = bid + rid;
  BLEScanResults foundDevices = pBLEScan->start(scanTime, false);
  Serial.print("Devices found: ");
  Serial.println(foundDevices.getCount());
  Serial.println("Scan done!");
  //insert timestamp to payload
  Serial.print("Payload = ");
  Serial.println(payload);
  pBLEScan->clearResults();   // delete results fromBLEScan buffer to release memory

  //connect to wifi
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());

  if(WiFi.status()== WL_CONNECTED){
    HTTPClient http;
    
    // Your Domain name with URL path or IP address with path
    http.begin(serverName);

    // Specify content-type header
    http.addHeader("Content-Type", "text/plain"); 
    // Data to send with HTTP POST
    String httpRequestData = payload;
    // Send HTTP POST request
    int httpResponseCode = http.POST(payload);
    
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    
    // Free resources
    http.end();
    }
   else {
    Serial.println("WiFi Disconnected");
    }

  //disconnect from wifi
  WiFi.disconnect();
  
  delay(2000); //set interval between scans here //1min
}
