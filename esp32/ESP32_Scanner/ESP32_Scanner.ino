#include <WiFi.h>
#include <HTTPClient.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEBeacon.h>
#include <string>
#include <NTPClient.h>
#include <WiFiUdp.h>

int scanTime = 5; //In seconds
//String numID = "";
//String rssiVal = "";
String payload = "";
int bid = 1; //bldg ID
int rid = 1; //room ID
//BLEUUID prxUUID = "";
//uint16_t numIDs[10];
//int listRssi[10];
BLEScan* pBLEScan;

const char* ssid = "HyperDriveJeepney";
const char* password = "$Bruno08Hotch13$";

//NTP config
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

// Variables to save date and time
String formattedDate;
String timeDateString;
String dayStamp;
String timeStamp;

//Your Domain name with URL path or IP address with path
const char* serverName = "https://smart-attendance-198.herokuapp.com/esp/";

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
      //Serial.printf("Advertised Device: %s \n", advertisedDevice.toString().c_str());
      BLEBeacon oBeacon = BLEBeacon();
      oBeacon.setData(advertisedDevice.getManufacturerData());
      //Serial.println(advertisedDevice.getManufacturerData().c_str());
      //Serial.print("UUID: ");
      //Serial.println(oBeacon.getProximityUUID().toString().c_str());
      unsigned int SN = ((__builtin_bswap16(oBeacon.getMajor())*100000) + __builtin_bswap16(oBeacon.getMinor()))/10;
      //Serial.printf("ID: %d\n", SN);
      int rssi = advertisedDevice.getRSSI();
      //Serial.printf("RSSI: %d\n", rssi);
      //numID = numID + String(SN) + ",";
      //rssiVal = rssiVal + String(rssi) + "," ;
      if (payload != "["){
        payload = payload + ",";
      }
      payload = payload + "{\"dayStamp\":\"" + dayStamp + "\",\"timeStamp\":\"" + timeStamp + "\",\"bid\":" + String(bid)+ ",\"rid\":" + String(rid) + ",\"numID\":" + String(SN) + ",\"rssi\":" + String(rssi) + "}";
      
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
  //get datetime
  connect2Wifi();
  timeClient.begin();
  timeClient.setTimeOffset(28800);
  while(!timeClient.update()) {
    timeClient.forceUpdate();
  }
  formattedDate = timeClient.getFormattedDate();
  int splitT = formattedDate.indexOf("T");
  timeStamp = formattedDate.substring(splitT+1, formattedDate.length()-1);
  dayStamp = formattedDate.substring(0, splitT);
  //Serial.print("Datatime: ");
  //Serial.println(timeDateString);
  WiFi.disconnect();
  
  payload = "[";
  BLEScanResults foundDevices = pBLEScan->start(scanTime, false);
  Serial.print("Devices found: ");
  Serial.println(foundDevices.getCount());
  Serial.println("Scan done!");
  payload = payload + "]";
  //insert timestamp to payload
  Serial.print("Payload = ");
  Serial.println(payload);
  //Serial.print("numID = ");
  //Serial.println(numID);
  //Serial.print("rssiVal = ");
  //Serial.println(rssiVal);
  pBLEScan->clearResults();   // delete results fromBLEScan buffer to release memory

  //connect to wifi
  connect2Wifi();

  if(WiFi.status()== WL_CONNECTED){
    HTTPClient http;
    
    // Your Domain name with URL path or IP address with path
    http.begin(serverName);

    // Specify content-type header
    http.addHeader("Content-Type", "application/json"); 
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
  
  delay(900000); //set interval between scans here //5min
}

void connect2Wifi() {
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}
