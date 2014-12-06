************************************************** CHORD SIMULATOR **************************************************


Installation:
IDE used is ECLIPSE
Import normally as you would import any file from ECLIPSE

Execution:
Give Run as -> Java Application in the ECLIPSE
It opens a console
The below list of commands can be used to provide inputs to the console:

create <IP address> 									; creates the chord network 
join <Source IP address> <Destination IP address> 		; joins the source node to the already connected destination node
leave <IP address> 										; removes the node from the Chord Network
store <filename> 										; stores the file into one of the nodes 
retrieve <filename> <IP address of the starting node>	; retrieves the file from the node where it is stored 
show <IP address> 										; displays the finger table of the Node 
showkeys <IP address> 									; displays the keys stored in the Node 
show all 												; displays all the IP addresses and their hashed identifiers 
exit 													; exits the Chord network

The above list can also be known by typing in "help" in the console.

Sample Running of the applicaiton:
chord:/>create 192.168.10.1
Creating a Chord ring with 192.168.10.1 as the root node
chord:/>join 192.168.10.2 192.168.10.1
Joining 192.168.10.2 to the Chord network with ID 53
chord:/>show all
IP address : 192.168.10.2 hashed value 53
IP address : 192.168.10.1 hashed value 17
chord:/>show 192.168.10.1
Finger Table of node "192.168.10.1" whose ID is 17 is as below 

| Finger Nodes | Successor Node | IP address       |
| 18           | 53             | 192.168.10.2     | 
| 19           | 53             | 192.168.10.2     | 
| 21           | 53             | 192.168.10.2     | 
| 25           | 53             | 192.168.10.2     | 
| 33           | 53             | 192.168.10.2     | 
| 49           | 53             | 192.168.10.2     | 
chord:/>show 192.168.10.2
Finger Table of node "192.168.10.2" whose ID is 53 is as below 

| Finger Nodes | Successor Node | IP address       |
| 54           | 17             | 192.168.10.1     | 
| 55           | 17             | 192.168.10.1     | 
| 57           | 17             | 192.168.10.1     | 
| 61           | 17             | 192.168.10.1     | 
| 5            | 17             | 192.168.10.1     | 
| 21           | 53             | 192.168.10.2     | 
chord:/>store test1.mp3
Resource test1.mp3 with id 29 stored in node 53
chord:/>showkeys 192.168.10.2
test1.mp3
chord:/>showkeys 192.168.10.1
No keys stored in this node
chord:/>retrieve test1.mp3 192.168.10.1
 ---> Node 53
Retrieved value from node 53
Value retrieved is test1.mp3
Hop count to find the file was 1
chord:/>leave 192.168.10.2
Removing 192.168.10.2 from the Chord network
Transferring keys to Node 17
count[successor] is 0 count[id] is 1
chord:/>exit

Test Suite:
There are predefined commands that will create 64 nodes with IDs ranging from 0-63 so that one can conduct appropriate tests.
console command "run join" does the above job.
"run store" stores 64 filenames with IDs ranging from 0-63 into appropriate nodes.
"run retrieve" retrieves all the keys stored with the node with ID 0 as the root node.
It also displays the path taken and also the hop count and the node from which the value was retrieved.
"run leave" unjoins all the nodes from the Chord network.

"run leave half" unjoins half of the nodes from the Chord network. 
Likewise "run retrieve half" does a similar job.


Over-writing Default Properties:
chord.properties file has pre-defined properties which can be over-written
To change the number of bits in the hashed value, change the value of key "mBitLength" to appropriate value. Default is 6.
Likewise change "writePathLength" to true if you want to write the result of the path length during retrieval of a key value to the file "path_count". Default is false.