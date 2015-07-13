from mockito import mock, verify
import unittest
import sys

from madalert import *


class CheckMkReportTest(unittest.TestCase):
    def test_siteStats(self):
        report = Report("src/unittest/resources/allMissing.json")
        out = mock()
        report.checkMkReport("madalert_latency", out)
        verify(out).write("0 madalert_latency_CERN-PROD_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_FZK-LCG2_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_IN2P3-CC_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_INFN-T1_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_KR-KISTI-GSDC-01_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_NDGF-T1_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_RAL-LCG2_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_RRC-KI-T1_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_SARA-MATRIX_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_TRIUMF-LCG2_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_Taiwan-LCG2_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_US-FNAL_LTcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_lhcperfmon-bnlcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
        verify(out).write("0 madalert_latency_pic_LATcount_0=0|count_1=0|count_2=0|count_3=52 OK\n")
