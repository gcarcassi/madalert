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

def matchInitiatedBySite(data, site, status):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row):
                if (column == site):
                    if (not matchBottomHalfCell(data, row, column, status)):
                        return False
                if (row == site):
                    if (not matchTopHalfCell(data, row, column, status)):
                        return False
                
    return True

def matchInitiatedOnSite(data, site, status):
    #TODO: check status in range
    nSites = len(data['columnNames'])
    for column in range(0, nSites):
        for row in range(0, nSites):
            if (column != row):
                if (column == site):
                    if (not matchTopHalfCell(data, row, column, status)):
                        return False
                if (row == site):
                    if (not matchBottomHalfCell(data, row, column, status)):
                        return False
                
    return True

def matchSiteAllStatus(data, site, status):
    return False
