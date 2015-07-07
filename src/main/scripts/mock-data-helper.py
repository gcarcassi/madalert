#!/usr/bin/env python
import sys
import json, urllib

inputFile = sys.argv[1]
outputFile = sys.argv[2]

with open(inputFile) as data_file:
    data = json.load(data_file)

site = 4

nSites = len(data['columnNames'])
for column in range(0, nSites):
    for row in range(0, nSites):
        if (column != row):
            if (column == site):
                data["grid"][row][column][0]["message"]="Unable to find any tests"
                data["grid"][row][column][0]["status"]=3
            if (row == site):
                data["grid"][row][column][1]["message"]="Unable to find any tests"
                data["grid"][row][column][1]["status"]=3


with open(outputFile, "w") as outfile:
    json.dump(data, outfile, indent=4, separators=(',', ': '))


