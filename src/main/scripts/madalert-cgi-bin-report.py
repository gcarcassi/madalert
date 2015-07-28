#!/usr/bin/env python
import sys
from madalert import *
import cgi
import cgitb; cgitb.enable()  # for troubleshooting

form = cgi.FieldStorage()
url = form.getvalue("json", "None")

print "Content-type: text/html"
print

if (url != "None"):
    report = Report(url)
    report.findProblems()
    report.htmlReport(sys.stdout)
else:
    print """<html>
<body>
  <h1>Madalert Report</h1>
  <p>Missing parameter 'json' containing the URL for the mesh data.</p>
</body>
</html>
"""
