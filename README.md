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
      compile 'com.github.cicdevelopmentnz:Android-BLE:v0.0.2'
   }
```

## Usage

### Beacon

```java
   HashMap messages = new HashMap<String, String>();
   messages.put("Key", "Value");
   
   Beacon beacon = new Beacon("Identifier", messages);

   BeaconManager beaconManager = new BeaconManager(this);
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

### Scanner

```java

   Scanner scanner = new Scanner(this);
   scanner.start().subscribe(
      jsonInfo -> {
         //JSON object containing advertised information per device
      }
   ); 
```

