import sys
import json, urllib


class MatchStatus:
    def __init__(self, status):
        self.result = True
        self.status = status

    def match(self, row, column, half, status):
        if self.status != status:
            self.result = False


def forAllSitesIn(data, matcher):
    nSites = len(data['columnNames'])
    for column in range(0, nSites):
        for row in range(0, nSites):
            if column != row:
                matcher.match(row, column, 0, data["grid"][row][column][0]["status"])

    return matcher.result

def matchStatus(status):
    return MatchStatus(status)

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

def matchHalfCell(halfCell, status):
    return status == halfCell["status"]

def matchAllSites(data, status):
    return forAllSitesIn(data, matchStatus(status))

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

class Problem:
    name = ""
    # 0 for NONE, 1 for MINOR, 2 for MAJOR
    severity = 0

class Report:
    def __init__(self, path):
        self.data = retrievegrid(path)
        self.sitesStats = []
        self.calculateStats()
        self.globalProblems = []
        self.siteProblems = []
        nSites = len(self.data["columnNames"])
        for row in range(0, nSites):
            self.siteProblems.append([])

    def addProblem(self, name, severity, site=-1):
        problem = Problem()
        problem.name = name
        problem.severity = severity

        if (site == -1):
            self.globalProblems.append(problem)
        else:
            self.siteProblems[site].append(problem)

    def maxSeverityForSite(self, site=-1):
        severity = 0
        problems = self.globalProblems
        if (site != -1):
            problems = self.siteProblems[site]

        for problem in range(0, len(problems)):
            newSeverity = problems[problem].severity
            if (newSeverity > severity):
                severity = newSeverity

        return severity

    def messageForSite(self, site=-1):
        message = "OK"
        problems = self.globalProblems
        if (site != -1):
            problems = self.siteProblems[site]

        for problem in range(0, len(problems)):
            if (message == "OK"):
                message = problems[problem].name
            else:
                message = message + "|" + problems[problem].name

        return message


    def calculateStats(self):
        nSites = len(self.data["columnNames"])
        self.stats = [0, 0, 0, 0]
        for row in range(0, nSites):
            for column in range(0, nSites):
                if (row != column):
                    status = self.data["grid"][row][column][0]["status"]
                    self.stats[status] += 1
                    status = self.data["grid"][row][column][1]["status"]
                    self.stats[status] += 1
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

    def findProblems(self):
        if (matchAllSites(self.data, 3)):
            self.addProblem("All grid down", 2)

        if (matchAllSites(self.data, 0)):
            self.addProblem("All is well", 0)

        nSites = len(self.data['columnNames'])
        for site in range(0, nSites):
            if (matchSite(self.data, site, 3)):
                self.addProblem("Site is down", 2, site)
            elif (matchInitiatedBySite(self.data, site, 3)):
                self.addProblem("Site can't test", 2, site)
            elif (matchInitiatedOnSite(self.data, site, 3)):
                self.addProblem("Site can't be tested", 2, site)
            elif (matchInitiatedBySite(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't test", 2, site)
            elif (matchInitiatedOnSite(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't be tested", 2, site)
            else:
                self.addProblem("OK - No match", 0, site)


    def checkMkReport(self, testGroup, out):
        out.write(str(self.maxSeverityForSite()) + " " + testGroup + "_global " +
                  "count_0=" + str(self.stats[0]) +
                  "|count_1=" + str(self.stats[1]) +
                  "|count_2=" + str(self.stats[2]) +
                  "|count_3=" + str(self.stats[3]) +
                  " " + self.messageForSite() + "\n")
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            out.write(str(self.maxSeverityForSite(site)) + " " + testGroup + "_" + siteName +
                      " count_0=" + str(self.sitesStats[site][0]) +
                      "|count_1=" + str(self.sitesStats[site][1]) +
                      "|count_2=" + str(self.sitesStats[site][2]) +
                      "|count_3=" + str(self.sitesStats[site][3]) +
                      " " + self.messageForSite(site) + "\n")

    def htmlReport(self, out):
        out.write("<!DOCTYPE html>\n")
        out.write("<html>\n")
        out.write("<body>\n")
        out.write("<h1>Madalert Report</h1>\n")
        out.write("<h2>" + self.data["name"] + "</h2>\n")
        out.write("<h2>Problems found:</h2>\n")
        if (self.maxSeverityForSite() != 0):
            out.write("<p><b>Global</b> - " + self.messageForSite() + "</p>\n")
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            if (self.maxSeverityForSite(site)):
                out.write("<p><b>" + siteName + "</b> - " + self.messageForSite(site) + "</p>\n")

        out.write("</body>\n")
        out.write("</html>\n")
