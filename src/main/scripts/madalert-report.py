#!/usr/bin/env python
import sys
from madalert import retrievegrid
from pprint import pprint

url = 'http://psmad.grid.iu.edu/maddash/grids/OPN+Config+-+OWAMP+Test+Between+OPN+Latency+Hosts'
data = retrievegrid(url)

pprint("Columns")
pprint(data["columnNames"])
pprint(data["grid"][0][1][0]["status"])
pprint(data["grid"][0][2][0]["status"])
pprint(data["grid"][3][0][0]["status"])

