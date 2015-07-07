#!/usr/bin/env python
import sys
from madalert import *

#url = 'http://psmad.grid.iu.edu/maddash/grids/OPN+Config+-+OWAMP+Test+Between+OPN+Latency+Hosts'
url = sys.argv[1]
data = retrievegrid(url)

# Check if the whole grid is down
if (matchAllSites(data, 3)):
    print("All grid down")
else:
    nSites = len(data['columnNames'])
    for site in range(0, nSites):
        if (matchSite(data, site, 3)):
            print("Site " + data['columnNames'][site] + " is down")


