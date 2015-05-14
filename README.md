# BitTorrentApplication
An implementation of BitTorrent

To execute the project:
Step 1: Extract BitTorrent.zip

Step 2: Compile all the java files:
        a) javac ./client/*.java
        b) javac ./BitTorrentWebService/*.java
        c) javac ./tracker/*.java

Step 3: Execute the Tracker on machine1: java tracker.Tracker

Step 4: The WebService needs to be deployed on netbeans on machine2 (Just import the provided zip, right click on the project, and select deploy.).

Step 5: The IP address of machine1(Tracker) goes to in a text file "Trackers.txt" on machine2. The location of this file can be changed in the WebService project in netbeans in BitTorrentWebService > src > java > BitTorrentWebService > BitTorrentWS.java > FILE. The .torrent files generated on upload will be produced in the folder specified by BitTorrentWebService > src > java > BitTorrentWebService > BitTorrentWS.java > PATH.

Step 6: The IP address of machine2(running the WebService) goes in BitTorrent > client > Utility.java > WEB_SERVICE_IP on client machines.

Step 7: Execute the client: java client.ClientUI <YourName>

Step 8: Select upload / download as desired.


Note:
1. The files being transferred have to be located in the root directory of the BitTorrent project.
2. The .torrent files generated on machine2 should some how be available to the clients. In the demo, we used HFS(Http File Server) to perform this task. Simply emailing the file, or transferring via a USB-stick can also do the job.
3. There has to be at least one peer alive who has the entire file available.
4. No external APIs were used for the development of the project.
