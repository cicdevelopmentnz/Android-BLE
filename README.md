# Android-BLE

## Installation

Add the following to your root build.gradle

```gradle
   allprojects {
      repositories {
         ...
         maven { url 'https://jitpack.io' }
      }
   }
```

Add the dependency
```gradle
   dependencies {
      compile 'com.github.cicdevelopmentnz:Android-BLE:v0.0.3'
   }
```

## Usage

### Example Advertisement

```javascript

{
   "id": "Gateway-Node",
   "messages": [{
      "id": "Gateway-Id",
      "value": "UxayxL"
   }, {
      "id": "Gateway-Key",
      "value": "UDP-123"
   }]
}

```

### Beacon (Peripheral/Advertiser)

```java
   HashMap messages = new HashMap<String, String>();
   messages.put("Gateway-Id", "UxayxL");
   messages.put("Gateway-Key", "UDP-123");
   
   Beacon beacon = new Beacon("Gateway-Node", messages);

   BeaconManager beaconManager = new BeaconManager(this);

   beaconManager.setRange(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
   beaconManager.setFrequency(AdvertiseSettings.ADVERTISE_MODE_BALANCED);

   beaconManager.start().subscribe(
      status -> {
         if(status){
            beaconManager.addBeacon(beacon);
         }else{
            //Failed to start advertising
         }
      }
   );
```

#### Beacon Manager

##### Frequency Options

Default: AdvertiseSettings.ADVERTISE_MODE_BALANCED

| Name                                         |
| -------------------------------------------- |
| AdvertiseSettings.ADVERTISE_MODE_LOW_POWER   |
| AdvertiseSettings.ADVERTISE_MODE_BALANCED    |
| AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY |

##### Range Options

Default: AdvertiseSettings.ADVERTISE_TX_POWER_LOW

| Name                                           |
| ---------------------------------------------- |
| AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW |
| AdvertiseSettings.ADVERTISE_TX_POWER_LOW       |
| AvdertiseSettings.ADVERTISE_TX_POWER_MEDIUM    |
| AdvertiseSettings.ADVERTISE_TX_POWER_HIGH      |


### Scanner (Central/Discovery)

```java

   Scanner scanner = new Scanner(this);
   scanner.start().subscribe(
      jsonInfo -> {
         //JSONObject containing raw advertised information
      }
   );

   scanner.start("Identifier", new String[]{"Key"}).subscribe(
      filteredInfo -> {
         //JsonObject containing filtered information
      }
   );
```

