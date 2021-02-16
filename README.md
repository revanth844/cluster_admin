# cluster_admin

Cluster File Manager application with REST interface
 
## Assumptions:

1. JSON Tree with space statistics of all clusters and their hosts is loaded in primary-memory
	This is Simulated by a FileAdminService/readClusterStatsToMemory()

## Check if JAVA_HOME and MAVEN_HOME are set properly
```
$ echo $JAVA_HOME
C:\Program Files\Java\jdk-11.0.8

$ echo $MAVEN_HOME
D:\Software\apache-maven-3.5.4
```

## Build the application, from folder in which source is copied
```
$ mvn clean package
```
Application tests will run and build the application.

Run the application and test with sample data, to get response as expected:


### Example-1
```
curl --location --request POST 'http://localhost:8080/file/getTargetCluster' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fileName": "gift_items_from_north_carolina.txt",
	"fileSize" : "25GB",
	"clusterName" : "c1"
}'
```
As specific "c1" cluster is specified in input request, and file is big, can be stored in "h1 of cluster c1" only.
```
[
    {
        "cluster": "c1",
        "hostname": "h1"
    }
]
```


### Example-2
```
curl --location --request POST 'http://localhost:8080/file/getTargetCluster' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fileName": "claim_records_UHC.txt",
	"fileSize" : "2GB",
	"clusterName" : "c1"
}'
```
As specific "c1" cluster is specified in input request, the file can be stored in h1 or h2 that belongs to cluster "c1"
```
[
    {
        "cluster": "c1",
        "hostname": "h1"
    },
    {
        "cluster": "c1",
        "hostname": "h2"
    }
]
```


### Example-3
```
curl --location --request POST 'http://localhost:8080/file/getTargetCluster' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fileName": "gift_items_from_north_carolina.txt",
	"fileSize" : "28GB"
}'
```
As specific cluster is not specified in input request, the file can be stored in "h1 of cluster c1" or "h1 of cluster c2"
```
[
    {
        "cluster": "c1",
        "hostname": "h1"
    },
    {
        "cluster": "c2",
        "hostname": "h1"
    }
]
```


### Example-4
```
curl --location --request POST 'http://localhost:8080/file/getTargetCluster' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fileName": "genome_sequence_data.txt",
	"fileSize" : "2000GB"
}'
```
This file cannot be stored in any of the hosts in any of the cluster due to the huge size.
```
[]
```
