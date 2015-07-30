# DroidUrDHT

THIS IS SOON TO BE DEPRECATED/REWORKED INTO SOMETHING MORE USEFUL ON DROID FOR THE UrDHT PROJECT!

This is really REALLY messy and badly organized... it needs to be torn apart and redone. This is motly just for me to learn a bit and test things out and is not meant for any sort of production use.
Mostly I am cutting corners because I need it to be finished in about a day and a half. you have been warned

also... hypocritically... this is really not well documentented currently.


Source is here: app/src/main/java/net/glidr/urdht_test

Hashing ... done
  (note - port multihash to java... that looks cool)

Ipaddress ... done
  getting public and local ipaddresses if different

EuclideanSpaceMath class ported
  this shoudl be done... it isnt tested yet.

Bootstrap
  Holds the master node info, in the Python code this is a json file, but I am cutting corners
  
Point
  Simple multidimensional point class

FileIO
  Doesnt do much, left on the slaughterhouse floor for now

Base58
  shamlessly pilfered from:
  https://github.com/blockcypher/java-client/blob/master/src/main/java/com/google/bitcoin/core/utils/Utils.java
  it has been modified slightly to work, but mostly this is not my code and I cannot take any credit for it.
  
MainActivity
  Main screen you see when you start the app. Launches/stops the service. Displays client Info.
  There is lots of ASYNC calls happening here.
  /MAY/ have an auxilliary screen where the entire network is graphed somehow... 
  
