# README for CS 2520 Project --- sLSRP
#
#This readme file lists all the steps how to compile and run the project.
#
# Authors: Eric Gratta  & Zhenjiang Fan
#
#Step 1: Unzip the compressed file and compile the project

unzip sLSRP_Project.zip

javac sLSRP/*.java

# Step 2: Start the Name server

java sLSRP/NameServer

#Step 3: Add the connection information of the Name server into configuration file

#Step 4: Start a router

java sLSRP/Router routerI_D sLSRP/config.txt

#Step 5: Build a topology of a network, then start other routers

After starting a router, user interface option 4 allows you to add neighbors by router id

#Now, we can test it!


