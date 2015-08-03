import sys
import json, urllib


class MatchStatus:
    def __init__(self, status):
        self.result = True
        self.status = status

    def prepare(self, data):
        return

    def match(self, row, column, half, status):
        if self.status != status:
            self.result = False

    def get_result(self):
        return self.result


class MatchStatusThreshold:
    def __init__(self, status, threshold=1.0):
        self.status = status
        self.matches = 0.0
        self.total = 0.0
        self.threshold = threshold

    def prepare(self, data):
        return

    def match(self, row, column, half, status):
        self.total += 1.0
        if self.status == status:
            self.matches += 1.0

    def get_result(self):
        return (self.matches / self.total) >= self.threshold


class MatchWeightedStatus:
    def __init__(self, weights, threshold=1.0):
        self.weights = weights
        self.matches = 0.0
        self.total = 0.0
        self.threshold = threshold

    def prepare(self, data):
        return

    def match(self, row, column, half, status):
        weight = self.weights[status]
        if 0.0 <= weight <= 1.0:
            self.total += 1.0
            self.matches += weight

    def get_result(self):
        return (self.matches / self.total) >= self.threshold


class MadStatistics:
    def __init__(self, n_sites):
        self.total = [0, 0, 0, 0]
        self.site = []
        for site in range(0, n_sites):
            self.site.append([0, 0, 0, 0])


class CalculateStatistics:
    def __init__(self):
        self.stats = None

    def prepare(self, data):
        self.stats = MadStatistics(len(data['columnNames']))

    def match(self, row, column, half, status):
        self.stats.total[status] += 1
        self.stats.site[row][status] += 1
        self.stats.site[column][status] += 1

    def get_result(self):
        return self.stats


def for_all_sites_in(data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for column in range(0, n_sites):
        for row in range(0, n_sites):
            if column != row:
                matcher.match(row, column, 0, data["grid"][row][column][0]["status"])
                matcher.match(row, column, 1, data["grid"][row][column][1]["status"])

    return matcher.get_result()


def for_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for column in range(0, n_sites):
        for row in range(0, n_sites):
            if column != row and (column == site or row == site):
                matcher.match(row, column, 0, data["grid"][row][column][0]["status"])
                matcher.match(row, column, 1, data["grid"][row][column][1]["status"])

    return matcher.get_result()


def for_initiated_by_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for column in range(0, n_sites):
        for row in range(0, n_sites):
            if column != row:
                if column == site:
                    matcher.match(row, column, 1, data["grid"][row][column][1]["status"])
                if row == site:
                    matcher.match(row, column, 0, data["grid"][row][column][0]["status"])

    return matcher.get_result()


def for_initiated_on_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for column in range(0, n_sites):
        for row in range(0, n_sites):
            if column != row:
                if column == site:
                    matcher.match(row, column, 0, data["grid"][row][column][0]["status"])
                if row == site:
                    matcher.match(row, column, 1, data["grid"][row][column][1]["status"])

    return matcher.get_result()


def for_outgoing_from_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for row in range(0, n_sites):
        if row != site:
            matcher.match(row, site, 1, data["grid"][row][site][1]["status"])
            matcher.match(row, site, 0, data["grid"][row][site][0]["status"])

    return matcher.get_result()


def for_incoming_from_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for column in range(0, n_sites):
        if column != site:
            matcher.match(site, column, 1, data["grid"][site][column][1]["status"])
            matcher.match(site, column, 0, data["grid"][site][column][0]["status"])

    return matcher.get_result()


def match_status(status, threshold=1.0):
    return MatchStatusThreshold(status, threshold)


# Each weight should be either -1.0 (excludeo from count)
# or 0.0 to 1.0
def match_weighted_status(weights, threshold=1.0):
    return MatchWeightedStatus(weights, threshold)


def retrievegrid(url):
    resp = urllib.urlopen(url)
    return json.load(resp)


def match_all_sites(data, status):
    return for_all_sites_in(data, match_status(status))


def match_site(data, site, status):
    return for_site(site, data, match_status(status))


def match_initiated_by_site(data, site, status, threshold=1.0):
    return for_initiated_by_site(site, data, match_status(status, threshold))


def match_initiated_on_site(data, site, status, threshold=1.0):
    return for_initiated_on_site(site, data, match_status(status, threshold))


class Problem:
    name = ""
    # 0 for NONE, 1 for MINOR, 2 for MAJOR
    severity = 0


class Report:
    def __init__(self, path):
        self.data = retrievegrid(path)
        self.stats = for_all_sites_in(self.data, CalculateStatistics())
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
        message = "OK - No match"
        problems = self.globalProblems
        if (site != -1):
            problems = self.siteProblems[site]

        for problem in range(0, len(problems)):
            if (message == "OK - No match"):
                message = problems[problem].name
            else:
                message = message + "|" + problems[problem].name

        return message

    def findProblems(self):
        if (match_all_sites(self.data, 3)):
            self.addProblem("All grid down", 2)

        if (match_all_sites(self.data, 0)):
            self.addProblem("All is well", 0)

        nSites = len(self.data['columnNames'])
        for site in range(0, nSites):
            if (match_site(self.data, site, 3)):
                self.addProblem("Site is down", 2, site)
            elif (match_initiated_by_site(self.data, site, 3)):
                self.addProblem("Site can't test", 2, site)
            elif (match_initiated_on_site(self.data, site, 3)):
                self.addProblem("Site can't be tested", 2, site)
            elif (match_initiated_by_site(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't test", 2, site)
            elif (match_initiated_on_site(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't be tested", 2, site)
            else:
                if for_outgoing_from_site(site, self.data, match_weighted_status([0, 0.5, 1.0, -1.0], 0.7)):
                    self.addProblem("Outgoing problem at site", 2, site)
                if for_incoming_from_site(site, self.data, match_weighted_status([0, 0.5, 1.0, -1.0], 0.7)):
                    self.addProblem("Incoming problem at site", 2, site)


    def checkMkReport(self, testGroup, out):
        out.write(str(self.maxSeverityForSite()) + " " + testGroup + "_global " +
                  "count_0=" + str(self.stats.total[0]) +
                  "|count_1=" + str(self.stats.total[1]) +
                  "|count_2=" + str(self.stats.total[2]) +
                  "|count_3=" + str(self.stats.total[3]) +
                  " " + self.messageForSite() + "\n")
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            out.write(str(self.maxSeverityForSite(site)) + " " + testGroup + "_" + siteName +
                      " count_0=" + str(self.stats.site[site][0]) +
                      "|count_1=" + str(self.stats.site[site][1]) +
                      "|count_2=" + str(self.stats.site[site][2]) +
                      "|count_3=" + str(self.stats.site[site][3]) +
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
