from mockito import mock, verify
import unittest
import sys

from madalert import *


class CheckMkReportTest(unittest.TestCase):
    def test_siteStats(self):
        report = Report("src/unittest/resources/allMissing.json")
        out = mock()
        report.checkMkReport("madalert_latency", out)
        verify(out).write("0 madalert_latency_global count_0=0|count_1=0|count_2=0|count_3=364 OK - No match\n")
        verify(out).write("0 madalert_latency_CERN-PROD_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_FZK-LCG2_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_IN2P3-CC_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_INFN-T1_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_KR-KISTI-GSDC-01_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_NDGF-T1_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_RAL-LCG2_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_RRC-KI-T1_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_SARA-MATRIX_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_TRIUMF-LCG2_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_Taiwan-LCG2_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_US-FNAL_LT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_lhcperfmon-bnl count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")
        verify(out).write("0 madalert_latency_pic_LAT count_0=0|count_1=0|count_2=0|count_3=52 OK - No match\n")

    def test_problems(self):
        report = Report("src/unittest/resources/allMissing.json")
        report.addProblem("Minor", 1)
        report.addProblem("Minor", 1, 0)
        report.addProblem("Major", 2, 0)
        self.assertEquals(report.maxSeverityForSite(), 1)
        self.assertEquals(report.messageForSite(), "Minor")
        self.assertEquals(report.maxSeverityForSite(0), 2)
        self.assertEquals(report.messageForSite(0), "Minor|Major")
        self.assertEquals(report.maxSeverityForSite(1), 0)
        self.assertEquals(report.messageForSite(1), "OK - No match")
        

