#!/usr/bin/env python
import sys
from madalert import *

#url = 'http://psmad.grid.iu.edu/maddash/grids/OPN+Config+-+OWAMP+Test+Between+OPN+Latency+Hosts'
url = sys.argv[1]

print "Loading data from " + url
data = retrievegrid(url)
print "Analyzing \"" + data["name"] + "\""


# If the whole grid is down, no further checks
if (matchAllSites(data, 3)):
    print("* All grid down")
    sys.exit()

# If the whole grid is green, no further checks
if (matchAllSites(data, 0)):
    print("* All is well")
    sys.exit()

nSites = len(data['columnNames'])
for site in range(0, nSites):
    if (matchSite(data, site, 3)):
        print("* Site " + data['columnNames'][site] + " is down")
    elif (matchInitiatedBySite(data, site, 3)):
        print("* Site " + data['columnNames'][site] + " can't test")
    elif (matchInitiatedOnSite(data, site, 3)):
        print("* Site " + data['columnNames'][site] + " can't be tested")
    elif (matchInitiatedBySite(data, site, 3, 0.75)):
        print("* Site " + data['columnNames'][site] + " mostly can't test")
    elif (matchInitiatedOnSite(data, site, 3, 0.70)):
        print("* Site " + data['columnNames'][site] + " mostly can't be tested")

