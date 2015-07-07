import sys
import json, urllib

def helloworld(out):
    out.write("Hello world of Python\n")

def retrievegrid(url):
    resp = urllib.urlopen(url)
    return json.load(resp)

def matchTopHalfCell(data, row, column, status):
    currentStatus = data["grid"][row][column][0]["status"]
    return status == currentStatus

def matchBottomHalfCell(data, row, column, status):
    currentStatus = data["grid"][row][column][1]["status"]
    return status == currentStatus


def matchAllSites(data, status):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row):
                if (not matchTopHalfCell(data, row, column, status) or
                    not matchBottomHalfCell(data, row, column, status)):
                    return False
                
    return True

def matchSite(data, site, status):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row and (column == site or row == site)):
                if (not matchTopHalfCell(data, row, column, status) or
                    not matchBottomHalfCell(data, row, column, status)):
                    return False
                
    return True

def matchInitiatedBySite(data, site, status, threshold=1.0):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    matches = 0.0
    possibleMatches = float((nSites - 1) * 2)
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row):
                if (column == site):
                    if (matchBottomHalfCell(data, row, column, status)):
                        matches += 1.0
                if (row == site):
                    if (matchTopHalfCell(data, row, column, status)):
                        matches += 1.0
    if ((matches / possibleMatches) >= threshold):
        return True
    return False

def matchInitiatedOnSite(data, site, status, threshold=1.0):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    matches = 0.0
    possibleMatches = float((nSites - 1) * 2)
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row):
                if (column == site):
                    if (matchTopHalfCell(data, row, column, status)):
                        matches += 1.0
                if (row == site):
                    if (matchBottomHalfCell(data, row, column, status)):
                        matches += 1.0
    if ((matches / possibleMatches) >= threshold):
        return True
    return False

def matchSiteAllStatus(data, site, status):
    return False
