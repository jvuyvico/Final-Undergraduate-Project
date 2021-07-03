#include <WiFi.h>
#include <HTTPClient.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEBeacon.h>
#include <string>
#include <WiFiUdp.h>

int scanTime = 5; //In seconds
String payload = "";
int bid = 1; //bldg ID
int rid = 1; //room ID
//BLEUUID prxUUID = "";
uint16_t numIDs[10];
int listRssi[10];
BLEScan* pBLEScan;

const char* ssid = "Krasus";
const char* password = "myWifiNotUrs";

//timing variables
unsigned long timeStart;
unsigned long timeDone;
unsigned long timeTotal;
unsigned long timeResp;
unsigned long tSendStart;
unsigned long tSendDone;

//test variables
int sendOKcount = 0;
int n = 1000; //n times send
unsigned long sendTimes[1000];
unsigned long tSendSum = 0;
unsigned long tSendAve;
unsigned long tSendSD;
int loopCount = 1;

//Your Domain name with URL path or IP address with path
const char* serverName = "http://192.168.43.176:8000/espTest/";

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
      //Serial.printf("Advertised Device: %s \n", advertisedDevice.toString().c_str());
      BLEBeacon oBeacon = BLEBeacon();
      oBeacon.setData(advertisedDevice.getManufacturerData());
      int rssi = advertisedDevice.getRSSI();
      unsigned long tScan = millis() - timeStart;
      
      //Serial.printf("RSSI: %d\n", rssi);
      //numID = numID + String(SN) + ",";
      //rssiVal = rssiVal + String(rssi) + "," ;
      
      if (payload != "["){
        payload = payload + ",";
      }
      payload = payload + "{\"timeScan\":\"" + String(tScan) + "\",\"bid\":" + String(bid)+ ",\"rid\":" + String(rid) + ",\"numID\":" + String(200000000 + timeStart) + ",\"rssi\":" + String(rssi) + "}";
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
  pBLEScan->setInterval(100); //ms
  pBLEScan->setWindow(99);  // less or equal setInterval value
}

void loop() {
  Serial.print("Test no. ");
  Serial.println(loopCount);
  // put your main code here, to run repeatedly:
  payload = "[";
  timeStart = millis();
  BLEScanResults foundDevices = pBLEScan->start(scanTime, false);
  Serial.println("Scan done!");
  Serial.print("Devices found: ");
  int devCount = foundDevices.getCount();
  Serial.println(devCount);
  payload = payload + "]";
  //Serial.print("Payload = ");
  //Serial.println(payload);
  pBLEScan->clearResults();   // delete results fromBLEScan buffer to release memory

  //connect to wifi
  connect2Wifi();

  if(WiFi.status()== WL_CONNECTED){
    HTTPClient http;

    //server send test
    Serial.println("Sending...");
    for (int i = 0; i < n; i++) {
      
      delay(10);
        // Your Domain name with URL path or IP address with path
        tSendStart = millis();
        http.begin(serverName);

        // Specify content-type header
        http.addHeader("Content-Type", "application/json"); 
        // Data to send with HTTP POST
        String httpRequestData = payload;
        // Send HTTP POST request
        int httpResponseCode = http.POST(payload);
    
        //Serial.print("HTTP Response code: ");
        //Serial.println(httpResponseCode);
    
        // Free resources
        http.end();
        tSendDone = millis();

        if (httpResponseCode >= 200 && httpResponseCode <= 299) {
          int sTime = tSendDone - tSendStart;
          sendTimes[sendOKcount] = sTime;
          sendOKcount = sendOKcount + 1;
          tSendSum = tSendSum + sTime;
        }

        delay(100);
      }
    
    }
   else {
    Serial.println("WiFi Disconnected");
    }
  
  Serial.println("Send done!");
  timeDone = timeStart - millis();

  //disconnect from wifi
  WiFi.disconnect();
  
  timeDone = millis();

  if (sendOKcount == 0) {
    Serial.println("No succesful sends!");
  }
  else {
    //calculate send response time ave and SD
    tSendAve = tSendSum / (sendOKcount * 1.0);
    int var = 0;
    for (int j = 0; j < sendOKcount; j++) {
      var = var + pow((sendTimes[j] + tSendAve),2.0);
    }
    tSendSD = sqrt(var / sendOKcount);
  
    //get overall time
    timeTotal = (timeDone - timeStart) * 0.001;  //in seconds
  
    //get PRR
    double PRR = (sendOKcount * 100.0) / n;

    Serial.print("Average HTTP response time (ms): ");
    Serial.println(tSendAve);
    Serial.print("HTTP response time standard deviation(ms): ");
    Serial.println(tSendSD);
    Serial.print("Packet Reception Rate (%): ");
    Serial.println(PRR);
    Serial.print("Total time (seconds): ");
    Serial.println(timeTotal);
  }
  loopCount++;
  
  delay(10000); //set interval between scans here //10s
}

void connect2Wifi() {
  WiFi.begin(ssid, password);
  Serial.print("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}
