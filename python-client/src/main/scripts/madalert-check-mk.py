#!/usr/bin/env python
import sys
from madalert import *

url = sys.argv[1]

report = Report(url)
report.findProblems()
report.checkMkReport("madalert_latency", sys.stdout)
