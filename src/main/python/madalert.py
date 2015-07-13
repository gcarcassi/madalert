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

class Report:
    def __init__(self, path):
        self.data = retrievegrid(path)
        self.sitesStats = []
        self.calculateStats()

    def calculateStats(self):
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteStats = [0, 0, 0, 0]
            for n in range(0, nSites):
                if (n != site):
                    status = self.data["grid"][site][n][0]["status"]
                    siteStats[status] += 1
                    status = self.data["grid"][site][n][1]["status"]
                    siteStats[status] += 1
                    status = self.data["grid"][n][site][0]["status"]
                    siteStats[status] += 1
                    status = self.data["grid"][n][site][1]["status"]
                    siteStats[status] += 1
            self.sitesStats.append(siteStats)

    def checkMkReport(self, testGroup, out):
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            out.write("0 " + testGroup + "_" + siteName +
                      "count_0=" + str(self.sitesStats[site][0]) +
                      "|count_1=" + str(self.sitesStats[site][1]) +
                      "|count_2=" + str(self.sitesStats[site][2]) +
                      "|count_3=" + str(self.sitesStats[site][3]) +
                      " OK\n")

