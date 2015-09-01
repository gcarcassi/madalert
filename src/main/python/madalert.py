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


def for_incoming_from_site(site, data, matcher):
    n_sites = len(data['columnNames'])
    matcher.prepare(data)
    for row in range(0, n_sites):
        if row != site:
            matcher.match(row, site, 1, data["grid"][row][site][1]["status"])
            matcher.match(row, site, 0, data["grid"][row][site][0]["status"])

    return matcher.get_result()


def for_outgoing_from_site(site, data, matcher):
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
    # INFRASTRUCTURE or ACTUAL
    category = ""


class Report:
    def __init__(self, path):
        self.path = path
        self.data = retrievegrid(path)
        self.stats = for_all_sites_in(self.data, CalculateStatistics())
        self.globalProblems = []
        self.siteProblems = []
        nSites = len(self.data["columnNames"])
        for row in range(0, nSites):
            self.siteProblems.append([])

    def addProblem(self, name, severity, category, site=-1):
        problem = Problem()
        problem.name = name
        problem.severity = severity
        problem.category = category

        if (site == -1):
            self.globalProblems.append(problem)
        else:
            self.siteProblems[site].append(problem)

    def maxSeverityForSite(self, site=-1, category=""):
        severity = 0
        problems = self.globalProblems
        if (site != -1):
            problems = self.siteProblems[site]

        for problem in range(0, len(problems)):
            if category == "" or category == problems[problem].category:
                newSeverity = problems[problem].severity
                if (newSeverity > severity):
                    severity = newSeverity

        return severity

    def messageForSite(self, site=-1, category=""):
        message = "OK - No match"
        problems = self.globalProblems
        if (site != -1):
            problems = self.siteProblems[site]

        for problem in range(0, len(problems)):
            if category == "" or category == problems[problem].category:
                if (message == "OK - No match"):
                    message = problems[problem].name
                else:
                    message = message + "|" + problems[problem].name

        return message

    def findProblems(self):
        if (match_all_sites(self.data, 3)):
            self.addProblem("All grid down", 2, "INFRASTRUCTURE")
            return

        if (match_all_sites(self.data, 0)):
            self.addProblem("All is well", 0, "INFRASTRUCTURE")

        nSites = len(self.data['columnNames'])
        for site in range(0, nSites):
            if (match_site(self.data, site, 3)):
                self.addProblem("Site is down", 2, "INFRASTRUCTURE", site)
            elif (match_initiated_by_site(self.data, site, 3)):
                self.addProblem("Site can't test", 2, "INFRASTRUCTURE", site)
            elif (match_initiated_on_site(self.data, site, 3)):
                self.addProblem("Site can't be tested", 2, "INFRASTRUCTURE", site)
            elif (match_initiated_by_site(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't test", 2, "INFRASTRUCTURE", site)
            elif (match_initiated_on_site(self.data, site, 3, 0.70)):
                self.addProblem("Site mostly can't be tested", 2, "INFRASTRUCTURE", site)
            else:
                if for_outgoing_from_site(site, self.data, match_weighted_status([0, 0.5, 1.0, -1.0], 0.7)):
                    self.addProblem("Outgoing tests failure (" + self.data["statusLabels"][2] + ")", 2, "ACTUAL", site)
                if for_incoming_from_site(site, self.data, match_weighted_status([0, 0.5, 1.0, -1.0], 0.7)):
                    self.addProblem("Incoming tests failure (" + self.data["statusLabels"][2] + ")", 2, "ACTUAL", site)


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

    def jsonReport(self, out):
        jsonReport = {"sites" : {}}
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            newSite = {"stats": self.stats.site[site], "severity": self.maxSeverityForSite(site)}
            problems = self.siteProblems[site]
            if (len(problems) > 0):
                newSiteProblems = []
                for problem in range(0, len(problems)):
                    newSiteProblems.append({"name": problems[problem].name, "severity": problems[problem].severity, "category": problems[problem].category})
                newSite["problems"] = newSiteProblems

            jsonReport["sites"][siteName] = newSite
        json.dump(jsonReport, out)

    def htmlReport(self, out):

        out.write("""<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Madalert report</title>

        <!-- Bootstrap -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">

    </head>

    <body style="padding-top: 50px">
        <!-- Navigation bar -->
        <!-- ============== -->
        <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand">Madalert</a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a><i class="fa fa-check-square-o  fa-fw"></i>&nbsp; Report</a></li>
                        <li><a href="https://github.com/gcarcassi/madalert"><i class="fa fa-github fa-fw"></i>&nbsp; GitHub</a></li>
                        <li><a href="https://github.com/gcarcassi/madalert/wiki"><i class="fa fa-book fa-fw"></i>&nbsp; Wiki</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </div>


        <div class="featurette gray">
            <div class="container">
                <div class="row">
                    <div class="col-md-12">
                        <h1>Madalert Report</h1>
""")
        out.write("                        <p>Mesh name: " + self.data["name"] + "</p>\n")
        out.write("                        <p>Mesh location: <a href=\"" + self.path + "\">"
                  + self.path + "</a></p>\n")
        out.write("""                    </div>
                    <div class="col-md-6">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><b>Infrastructure problems</b></h3>
                            </div>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Site</th>
                                        <th>Description</th>
                                    </tr>
                                </thead>
                                <tbody>
""")

        if (self.maxSeverityForSite(category="INFRASTRUCTURE") != 0):
            out.write("                                    <tr><td><b>Global (all)</b></td><td>" + self.messageForSite(-1, "INFRASTRUCTURE") + "</td></tr>\n")
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            if (self.maxSeverityForSite(site, "INFRASTRUCTURE")):
                out.write("                                    <tr><td><b>" + siteName + "</b></td><td>" + self.messageForSite(site, "INFRASTRUCTURE") + "</td></tr>\n")

        out.write("""                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><b>Test failures</b></h3>
                            </div>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Site</th>
                                        <th>Description</th>
                                    </tr>
                                </thead>
                                <tbody>
""")

        if (self.maxSeverityForSite(category="ACTUAL") != 0):
            out.write("                                    <tr><td><b>Global (all)</b></td><td>" + self.messageForSite(-1, "ACTUAL") + "</td></tr>\n")
        nSites = len(self.data["columnNames"])
        for site in range(0, nSites):
            siteName = self.data["columnNames"][site].replace(" ", "_")
            if (self.maxSeverityForSite(site, "ACTUAL")):
                out.write("                                    <tr><td><b>" + siteName + "</b></td><td>" + self.messageForSite(site, "ACTUAL") + "</td></tr>\n")

        out.write("""                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Placed at the end of the document so the pages load faster -->
        <!-- JQuery -->
        <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
        <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
        <!-- Bootstrap -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    </body>
</html>
""")
