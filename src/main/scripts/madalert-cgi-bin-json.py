#!/usr/bin/env python
import sys
from madalert import *
import cgi
import cgitb; cgitb.enable()  # for troubleshooting

form = cgi.FieldStorage()
url = form.getvalue("json", "None")

print "Content-type: application/json"
print

if (url != "None"):
    report = Report(url)
    report.findProblems()
    report.jsonReport(sys.stdout)
else:
    print """{ "error" : "Missing parameter 'json' containing the URL for the mesh data." }
"""
